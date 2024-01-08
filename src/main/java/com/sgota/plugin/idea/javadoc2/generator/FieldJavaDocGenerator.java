package com.sgota.plugin.idea.javadoc2.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import com.sgota.plugin.idea.javadoc2.model.JavaDoc;
import com.sgota.plugin.idea.javadoc2.model.JavaDocType;
import com.sgota.plugin.idea.javadoc2.service.JavaDocTemplateService;
import com.sgota.plugin.idea.javadoc2.utils.JavaDocUtils;
import org.apache.velocity.Template;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Field java doc generator.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class FieldJavaDocGenerator extends AbstractJavaDocGenerator {

    public FieldJavaDocGenerator(Project project) {
        super(project);
    }

    @Override
    protected JavaDoc createJavaDoc(PsiElement psiElement) {
        PsiField psiField = (PsiField) psiElement;
        JavaDocTemplateService templateService = ApplicationManager.getApplication().getService(JavaDocTemplateService.class);
        Template template = templateService.getFieldTemplate(psiField);
        Map<String, Object> model = new HashMap<>();
        model.put("fieldName", psiField.getName());
        String docText = templateService.mergeToString(template, model, this.getProject());
        PsiDocComment psiDocComment = psiElementFactory.createDocCommentFromText(docText);
        return JavaDocUtils.createJavaDoc(psiDocComment, JavaDocType.FIELD);
    }
}
