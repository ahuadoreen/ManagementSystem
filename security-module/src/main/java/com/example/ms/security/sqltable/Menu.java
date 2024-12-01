package com.example.ms.security.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;

public final class Menu extends BaseTable {
    public final SqlColumn<String> menuName = column("menu_name", JDBCType.VARCHAR);
    public final SqlColumn<String> label = column("label", JDBCType.VARCHAR);
    public final SqlColumn<String> url = column("url", JDBCType.VARCHAR);
    public final SqlColumn<String> requestPath = column("request_path", JDBCType.VARCHAR);
    public final SqlColumn<String> auth = column("auth", JDBCType.VARCHAR);
    public final SqlColumn<String> icon = column("icon", JDBCType.VARCHAR);
    public final SqlColumn<String> remark = column("remark", JDBCType.VARCHAR);
    public final SqlColumn<Long> parentId = column("parent_id", JDBCType.BIGINT);
    public final SqlColumn<Integer> orderNo = column("order_no", JDBCType.INTEGER);
    public final SqlColumn<Boolean> isShow = column("is_show", JDBCType.BOOLEAN);
    public final SqlColumn<Boolean> enable = column("enable", JDBCType.BOOLEAN);

    public Menu() {
        super("sys_menu");
    }
}
