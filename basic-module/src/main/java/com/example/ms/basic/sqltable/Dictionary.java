package com.example.ms.basic.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;

public final class Dictionary extends BaseTable {
    public final SqlColumn<String> dictionaryName = column("dictionary_name", JDBCType.VARCHAR);
    public final SqlColumn<String> dictionaryKey = column("dictionary_key", JDBCType.VARCHAR);

    public final SqlColumn<String> dictionaryValue = column("dictionary_value", JDBCType.VARCHAR);

    public final SqlColumn<Integer> dictionaryType = column("dictionary_type", JDBCType.TINYINT);

    public final SqlColumn<Integer> keyType = column("key_type", JDBCType.TINYINT);

    public final SqlColumn<Integer> valueType = column("value_type", JDBCType.TINYINT);

    public final SqlColumn<String> serviceName = column("service_name", JDBCType.VARCHAR);

    public final SqlColumn<String> frontendStyle = column("frontend_style", JDBCType.VARCHAR);

    public final SqlColumn<String> remark = column("remark", JDBCType.VARCHAR);

    public final SqlColumn<Long> parentId = column("parent_id", JDBCType.BIGINT);
    public final SqlColumn<Integer> orderNo = column("order_no", JDBCType.INTEGER);
    public final SqlColumn<Boolean> enable = column("enable", JDBCType.BOOLEAN);

    public Dictionary() {
        super("sys_dictionary");
    }
}
