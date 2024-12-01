package com.example.ms.security.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;

public final class Role extends BaseTable {
    public final SqlColumn<String> roleName = column("role_name", JDBCType.VARCHAR);
    public final SqlColumn<Long> parentId = column("parent_id", JDBCType.BIGINT);
    public final SqlColumn<Integer> orderNo = column("order_no", JDBCType.INTEGER);
    public final SqlColumn<Boolean> enable = column("enable", JDBCType.BOOLEAN);

    public Role() {
        super("sys_role");
    }
}
