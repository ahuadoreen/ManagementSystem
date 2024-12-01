package com.example.tools.entity;

import lombok.Data;
import org.mybatis.dynamic.sql.SqlColumn;

import java.util.List;

@Data
public class TableInfo {
    /**
     * SqlTable实例化的对象名称，如果不配置，默认为类名首字母小写
     */
    private String tableName;
    /**
     * 导出模板或数据时的文件名称
     */
    private String label;
    /**
     * SqlTable中主键变量的变量名
     */
    private String primaryKey;
    /**
     * entity中唯一索引的变量名
     */
    private String[] uniqueKeys;
    private List<TableColumn> tableColumns;
    private boolean importExistUpdate;
}
