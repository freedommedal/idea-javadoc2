package com.sgota.plugin.idea.javadoc2.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sgota.plugin.idea.javadoc2.service.JavaDocWriterService;

/**
 * The type Java doc remove action.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocRemoveAction extends JavaDocGenerateAction {

    @Override
    protected void processElement(Project project,PsiElement psiElement) {
        JavaDocWriterService docWriterService = ApplicationManager.getApplication().getService(JavaDocWriterService.class);
        docWriterService.remove(project,psiElement);
    }
}
