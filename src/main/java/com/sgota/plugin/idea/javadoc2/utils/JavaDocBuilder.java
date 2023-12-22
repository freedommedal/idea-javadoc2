package com.sgota.plugin.idea.javadoc2.utils;

import com.sgota.plugin.idea.javadoc2.enums.JavaDocElementEnum;
import com.sgota.plugin.idea.javadoc2.model.JavaDoc;
import com.sgota.plugin.idea.javadoc2.model.JavaDocTag;
import com.sgota.plugin.idea.javadoc2.model.JavaDocType;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The type Java doc builder.
 *
 * @author SergeyTimofiychuk, tiankuo
 */
public class JavaDocBuilder {

    private final StringBuilder stringBuilder;

    /**
     * Instantiates a new Java doc builder.
     */
    public JavaDocBuilder() {
        stringBuilder = new StringBuilder();
    }

    /**
     * docType
     */
    private JavaDocType docType;

    /**
     * Creates the java doc by default rules.
     *
     * @param javadoc the Javadoc
     * @return the Java doc builder
     */
    public JavaDocBuilder createDefaultJavaDoc(JavaDoc javadoc) {
        this.docType = javadoc.getDocType();
        openJavaDoc();
        addDescription(javadoc.getDescriptions());
        addTags(javadoc.getTags());
        closeJavaDoc();
        return this;
    }

    /**
     * Builds the javadoc section.
     *
     * @return the String
     */
    public String build() {
        return stringBuilder.toString();
    }

    /**
     * Open the java doc section.
     *
     * @return the Java doc builder
     */
    public JavaDocBuilder openJavaDoc() {
        stringBuilder.append(JavaDocElementEnum.STARTING.getText());
        stringBuilder.append(JavaDocElementEnum.LINE_START.getText());
        return this;
    }

    /**
     * Close the java doc section.
     *
     * @return the Java doc builder
     */
    public JavaDocBuilder closeJavaDoc() {
        // add new line with asterisk if it's not new line
        if (!closeRegularJavaDoc()) {
            closeOneLineJavaDoc();
        }
        stringBuilder.append(JavaDocElementEnum.ENDING.getText());
        return this;
    }

    /**
     * Add new line to javadoc section.
     *
     * @return the Java doc builder
     */
    public JavaDocBuilder addNewLine() {
        stringBuilder.append(JavaDocElementEnum.NEW_LINE.getText());
        stringBuilder.append(JavaDocElementEnum.LINE_START.getText());
        return this;
    }

    /**
     * Add description to javadoc section.
     *
     * @param descriptions the Descriptions
     * @return the Java doc builder
     */
    public JavaDocBuilder addDescription(List<String> descriptions) {
        Iterator<String> iterator = descriptions.iterator();
        while (iterator.hasNext()) {
            String description = iterator.next();
            if (isAcceptedDescription(description, iterator.hasNext())) {
                stringBuilder.append(description);
                if (StringUtils.contains(description, JavaDocElementEnum.NEW_LINE.getText())) {
                    stringBuilder.append(JavaDocElementEnum.LINE_START.getText());
                }
            }
        }
        return this;
    }

    /**
     * Add tag description to javadoc section.
     *
     * @param descriptions the Descriptions
     * @return the Java doc builder
     */
    public JavaDocBuilder addTagDescription(List<String> descriptions) {
        for (int i = 0; i < descriptions.size(); i++) {
            String description = descriptions.get(i);
            // 如果单行tag的最后一个字符是空格，则表示后面还有下一行，进行换行处理
            if(" ".equals(description) && i == descriptions.size() - 1) {
                addNewLine();
            }
            stringBuilder.append(description);
        }
        return this;
    }

    /**
     * Add tag to javadoc section.
     *
     * @param name the Name
     * @param tag  the Tag
     * @return the Java doc builder
     */
    public JavaDocBuilder addTag(String name, JavaDocTag tag) {
        stringBuilder.append(JavaDocElementEnum.WHITE_SPACE.getText());
        stringBuilder.append(JavaDocElementEnum.TAG_START.getText());
        stringBuilder.append(name);
        stringBuilder.append(JavaDocElementEnum.WHITE_SPACE.getText());

        if (StringUtils.isNotBlank(tag.getRefParam())) {
            stringBuilder.append(tag.getRefParam());
        } else if (StringUtils.isNotBlank(tag.getValue())) {
            stringBuilder.append(tag.getValue());
        }

        // 方法注释tag和description之间有空格，类注释其间没有空格
        if(this.docType == JavaDocType.METHOD) {
            stringBuilder.append(JavaDocElementEnum.WHITE_SPACE.getText());
        }
        addTagDescription(tag.getDescription());
        return this;
    }

    /**
     * Add tags to javadoc section.
     *
     * @param tags the Tags
     * @return theJavadoc builder
     */
    public JavaDocBuilder addTags(Map<String, List<JavaDocTag>> tags) {
        Iterator<Entry<String, List<JavaDocTag>>> iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, List<JavaDocTag>> entry = iterator.next();
            String name = entry.getKey();
            Iterator<JavaDocTag> javaDocTagsIterator = entry.getValue().iterator();
            while (javaDocTagsIterator.hasNext()) {
                JavaDocTag javaDocTag = javaDocTagsIterator.next();
                addTag(name, javaDocTag);
                if (javaDocTagsIterator.hasNext()) {
                    addNewLine();
                }
            }
            if (iterator.hasNext()) {
                addNewLine();
            }
        }
        return this;
    }


    private boolean closeRegularJavaDoc() {
        boolean result = false;
        if (stringBuilder.lastIndexOf(JavaDocElementEnum.LINE_START.getText()) != stringBuilder.length() - 1 && stringBuilder.lastIndexOf(JavaDocElementEnum.NEW_LINE.getText()) >= 0) {
            stringBuilder.append(JavaDocElementEnum.NEW_LINE.getText());
            stringBuilder.append(JavaDocElementEnum.WHITE_SPACE.getText());
            stringBuilder.append(JavaDocElementEnum.LINE_START.getText());
            result = true;
        }
        return result;
    }

    private boolean closeOneLineJavaDoc() {
        boolean result = false;
        if (stringBuilder.indexOf(JavaDocElementEnum.NEW_LINE.getText()) < 0) {
            stringBuilder.append(JavaDocElementEnum.LINE_START.getText());
            result = true;
        }
        return result;
    }

    private boolean isAcceptedDescription(String description, boolean hasNext) {
        return (hasNext && StringUtils.isNotEmpty(description)) || (!hasNext && !description.matches(" +"));
    }

}
