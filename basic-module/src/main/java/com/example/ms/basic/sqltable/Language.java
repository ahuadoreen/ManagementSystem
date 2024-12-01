package com.example.ms.basic.sqltable;

import com.example.ms.sqltable.BaseTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;

public final class Language extends BaseTable {
    public final SqlColumn<String> keyName = column("key_name", JDBCType.VARCHAR);
    public final SqlColumn<String> enText = column("en_text", JDBCType.VARCHAR);
    public final SqlColumn<String> cnText = column("cn_text", JDBCType.VARCHAR);

    public Language() {
        super("sys_lang");
    }
}
