package com.sgota.plugin.idea.javadoc2.model;

import java.util.List;

/**
 * The type Java doc tag.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocTag {

    /**
     * 对应PsiDocParamRef
     */
    private final String refParam;
    /**
     * 对应PsiDocTagValueImpl
     */
    private final String value;
    /**
     * 对应PsiDocTokenImpl
     */
    private final List<String> description;

    /**
     * Instantiates a new Java doc tag.
     *
     * @param refParam    the Ref param
     * @param value       the Value
     * @param description the Description
     */
    public JavaDocTag(String refParam, String value, List<String> description) {
        this.refParam = refParam;
        this.value = value;
        this.description = description;
    }

    /**
     * Gets the ref param.
     *
     * @return the Ref param
     */

    public String getRefParam() {
        return refParam;
    }

    /**
     * Gets the value.
     *
     * @return the Value
     */

    public String getValue() {
        return value;
    }

    /**
     * Gets the description.
     *
     * @return the Description
     */

    public List<String> getDescription() {
        return description;
    }

}
