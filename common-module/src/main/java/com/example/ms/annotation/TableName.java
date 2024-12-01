package com.example.ms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface TableName {
    /**
     * SqlTable实例化的对象名称，如果不配置，默认为类名首字母小写
     * @return
     */
    String value() default "";

    /**
     * 导出模板或数据时的文件名称
     * @return
     */
    String label() default "";

    /**
     * SqlTable中主键变量的变量名
     * @return
     */
    String primaryKey() default "id";
    /**
     * entity中唯一索引的变量名
     * @return
     */
    String[] uniqueKeys() default {};

    boolean importExistUpdate() default false;
}
