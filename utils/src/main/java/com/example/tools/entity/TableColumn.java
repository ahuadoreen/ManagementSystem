package com.example.tools.entity;

import lombok.Data;
import org.mybatis.dynamic.sql.SqlColumn;

import java.util.List;
import java.util.Map;

@Data
public class TableColumn {
    /**
     * 在entity中的属性名称
     */
    private String index;
    /**
     * 是否可插入，默认是
     */
    private boolean insertable;
    /**
     * 是否可修改，默认是
     */
    private boolean updatable;
    /**
     * 是否可查询，默认是
     */
    private boolean queryable;
    /**
     * 是否可过滤，默认是
     */
    private boolean filterable;
    /**
     * 是否可导出，默认是
     */
    private boolean exportable;
    /**
     * 是否可导入，默认是
     */
    private boolean importable;
    /**
     * 在SqlTable中定义的SqlColumn的属性名称，和entity中的属性名称对应，默认相同
     */
    private SqlColumn sqlColumn;
    /**
     * 导出模板或数据时对应的标题
     */
    private String title;
    /**
     * 前端反传回的对应的字典表的数据
     */
    private List<Map<String, Object>> enums;
}
