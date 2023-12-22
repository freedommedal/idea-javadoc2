package com.sgota.plugin.idea.javadoc2.utils;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.javadoc.PsiDocMethodOrFieldRef;
import com.intellij.psi.impl.source.javadoc.PsiDocParamRef;
import com.intellij.psi.impl.source.javadoc.PsiDocTagValueImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.sgota.plugin.idea.javadoc2.model.JavaDoc;
import com.sgota.plugin.idea.javadoc2.model.JavaDocTag;
import com.sgota.plugin.idea.javadoc2.model.JavaDocType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * The type Java doc utils.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocUtils {

    private static final List<String> MERGE_TAG_NAMES = Arrays.asList("param", "throws");

    /**
     * createJavaDoc
     *
     * @param psiDocComment psiDocComment
     * @param javaDocType   javaDocType
     * @return javadoc
     */
    public static JavaDoc createJavaDoc(PsiDocComment psiDocComment, JavaDocType javaDocType) {
        List<String> descriptions = findDescriptions(psiDocComment);
        Map<String, List<JavaDocTag>> tags = findTags(psiDocComment);
        return new JavaDoc(descriptions, tags, javaDocType);
    }

    /**
     * Find java doc description.
     *
     * @param psiDocComment the Doc comment
     * @return the description
     */
    private static List<String> findDescriptions(PsiDocComment psiDocComment) {
        List<String> descriptions = new ArrayList<>();
        PsiElement[] psiElements = psiDocComment.getDescriptionElements();
        for (PsiElement psiElement : psiElements) {
            descriptions.add(psiElement.getText());
        }
        return descriptions;
    }

    /**
     * Find doc tags.
     *
     * @param psiDocComment the Doc comment
     * @return the javadoc tags
     */
    private static Map<String, List<JavaDocTag>> findTags(PsiDocComment psiDocComment) {
        Map<String, List<JavaDocTag>> tagMap = new LinkedHashMap<>();
        PsiDocTag[] psiDocTags = psiDocComment.getTags();
        for (PsiDocTag psiDocTag : psiDocTags) {
            String name = psiDocTag.getName();
            if (!tagMap.containsKey(name)) {
                tagMap.put(name, new ArrayList<>());
            }
            tagMap.get(name).add(createJavaDocTag(psiDocTag));
        }
        return tagMap;
    }

    /**
     * Creates the java doc tag.
     *
     * @param psiDocTag the Doc tag
     * @return the Java doc tag
     */
    private static JavaDocTag createJavaDocTag(PsiDocTag psiDocTag) {
        String docTagRefParam = findDocTagRefParam(psiDocTag);
        String docTagValue = findDocTagValue(psiDocTag);
        List<String> docTagDescription = findDocTagDescription(psiDocTag, docTagRefParam, docTagValue);
        return new JavaDocTag(docTagRefParam, docTagValue, docTagDescription);
    }

    /**
     * Find doc tag ref param.
     *
     * @param psiDocTag the Doc tag
     * @return the javadoc's tag ref parameter
     */
    private static String findDocTagRefParam(PsiDocTag psiDocTag) {
        for (PsiElement psiElement : psiDocTag.getDataElements()) {
            if (psiElement instanceof PsiDocParamRef || psiElement instanceof PsiDocMethodOrFieldRef) {
                return psiElement.getText();
            }
        }
        return null;
    }

    /**
     * Find doc tag value.
     *
     * @param psiDocTag the Doc tag
     * @return the javadoc's tag value
     */
    private static String findDocTagValue(PsiDocTag psiDocTag) {
        for (PsiElement psiElement : psiDocTag.getDataElements()) {
            if (psiElement instanceof PsiDocTagValue) {
                return psiElement.getText();
            }
        }
        return null;
    }

    /**
     * Find doc tag description.
     *
     * @param psiDocTag      the Doc tag
     * @param docTagRefParam the doc tag ref param
     * @param docTagValue    the doc tag value
     * @return the javadoc's tag descriptions
     */
    private static List<String> findDocTagDescription(PsiDocTag psiDocTag, String docTagRefParam, String docTagValue) {
        List<String> descriptions = new ArrayList<>();
        List<PsiElement> psiElementList = new ArrayList<>(Arrays.asList(psiDocTag.getDataElements()));
        for (Iterator<PsiElement> iterator = psiElementList.iterator(); iterator.hasNext(); ) {
            PsiElement psiElement = iterator.next();
            removeValueIfAssignableType(docTagRefParam, PsiDocParamRef.class, iterator, psiElement);
            removeValueIfAssignableType(docTagValue, PsiDocTagValueImpl.class, iterator, psiElement);
        }
        for (PsiElement psiElement : psiElementList) {
            descriptions.add(psiElement.getText());
        }
        return descriptions;
    }

    private static void removeValueIfAssignableType(String value, Class<? extends PsiElement> valueType, Iterator<PsiElement> iterator, PsiElement psiElement) {
        if (psiElement.getClass().isAssignableFrom(valueType) && psiElement.getText().equals(value)) {
            iterator.remove();
        }
    }

    /**
     * Merge java docs.
     *
     * @param oldJavaDoc the Old java doc
     * @param newJavaDoc the New java doc
     * @return theJava doc
     */
    public static JavaDoc mergeJavaDocs(JavaDoc oldJavaDoc, JavaDoc newJavaDoc) {
        List<String> descriptions = oldJavaDoc.getDescriptions();
        if (descriptionIsEmpty(descriptions)) {
            descriptions = newJavaDoc.getDescriptions();
        }
        Map<String, List<JavaDocTag>> oldTags = oldJavaDoc.getTags();
        Map<String, List<JavaDocTag>> newTags = newJavaDoc.getTags();
        Map<String, List<JavaDocTag>> tags = new LinkedHashMap<>();
        List<String> processedTagNames = new ArrayList<>();
        for (Entry<String, List<JavaDocTag>> newTagsEntry : newTags.entrySet()) {
            String name = newTagsEntry.getKey();
            if (!tags.containsKey(name)) {
                tags.put(name, new ArrayList<>());
            }
            List<JavaDocTag> tagsEntry = newTagsEntry.getValue();
            for (JavaDocTag tag : tagsEntry) {
                if (oldTags.containsKey(name)) {
                    List<JavaDocTag> oldTagsEntry = oldTags.get(name);
                    JavaDocTag oldTag;
                    if (!MERGE_TAG_NAMES.contains(name)) {
                        oldTag = oldTagsEntry.get(0);
                    } else {
                        oldTag = findOldTag(oldTagsEntry, tag.getValue(), tag.getRefParam());
                    }
                    if (oldTag != null) {
                        tags.get(name).add(mergeJavaDocTag(oldTag, tag));
                    } else {
                        tags.get(name).add(tag);
                    }
                } else {
                    tags.get(name).add(tag);
                }
            }
            processedTagNames.add(name);
        }
        for (Entry<String, List<JavaDocTag>> entry : oldTags.entrySet()) {
            String name = entry.getKey();
            // 除了param、throws、return之外的其他自定义tag，保留
            processedTagNames.addAll(MERGE_TAG_NAMES);
            processedTagNames.add("return");
            if (!processedTagNames.contains(name)) {
                tags.put(name, entry.getValue());
            }
        }
        return new JavaDoc(descriptions, tags, oldJavaDoc.getDocType() != null ? oldJavaDoc.getDocType() : newJavaDoc.getDocType());
    }

    /**
     * Merge java doc tag.
     *
     * @param oldJavaDocTag the Old java doc tag
     * @param newJavaDocTag the New java doc tag
     * @return the Java doc tag
     */
    public static JavaDocTag mergeJavaDocTag(JavaDocTag oldJavaDocTag, JavaDocTag newJavaDocTag) {
        List<String> description = oldJavaDocTag.getDescription();
        if (descriptionIsEmpty(description)) {
            description = newJavaDocTag.getDescription();
        }
        return new JavaDocTag(oldJavaDocTag.getRefParam(), oldJavaDocTag.getValue(), description);
    }

    private static JavaDocTag findOldTag(List<JavaDocTag> oldTagsEntry, String value, String refParam) {
        JavaDocTag result = null;
        for (JavaDocTag oldTag : oldTagsEntry) {
            if (StringUtils.equals(oldTag.getValue(), value) && StringUtils.equals(oldTag.getRefParam(), refParam)) {
                result = oldTag;
                break;
            }
        }
        return result;
    }

    private static boolean descriptionIsEmpty(List<String> descriptions) {
        boolean result = true;
        if (!CollectionUtils.isEmpty(descriptions)) {
            for (String item : descriptions) {
                result = result && StringUtils.isBlank(item);
            }
        }
        return result;
    }
}
