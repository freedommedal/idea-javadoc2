package com.sgota.plugin.idea.javadoc2.model.setting;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Java doc settings.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class JavaDocSetting implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Map<String, String> classTemplates = new LinkedHashMap<>();
    private Map<String, String> fieldTemplates = new LinkedHashMap<>();
    private Map<String, String> methodTemplates = new LinkedHashMap<>();
    private Map<String, String> constructorTemplates = new LinkedHashMap<>();

    /**
     * Gets class templates.
     *
     * @return the class templates
     */
    public Map<String, String> getClassTemplates() {
        return classTemplates;
    }

    /**
     * Sets class templates.
     *
     * @param classTemplates the class templates
     */
    public void setClassTemplates(Map<String, String> classTemplates) {
        this.classTemplates = classTemplates;
    }

    /**
     * Gets constructor templates.
     *
     * @return the constructor templates
     */
    public Map<String, String> getConstructorTemplates() {
        return constructorTemplates;
    }

    /**
     * Sets constructor templates.
     *
     * @param constructorTemplates the constructor templates
     */
    public void setConstructorTemplates(Map<String, String> constructorTemplates) {
        this.constructorTemplates = constructorTemplates;
    }

    /**
     * Gets field templates.
     *
     * @return the field templates
     */
    public Map<String, String> getFieldTemplates() {
        return fieldTemplates;
    }

    /**
     * Sets field templates.
     *
     * @param fieldTemplates the field templates
     */
    public void setFieldTemplates(Map<String, String> fieldTemplates) {
        this.fieldTemplates = fieldTemplates;
    }

    /**
     * Gets method templates.
     *
     * @return the method templates
     */
    public Map<String, String> getMethodTemplates() {
        return methodTemplates;
    }

    /**
     * Sets method templates.
     *
     * @param methodTemplates the method templates
     */
    public void setMethodTemplates(Map<String, String> methodTemplates) {
        this.methodTemplates = methodTemplates;
    }
}
