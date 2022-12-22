package com.sgota.plugin.idea.javadoc2.ui.model;

import java.util.Objects;

/**
 * TemplateVo
 *
 * @author tiankuo
 */
public class TemplateVo {
    /**
     * name
     */
    private String name;
    /**
     * content
     */
    private String content;

    /**
     * Gets name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets content
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateVo that = (TemplateVo) o;
        return Objects.equals(name, that.name) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, content);
    }
}
