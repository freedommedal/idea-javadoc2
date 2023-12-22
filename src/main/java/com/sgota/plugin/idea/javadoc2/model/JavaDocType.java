package com.sgota.plugin.idea.javadoc2.model;

/**
 * Java注释类型
 *
 * @author 单红宇
 * @date 2023/12/22 9:36
 */
public enum JavaDocType {

    /**
     * 枚举值：CLASS
     */
    CLASS("class"),

    /**
     * 枚举值：METHOD
     */
    METHOD("method"),

    /**
     * 枚举值：FIELD
     */
    FIELD("field");

    private final String value;

    /**
     * 构造函数，私有化以防止外部创建对象
     *
     * @param value 枚举值
     */
    JavaDocType(String value) {
        this.value = value;
    }

    /**
     * 获取枚举值
     *
     * @return 枚举值字符串
     */
    public String getValue() {
        return value;
    }

}
