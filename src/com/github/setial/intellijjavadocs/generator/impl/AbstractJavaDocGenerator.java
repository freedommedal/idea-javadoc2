package com.github.setial.intellijjavadocs.generator.impl;

import com.github.setial.intellijjavadocs.configuration.JavaDocConfiguration;
import com.github.setial.intellijjavadocs.generator.JavaDocGenerator;
import com.github.setial.intellijjavadocs.model.JavaDoc;
import com.github.setial.intellijjavadocs.model.settings.JavaDocSettings;
import com.github.setial.intellijjavadocs.model.settings.Mode;
import com.github.setial.intellijjavadocs.model.settings.Visibility;
import com.github.setial.intellijjavadocs.template.DocTemplateManager;
import com.github.setial.intellijjavadocs.template.DocTemplateProcessor;
import com.github.setial.intellijjavadocs.utils.JavaDocUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.PomNamedTarget;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.javadoc.PsiDocComment;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Abstract java doc generator.
 *
 * @param <T> the type parameter
 * @author Sergey Timofiychuk
 */
public abstract class AbstractJavaDocGenerator<T extends PsiElement> implements JavaDocGenerator<T> {

    private final DocTemplateManager docTemplateManager;
    private final DocTemplateProcessor docTemplateProcessor;
    private final PsiElementFactory psiElementFactory;
    private final JavaDocConfiguration settings;

    /**
     * Instantiates a new Abstract java doc generator.
     *
     * @param project the Project
     */
    public AbstractJavaDocGenerator(@NotNull Project project) {
        this.docTemplateManager = ApplicationManager.getApplication().getComponent(DocTemplateManager.class);
        this.docTemplateProcessor = project.getComponent(DocTemplateProcessor.class);
        this.psiElementFactory = PsiElementFactory.SERVICE.getInstance(project);
        this.settings = ApplicationManager.getApplication().getComponent(JavaDocConfiguration.class);
    }

    @Nullable
    @Override
    public final PsiDocComment generate(@NotNull T element) {
        PsiDocComment result = null;
        PsiDocComment oldDocComment = null;
        PsiElement firstElement = element.getFirstChild();
        if (firstElement instanceof PsiDocComment) {
            oldDocComment = (PsiDocComment) firstElement;
        }

        JavaDocSettings configuration = this.settings.getConfiguration();
        if (configuration != null) {
            Mode mode = configuration.getGeneralSettings().getMode();
            switch (mode) {
                case KEEP:
                    if (oldDocComment != null) {
                        break;
                    }
                case REPLACE:
                    result = this.replaceJavaDocAction(element);
                    break;
                case UPDATE:
                default:
                    if (oldDocComment != null) {
                        result = this.updateJavaDocAction(element, oldDocComment);
                    } else {
                        result = this.replaceJavaDocAction(element);
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * Gets the doc template manager.
     *
     * @return the Doc template manager
     */
    @NotNull
    protected DocTemplateManager getDocTemplateManager() {
        return this.docTemplateManager;
    }

    /**
     * Gets the doc template processor.
     *
     * @return the Doc template processor
     */
    @NotNull
    protected DocTemplateProcessor getDocTemplateProcessor() {
        return this.docTemplateProcessor;
    }

    /**
     * Gets the psi element factory.
     *
     * @return the Psi element factory
     */
    @NotNull
    protected PsiElementFactory getPsiElementFactory() {
        return this.psiElementFactory;
    }

    /**
     * Gets settings.
     *
     * @return the settings
     */
    @NotNull
    protected JavaDocConfiguration getSettings() {
        return this.settings;
    }

    /**
     * Check whether javadoc should be generated.
     *
     * @param modifiers the modifiers
     * @return the boolean
     */
    protected boolean shouldGenerate(PsiModifierList modifiers) {
        return this.checkModifiers(modifiers, PsiModifier.PUBLIC, Visibility.PUBLIC) ||
               this.checkModifiers(modifiers, PsiModifier.PROTECTED, Visibility.PROTECTED) ||
               this.checkModifiers(modifiers, PsiModifier.PACKAGE_LOCAL, Visibility.DEFAULT) ||
               this.checkModifiers(modifiers, PsiModifier.PRIVATE, Visibility.PRIVATE);
    }

    /**
     * Gets default parameters used to build template.
     *
     * @param element the element
     * @return the default parameters
     */
    protected Map<String, Object> getDefaultParameters(PomNamedTarget element) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("element", element);
        params.put("name", this.getDocTemplateProcessor().buildDescription(element.getName(), true));
        params.put("partName", this.getDocTemplateProcessor().buildPartialDescription(element.getName()));
        params.put("splitNames", StringUtils.splitByCharacterTypeCamelCase(element.getName()));
        return params;
    }

    private PsiDocComment updateJavaDocAction(T element, PsiDocComment oldDocComment) {
        PsiDocComment result = null;
        JavaDoc newJavaDoc = this.generateJavaDoc(element);
        JavaDoc oldJavaDoc = JavaDocUtils.createJavaDoc(oldDocComment);
        if (newJavaDoc != null) {
            newJavaDoc = JavaDocUtils.mergeJavaDocs(oldJavaDoc, newJavaDoc);
            String javaDoc = newJavaDoc.toJavaDoc();
            result = this.psiElementFactory.createDocCommentFromText(javaDoc);
        }
        return result;
    }

    private PsiDocComment replaceJavaDocAction(T element) {
        PsiDocComment result = null;
        JavaDoc newJavaDoc = this.generateJavaDoc(element);
        if (newJavaDoc != null) {
            String javaDoc = newJavaDoc.toJavaDoc();
            result = this.psiElementFactory.createDocCommentFromText(javaDoc);
        }
        return result;
    }

    private boolean checkModifiers(PsiModifierList modifiers, String modifier, Visibility visibility) {
        JavaDocSettings configuration = this.getSettings().getConfiguration();
        return modifiers != null && modifiers.hasModifierProperty(modifier) && configuration != null &&
                configuration.getGeneralSettings().getVisibilities().contains(visibility);
    }

    /**
     * Generate java doc.
     *
     * @param element the Element
     * @return the Java doc
     */
    @Nullable
    protected abstract JavaDoc generateJavaDoc(@NotNull T element);

}
