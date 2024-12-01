package com.example.ms.security.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

public final class RoleAuth extends BaseTable {
    public final SqlColumn<Long> roleId = column("role_id", JDBCType.BIGINT);
    public final SqlColumn<Long> menuId = column("menu_id", JDBCType.BIGINT);
    public final SqlColumn<String> auth = column("auth", JDBCType.VARCHAR);

    public RoleAuth() {
        super("sys_role_auth");
    }
}
