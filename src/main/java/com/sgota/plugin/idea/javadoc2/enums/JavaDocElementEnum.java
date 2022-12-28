package com.sgota.plugin.idea.javadoc2.enums;

/**
 * The enum Java doc elements.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public enum JavaDocElementEnum {

    STARTING("/*"),
    ENDING("/"),
    NEW_LINE("\n"),
    TAG_START("@"),
    LINE_START("*"),
    WHITE_SPACE(" ");

    private final String text;

    /**
     * Instantiates a new Java doc elements.
     *
     * @param value the value
     */
    JavaDocElementEnum(String value) {
        text = value;
    }

    /**
     * Gets the presentation.
     *
     * @return the Presentation
     */

    public String getText() {
        return text;
    }

}
