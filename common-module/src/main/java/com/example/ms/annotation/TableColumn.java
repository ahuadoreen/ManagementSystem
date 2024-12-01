package com.example.ms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableColumn {
    /**
     * SqlTable中声明的字段变量名，默认和entity中的相同
     * @return
     */
    String value() default "";

    /**
     * 导入模板或数据时的标题
     * @return
     */
    String title() default "";
    boolean insertable() default true;
    boolean updatable() default true;
    boolean queryable() default true;
    boolean filterable() default true;
    boolean exportable() default true;
    boolean importable() default true;

    /**
     * ignore为true时代表上述所有able结尾的都是false
     * @return
     */
    boolean ignore() default false;
}
