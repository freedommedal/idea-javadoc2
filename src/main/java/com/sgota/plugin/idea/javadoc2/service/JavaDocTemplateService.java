package com.sgota.plugin.idea.javadoc2.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ClassLoaderUtil;
import com.intellij.psi.*;
import com.sgota.plugin.idea.javadoc2.exception.GenerateException;
import com.sgota.plugin.idea.javadoc2.model.setting.JavaDocSetting;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;

import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The type Doc template manager impl.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocTemplateService {

    private Map<String, Template> classTemplateCache = new LinkedHashMap<>();
    private Map<String, Template> fieldTemplateCache = new LinkedHashMap<>();
    private Map<String, Template> methodTemplateCache = new LinkedHashMap<>();
    private Map<String, Template> constructorTemplateCache = new LinkedHashMap<>();

    private final VelocityEngine velocityEngine;

    public JavaDocTemplateService() {
        Properties properties = new Properties();
        properties.put("input.encoding", "UTF-8");
        properties.put("output.encoding", "UTF-8");
        properties.put("resource.loader", "string");
        properties.put("string.resource.loader.repository.class", MyStringResourceRepository.class.getName());
        velocityEngine = new VelocityEngine(properties);
        ClassLoaderUtil.runWithClassLoader(JavaDocTemplateService.class.getClassLoader(), () -> velocityEngine.init());
    }

    public void reloadTemplateCache(JavaDocSetting javaDocSetting) {
        MyStringResourceRepository resourceRepository = (MyStringResourceRepository) StringResourceLoader.getRepository();
        resourceRepository.clear();
        classTemplateCache = createTemplateCache(javaDocSetting.getClassTemplates(), "class");
        constructorTemplateCache = createTemplateCache(javaDocSetting.getConstructorTemplates(), "constructor");
        methodTemplateCache = createTemplateCache(javaDocSetting.getMethodTemplates(), "method");
        fieldTemplateCache = createTemplateCache(javaDocSetting.getFieldTemplates(), "field");
    }

    private Map<String, Template> createTemplateCache(Map<String, String> templatModel, String cacheName) {
        Map<String, Template> templatCache = new LinkedHashMap<>();
        for (Entry<String, String> entry : templatModel.entrySet()) {
            try {
                String key = entry.getKey();
                String value = entry.getValue();
                String templateName = cacheName + "_" + key.hashCode();
                StringResourceLoader.getRepository().putStringResource(templateName, value);
                Template template = velocityEngine.getTemplate(templateName);
                templatCache.put(key, template);
            } catch (Exception e) {
                throw new GenerateException(e);
            }
        }
        return templatCache;
    }

    public Template getClassTemplate(PsiClass psiClass) {
        String classSignature = getClassSignature(psiClass);
        return getMatchingTemplate(classSignature, classTemplateCache);
    }

    public Template getFieldTemplate(PsiField psiField) {
        return getMatchingTemplate(psiField.getText(), fieldTemplateCache);
    }

    public Template getMethodTemplate(PsiMethod psiMethod) {
        Map<String, Template> templates;
        if (psiMethod.isConstructor()) {
            templates = constructorTemplateCache;
        } else {
            templates = methodTemplateCache;
        }
        String signature = psiMethod.getText();
        PsiCodeBlock methodBody = psiMethod.getBody();
        if (methodBody != null) {
            signature = signature.replace(methodBody.getText(), "");
        }
        return getMatchingTemplate(signature, templates);
    }

    private Template getMatchingTemplate(String elementText, Map<String, Template> templateMap) {
        Template result = null;
        for (Entry<String, Template> entry : templateMap.entrySet()) {
            if (Pattern.compile(entry.getKey(), Pattern.DOTALL | Pattern.MULTILINE).matcher(elementText).matches()) {
                result = entry.getValue();
                break;
            }
        }
        if (result == null) {
            throw new GenerateException(elementText);
        }
        return result;
    }

    private String getClassSignature(PsiClass psiClass) {
        StringBuilder builder = new StringBuilder();
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            builder.append(modifierList.getText());
        }
        builder.append(" ");
        if (psiClass.isInterface()) {
            builder.append("interface ");
        } else if (psiClass.isEnum()) {
            builder.append("enum ");
        } else {
            builder.append("class ");
        }
        builder.append(psiClass.getName());
        builder.append(" ");
        PsiClassType[] extendsTypes = psiClass.getExtendsListTypes();
        if (extendsTypes.length > 0) {
            builder.append("extends ");
            for (int i = 0; i < extendsTypes.length; i++) {
                PsiClassType extendsType = extendsTypes[i];
                builder.append(extendsType.getClassName());
                if (i < extendsTypes.length - 1) {
                    builder.append(",");
                }
                builder.append(" ");
            }
        }
        PsiClassType[] implementTypes = psiClass.getImplementsListTypes();
        if (implementTypes.length > 0) {
            builder.append("implements ");
            for (int i = 0; i < implementTypes.length; i++) {
                PsiClassType implementType = implementTypes[i];
                builder.append(implementType.getClassName());
                if (i < implementTypes.length - 1) {
                    builder.append(",");
                }
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public String mergeToString(Template template, Map<String, Object> model, Project project) {
        StringWriter writer = new StringWriter();
        try {
            this.putCommonVariables(model, project);
            VelocityContext velocityContext = new VelocityContext(model);
            template.merge(velocityContext, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new GenerateException(e);
        }
    }

    public void putCommonVariables(Map<String, Object> model, Project project) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTime = currentDateTime.format(formatter);
        String[] temp = dateTime.split(" ");
        String date = temp[0];
        String time = temp[1];
        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
        String week = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINESE);
        String weekEn = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String user = System.getProperty("user.name");
        String projectName = project.getName();

        model.putIfAbsent("PROJECT_NAME", projectName);
        model.putIfAbsent("USER", user);
        model.putIfAbsent("DATETIME", dateTime);
        model.putIfAbsent("DATE", date);
        model.putIfAbsent("TIME", time);
        model.putIfAbsent("WEEK", week);
        model.putIfAbsent("WEEK_EN", weekEn);
        model.putIfAbsent("YEAR", currentDateTime.getYear());
        model.putIfAbsent("MONTH", currentDateTime.getMonthValue());
        model.putIfAbsent("MONTH_EN", currentDateTime.getMonth());
        model.putIfAbsent("DAY", currentDateTime.getDayOfMonth());
        model.putIfAbsent("HOUR", currentDateTime.getHour());
        model.putIfAbsent("MINUTE", currentDateTime.getMinute());
        model.putIfAbsent("SECOND", currentDateTime.getSecond());
    }

}
