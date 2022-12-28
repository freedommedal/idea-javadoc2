package com.sgota.plugin.idea.javadoc2.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.ReadonlyStatusHandler.OperationStatus;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiEditorUtil;
import com.intellij.util.ThrowableRunnable;
import com.sgota.plugin.idea.javadoc2.exception.GenerateException;

import java.util.Collections;

/**
 * The type Java doc writer impl.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocWriterService {
    public static final String WRITE_COMMAND_GROUP = "com.sgota.plugin.idea.javadoc2";
    public static final String WRITE_COMMAND_NAME = "JavaDocWriter";

    public void write(Project project, PsiDocComment psiDocComment, PsiElement psiElement) {
        try {
            checkFilesAccess(psiElement);
        } catch (GenerateException e) {
            Messages.showErrorDialog("Javadocs plugin is not available, cause: " + e.getMessage(), "Javadocs plugin");
            return;
        }
        PsiFile psiFile = psiElement.getContainingFile();
        WriteCommandAction.Builder writeCommandAction = WriteCommandAction.writeCommandAction(project, psiFile).withName(WRITE_COMMAND_NAME).withGroupId(WRITE_COMMAND_GROUP);
        JavaDocCreateCommand command = new JavaDocCreateCommand(psiDocComment, psiElement);
        try {
            writeCommandAction.run(command);
        } catch (Throwable e) {
            Messages.showErrorDialog("Javadocs plugin is not available, cause: " + e.getMessage(), "Javadocs plugin");
        }
    }

    public void remove(Project project, PsiElement psiElement) {
        try {
            checkFilesAccess(psiElement);
        } catch (GenerateException e) {
            Messages.showErrorDialog("Javadocs plugin is not available, cause: " + e.getMessage(), "Javadocs plugin");
            return;
        }
        PsiFile psiFile = psiElement.getContainingFile();
        WriteCommandAction.Builder writeCommandAction = WriteCommandAction.writeCommandAction(project, psiFile).withName(WRITE_COMMAND_NAME).withGroupId(WRITE_COMMAND_GROUP);
        JavaDocRemoveCommand command = new JavaDocRemoveCommand(psiElement);
        try {
            writeCommandAction.run(command);
        } catch (Throwable e) {
            Messages.showErrorDialog("Javadocs plugin is not available, cause: " + e.getMessage(), "Javadocs plugin");
        }
    }

    private static void deleteJavaDoc(PsiElement psiElement) {
        pushPostponedChanges(psiElement);
        psiElement.getFirstChild().delete();
    }

    private static void addJavaDoc(PsiElement psiElement, PsiDocComment psiDocComment) {
        pushPostponedChanges(psiElement);
        psiElement.getNode().addChild(psiDocComment.getNode(), psiElement.getFirstChild().getNode());
    }

    private static void replaceJavaDoc(PsiElement psiElement, PsiDocComment psiDocComment) {
        deleteJavaDoc(psiElement);
        addJavaDoc(psiElement, psiDocComment);
    }

    private static void reformatJavaDoc(PsiElement psiElement) {
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
        pushPostponedChanges(psiElement);
        try {
            int javadocTextOffset = findJavaDocTextOffset(psiElement);
            int javaCodeTextOffset = findJavaCodeTextOffset(psiElement);
            codeStyleManager.reformatText(psiElement.getContainingFile(), javadocTextOffset, javaCodeTextOffset + 1);
        } catch (GenerateException e) {
            Messages.showErrorDialog("Could not reformat javadoc since cannot find required elements: " + e.getMessage(), "Javadocs plugin");
        }
    }

    private static int findJavaDocTextOffset(PsiElement psiElement) {
        PsiElement docElement = psiElement.getFirstChild();
        if (!(docElement instanceof PsiDocComment)) {
            throw new GenerateException("Cannot find element of type PsiDocComment");
        }
        return docElement.getTextOffset();
    }

    private static int findJavaCodeTextOffset(PsiElement psiElement) {
        if (psiElement.getChildren().length < 2) {
            throw new GenerateException("Can not find offset of java code");
        }
        return psiElement.getChildren()[1].getTextOffset();
    }

    private static void pushPostponedChanges(PsiElement psiElement) {
        Editor editor = PsiEditorUtil.findEditor(psiElement.getContainingFile());
        if (editor != null) {
            PsiDocumentManager.getInstance(psiElement.getProject()).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        }
    }

    private void checkFilesAccess(PsiElement psiElement) {
        PsiFile psiFile = psiElement.getContainingFile();
        if (psiFile == null || !psiFile.isValid()) {
            throw new GenerateException("File cannot be used to generate javadocs");
        }
        OperationStatus status = ReadonlyStatusHandler.getInstance(psiElement.getProject()).ensureFilesWritable(Collections.singletonList(psiFile.getVirtualFile()));
        if (status.hasReadonlyFiles()) {
            throw new GenerateException(status.getReadonlyFilesMessage());
        }
    }

    private static class JavaDocCreateCommand implements ThrowableRunnable<Throwable> {
        private final PsiElement psiElement;
        private final PsiDocComment psiDocComment;

        protected JavaDocCreateCommand(PsiDocComment psiDocComment, PsiElement psiElement) {
            this.psiDocComment = psiDocComment;
            this.psiElement = psiElement;
        }

        @Override
        public void run() {
            if (psiDocComment == null) {
                return;
            }
            if (psiElement.getFirstChild() instanceof PsiDocComment) {
                replaceJavaDoc(psiElement, psiDocComment);
            } else {
                addJavaDoc(psiElement, psiDocComment);
            }
            ensureWhitespaceAfterJavaDoc(psiElement);
            reformatJavaDoc(psiElement);
        }

        private void ensureWhitespaceAfterJavaDoc(PsiElement psiElement) {
            PsiElement firstChild = psiElement.getFirstChild();
            if (!PsiDocComment.class.isAssignableFrom(firstChild.getClass())) {
                return;
            }
            PsiElement nextElement = firstChild.getNextSibling();
            if (PsiWhiteSpace.class.isAssignableFrom(nextElement.getClass())) {
                return;
            }
            pushPostponedChanges(psiElement);
            psiElement.getNode().addChild(new PsiWhiteSpaceImpl("\n"), nextElement.getNode());
        }
    }

    private static class JavaDocRemoveCommand implements ThrowableRunnable<Throwable> {
        private final PsiElement psiElement;

        protected JavaDocRemoveCommand(PsiElement psiElement) {
            this.psiElement = psiElement;
        }

        @Override
        public void run() {
            if (psiElement.getFirstChild() instanceof PsiDocComment) {
                deleteJavaDoc(psiElement);
            }
        }
    }
}
