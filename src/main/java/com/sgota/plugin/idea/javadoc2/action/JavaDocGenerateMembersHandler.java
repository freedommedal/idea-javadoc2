package com.sgota.plugin.idea.javadoc2.action;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.GenerateMembersHandlerBase;
import com.intellij.codeInsight.generation.GenerationInfo;
import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

/**
 * The type Java doc handler.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocGenerateMembersHandler extends GenerateMembersHandlerBase {

    public JavaDocGenerateMembersHandler() {
        super("");
    }

    @Override
    protected ClassMember[] getAllOriginalMembers(PsiClass psiClass) {
        return new ClassMember[0];
    }

    @Override
    protected GenerationInfo[] generateMemberPrototypes(PsiClass psiClass, ClassMember classMember) throws IncorrectOperationException {
        return new GenerationInfo[0];
    }

    @Override
    public boolean isAvailableForQuickList(Editor editor, PsiFile psiFile, DataContext dataContext) {
        PsiClass psiClass = OverrideImplementUtil.getContextClass(psiFile.getProject(), editor, psiFile, true);
        return psiClass != null && hasMembers(psiClass);
    }
}
