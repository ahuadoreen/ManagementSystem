package com.example.ms.basic.sqltable;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.time.Instant;
import java.time.LocalDateTime;

public class Log extends SqlTable {
    public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
    public final SqlColumn<Integer> level = column("level", JDBCType.INTEGER);
    public final SqlColumn<Integer> logType = column("log_type", JDBCType.INTEGER);
    public final SqlColumn<String> serviceName = column("service_name", JDBCType.VARCHAR);
    public final SqlColumn<String> className = column("class_name", JDBCType.VARCHAR);
    public final SqlColumn<String> browserType = column("browser_type", JDBCType.VARCHAR);
    public final SqlColumn<String> serviceIp = column("service_ip", JDBCType.VARCHAR);
    public final SqlColumn<String> url = column("url", JDBCType.VARCHAR);
    public final SqlColumn<String> requestParam = column("request_param", JDBCType.BLOB);
    public final SqlColumn<String> response = column("response", JDBCType.BLOB);
    public final SqlColumn<String> exception = column("exception", JDBCType.BLOB);
    public final SqlColumn<String> userName = column("user_name", JDBCType.VARCHAR);
    public final SqlColumn<String> userIp = column("user_ip", JDBCType.VARCHAR);
    public final SqlColumn<String> roleName = column("role_name", JDBCType.VARCHAR);
    public final SqlColumn<Instant> startTime = column("start_time", JDBCType.DATE);
    public final SqlColumn<Instant> endTime = column("end_time", JDBCType.DATE);
    public final SqlColumn<Integer> elapsedTime = column("elapsed_time", JDBCType.INTEGER);

    protected Log() {
        super("sys_log");
    }
}
