package com.sgota.plugin.idea.javadoc2.action;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.sgota.plugin.idea.javadoc2.exception.GenerateException;
import com.sgota.plugin.idea.javadoc2.generator.ClassJavaDocGenerator;
import com.sgota.plugin.idea.javadoc2.generator.FieldJavaDocGenerator;
import com.sgota.plugin.idea.javadoc2.generator.JavaDocGenerator;
import com.sgota.plugin.idea.javadoc2.generator.MethodJavaDocGenerator;
import com.sgota.plugin.idea.javadoc2.service.JavaDocWriterService;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Java doc generate action.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocGenerateAction extends BaseGenerateAction {

    public JavaDocGenerateAction() {
        super(new JavaDocGenerateMembersHandler());
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.showDumbModeNotification("Javadocs plugin is not available during indexing");
            return;
        }

        Editor editor = LangDataKeys.EDITOR.getData(e.getDataContext());
        if (editor == null) {
            Messages.showErrorDialog("Javadocs plugin is not available", "Javadocs plugin");
            return;
        }
        int startPosition = editor.getSelectionModel().getSelectionStart();
        int endPosition = editor.getSelectionModel().getSelectionEnd();
        PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
        if (file == null) {
            Messages.showErrorDialog("Javadocs plugin is not available", "Javadocs plugin");
            return;
        }
        List<PsiElement> elements = new LinkedList<>();
        PsiElement element = getJavaElement(PsiUtilCore.getElementAtOffset(file, startPosition));
        do {
            if (isAllowedElementType(element)) {
                elements.add(element);
            }
            element = element.getNextSibling();
            if (element == null) {
                break;
            }
        } while (isElementInSelection(element, startPosition, endPosition));

        for (PsiElement psiElement : elements) {
            processElement(project, psiElement);
        }
    }

    protected void processElement(Project project, PsiElement psiElement) {
        JavaDocGenerator generator = getGenerator(psiElement);
        if (generator != null) {
            try {
                PsiDocComment javaDoc = generator.generate(psiElement);
                if (javaDoc != null) {
                    JavaDocWriterService writer = ApplicationManager.getApplication().getService(JavaDocWriterService.class);
                    writer.write(project, javaDoc, psiElement);
                }
            } catch (GenerateException e) {
                String message = "Javadocs plugin is not available. Can not find suitable template for the element:\n{0}";
                Messages.showWarningDialog(MessageFormat.format(message, e.getMessage()), "Javadocs plugin");
            }
        }
    }

    private JavaDocGenerator getGenerator(PsiElement element) {
        Project project = element.getProject();
        JavaDocGenerator generator = null;
        if (PsiClass.class.isAssignableFrom(element.getClass())) {
            generator = new ClassJavaDocGenerator(project);
        } else if (PsiMethod.class.isAssignableFrom(element.getClass())) {
            generator = new MethodJavaDocGenerator(project);
        } else if (PsiField.class.isAssignableFrom(element.getClass())) {
            generator = new FieldJavaDocGenerator(project);
        }
        return generator;
    }

    private PsiElement getJavaElement(PsiElement element) {
        PsiElement result = element;
        PsiField field = PsiTreeUtil.getParentOfType(element, PsiField.class);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (field != null) {
            result = field;
        } else if (method != null) {
            result = method;
        } else if (clazz != null) {
            result = clazz;
        }
        return result;
    }

    private boolean isElementInSelection(PsiElement element, int startPosition, int endPosition) {
        boolean result = false;
        int elementTextOffset = element.getTextRange().getStartOffset();
        if (elementTextOffset >= startPosition && elementTextOffset <= endPosition) {
            result = true;
        }
        return result;
    }

    private boolean isAllowedElementType(PsiElement element) {
        return element instanceof PsiClass || element instanceof PsiField || element instanceof PsiMethod;
    }

}
