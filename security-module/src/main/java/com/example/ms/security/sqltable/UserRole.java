package com.example.ms.security.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

public final class UserRole extends SqlTable {
    public final SqlColumn<Long> userId = column("user_id", JDBCType.BIGINT);
    public final SqlColumn<Long> roleId = column("role_id", JDBCType.BIGINT);
    public final SqlColumn<String> createName = column("create_name", JDBCType.VARCHAR);
    public final SqlColumn<Date> createTime = column("create_time", JDBCType.DATE);

    public UserRole() {
        super("sys_user_role");
    }
}
