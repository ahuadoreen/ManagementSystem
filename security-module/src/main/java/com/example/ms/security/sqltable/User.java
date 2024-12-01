package com.example.ms.security.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class User extends BaseTable {
    public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);
    public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);
    public final SqlColumn<String> displayName = column("display_name", JDBCType.VARCHAR);
    public final SqlColumn<Boolean> enable = column("enable", JDBCType.BOOLEAN);

    public User() {
        super("sys_user");
    }
}
