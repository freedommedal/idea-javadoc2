package com.sgota.plugin.idea.javadoc2.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.javadoc.PsiDocComment;
import com.sgota.plugin.idea.javadoc2.model.JavaDoc;
import com.sgota.plugin.idea.javadoc2.model.JavaDocType;
import com.sgota.plugin.idea.javadoc2.service.JavaDocSettingService;
import com.sgota.plugin.idea.javadoc2.service.JavaDocTemplateService;
import com.sgota.plugin.idea.javadoc2.utils.JavaDocBuilder;
import com.sgota.plugin.idea.javadoc2.utils.JavaDocUtils;

/**
 * The type Abstract java doc generator.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public abstract class AbstractJavaDocGenerator implements JavaDocGenerator {

    protected final JavaDocSettingService settingService;
    protected final JavaDocTemplateService templateService;
    protected final PsiElementFactory psiElementFactory;

    protected final Project project;

    public AbstractJavaDocGenerator(Project project) {
        this.project = project;
        settingService = ApplicationManager.getApplication().getService(JavaDocSettingService.class);
        templateService = ApplicationManager.getApplication().getService(JavaDocTemplateService.class);
        psiElementFactory = PsiElementFactory.getInstance(project);
    }

    public PsiDocComment generate(PsiElement psiElement) {
        PsiDocComment result = null;
        JavaDoc newJavaDoc = createJavaDoc(psiElement);
        if (newJavaDoc != null) {
            PsiElement firstElement = psiElement.getFirstChild();
            if (firstElement instanceof PsiDocComment) {
                PsiDocComment oldDocComment = (PsiDocComment) firstElement;
                JavaDoc oldJavaDoc = JavaDocUtils.createJavaDoc(oldDocComment, newJavaDoc.getDocType());
                newJavaDoc = JavaDocUtils.mergeJavaDocs(oldJavaDoc, newJavaDoc);
            }
            JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
            String docText = javaDocBuilder.createDefaultJavaDoc(newJavaDoc).build();
            result = psiElementFactory.createDocCommentFromText(docText);
        }
        return result;
    }

    public Project getProject() {
        return project;
    }

    protected abstract JavaDoc createJavaDoc(PsiElement psiElement);
}
