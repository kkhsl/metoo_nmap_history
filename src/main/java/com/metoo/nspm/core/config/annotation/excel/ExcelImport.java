package com.metoo.nspm.core.config.annotation.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    // 表字段名
    String value();

    /** 导出映射，格式如：0-未知;1-男;2-女 */
    String kv() default "";

    // 是否必填字段（默认为非必填）
    boolean required() default false;

    // 最大长度（默认255）
    int maxLength() default 255;

    // 导入唯一验证（多个字段则取联合验证）
    boolean unique() default false;
}