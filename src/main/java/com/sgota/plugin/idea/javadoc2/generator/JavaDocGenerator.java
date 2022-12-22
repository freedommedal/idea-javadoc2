package com.sgota.plugin.idea.javadoc2.generator;

import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;

/**
 * The interface Java doc generator.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public interface JavaDocGenerator {

    /**
     * Generate java docs.
     *
     * @param psiElement the Element
     * @return the Psi doc comment
     */
    PsiDocComment generate(PsiElement psiElement);

}
