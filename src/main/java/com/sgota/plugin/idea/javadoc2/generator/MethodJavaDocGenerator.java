package com.sgota.plugin.idea.javadoc2.generator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;
import com.sgota.plugin.idea.javadoc2.model.JavaDoc;
import com.sgota.plugin.idea.javadoc2.model.JavaDocType;
import com.sgota.plugin.idea.javadoc2.utils.JavaDocUtils;
import org.apache.velocity.Template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Method java doc generator.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class MethodJavaDocGenerator extends AbstractJavaDocGenerator {

    public MethodJavaDocGenerator(Project project) {
        super(project);
    }

    @Override
    protected JavaDoc createJavaDoc(PsiElement psiElement) {
        PsiMethod psiMethod = (PsiMethod) psiElement;
        if (psiMethod.findSuperMethods().length > 0) {
            return null;
        }
        Template template = templateService.getMethodTemplate(psiMethod);
        Map<String, Object> model = new HashMap<>();
        model.put("methodName", psiMethod.getName());
        PsiTypeParameter[] typeParameterArray = psiMethod.getTypeParameters();
        List<Map<String, String>> typeParameterList = Arrays.stream(typeParameterArray).map(typeParameter -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", typeParameter.getName());
            return map;
        }).collect(Collectors.toList());
        model.put("typeParameters", typeParameterList);
        PsiParameter[] parameterArray = psiMethod.getParameterList().getParameters();
        List<Map<String, String>> parameterList = Arrays.stream(parameterArray).map(parameter -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", parameter.getName());
            return map;
        }).collect(Collectors.toList());
        model.put("parameters", parameterList);
        PsiTypeElement returnTypeElement = psiMethod.getReturnTypeElement();
        if (returnTypeElement != null && !returnTypeElement.getType().isAssignableFrom(PsiType.VOID)) {
            String returnName = "data";
            PsiType returnTypeElementType = returnTypeElement.getType();
            PsiClass psiClass = PsiUtil.resolveClassInType(returnTypeElementType);
            if (psiClass != null) {
                returnName = psiClass.getName();
            }
            model.put("returnName", returnName);
        }
        PsiJavaCodeReferenceElement[] exceptionArray = psiMethod.getThrowsList().getReferenceElements();
        List<Map<String, String>> exceptionList = Arrays.stream(exceptionArray).map(referenceElement -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", referenceElement.getReferenceName());
            return map;
        }).collect(Collectors.toList());
        model.put("exceptions", exceptionList);
        String docText = templateService.mergeToString(template, model, this.getProject());
        PsiDocComment psiDocComment = psiElementFactory.createDocCommentFromText(docText);
        return JavaDocUtils.createJavaDoc(psiDocComment, JavaDocType.METHOD);
    }
}
