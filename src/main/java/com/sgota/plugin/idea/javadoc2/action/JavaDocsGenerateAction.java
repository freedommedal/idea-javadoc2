package com.sgota.plugin.idea.javadoc2.action;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * The type Java docs generate action.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocsGenerateAction extends JavaDocGenerateAction implements DumbAware {

    @Override
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        presentation.setEnabled(false);
        DataContext dataContext = event.getDataContext();
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        final VirtualFile[] files = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);

        if (editor != null) {
            PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (file != null && JavaFileType.INSTANCE.equals(file.getFileType())) {
                presentation.setEnabled(true);
                return;
            } else if (file != null && file.isDirectory()) {
                presentation.setEnabled(true);
                return;
            }
        }
        if (containsJavaFiles(files)) {
            presentation.setEnabled(true);
        }
    }

    private boolean containsJavaFiles(VirtualFile[] files) {
        if (files == null) {
            return false;
        }
        if (files.length < 1) {
            return false;
        }
        boolean result = false;
        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                result = result || containsJavaFiles(file.getChildren());
            } else if (JavaFileType.INSTANCE.equals(file.getFileType())) {
                result = true;
            }
        }
        return result;
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

        final PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
        DataContext dataContext = e.getDataContext();

        project = CommonDataKeys.PROJECT.getData(dataContext);
        final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        final VirtualFile[] files = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);

        if (editor != null && file != null) {
            processFile(project, file);
        } else if (project != null && files != null) {
            processFiles(project, files);
        } else {
            Messages.showErrorDialog("Javadocs plugin is not available", "Javadocs plugin");
        }
    }

    private void processFiles(Project project, VirtualFile[] files) {
        for (VirtualFile virtualFile : files) {
            if (virtualFile.isDirectory()) {
                processFiles(project, virtualFile.getChildren());
            } else {
                PsiFile file = convertToPsiFile(virtualFile, project);
                processFile(project, file);
            }
        }
    }

    private PsiFile convertToPsiFile(VirtualFile file, Project project) {
        PsiManager manager = PsiManager.getInstance(project);
        return manager.findFile(file);
    }

    private void processFile(Project project, PsiFile file) {
        List<PsiClass> classElements = getClasses(file);
        List<PsiElement> elements = new LinkedList<>(classElements);
        for (PsiClass classElement : classElements) {
            elements.addAll(PsiTreeUtil.getChildrenOfTypeAsList(classElement, PsiMethod.class));
            elements.addAll(PsiTreeUtil.getChildrenOfTypeAsList(classElement, PsiField.class));
        }
        for (PsiElement element : elements) {
            processElement(project, element);
        }
    }

    private List<PsiClass> getClasses(PsiElement element) {
        List<PsiClass> classElements = PsiTreeUtil.getChildrenOfTypeAsList(element, PsiClass.class);
        List<PsiClass> elements = new LinkedList<>(classElements);
        for (PsiClass classElement : classElements) {
            elements.addAll(getClasses(classElement));
        }
        return elements;
    }
}
