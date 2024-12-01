package com.example.ms.sqltable;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

public abstract class BaseTable extends SqlTable {
    public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
    public final SqlColumn<String> createName = column("create_name", JDBCType.VARCHAR);
    public final SqlColumn<Date> createTime = column("create_time", JDBCType.DATE);
    public final SqlColumn<String> updateName = column("update_name", JDBCType.VARCHAR);
    public final SqlColumn<Date> updateTime = column("update_time", JDBCType.DATE);

    public BaseTable(String tableName) {
        super(tableName);
    }
}
