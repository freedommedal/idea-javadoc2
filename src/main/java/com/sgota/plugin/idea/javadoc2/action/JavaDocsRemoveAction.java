package com.sgota.plugin.idea.javadoc2.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sgota.plugin.idea.javadoc2.service.JavaDocWriterService;

/**
 * The type Java docs remove action.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocsRemoveAction extends JavaDocsGenerateAction {

    @Override
    protected void processElement(Project project,PsiElement psiElement) {
        JavaDocWriterService docWriterService = ApplicationManager.getApplication().getService(JavaDocWriterService.class);
        docWriterService.remove(project,psiElement);
    }
}
