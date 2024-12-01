package com.example.ms.security.mapper;

import com.example.ms.mapper.BaseMapper;
import com.example.ms.security.entity.RoleAuth;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;

import java.util.List;

import static com.example.ms.security.sqltable.SqlTableInstance.*;
import static com.example.ms.security.sqltable.SqlTableInstance.role;
import static org.mybatis.dynamic.sql.SqlBuilder.equalTo;
import static org.mybatis.dynamic.sql.SqlBuilder.on;

public interface RoleAuthMapper extends BaseMapper<RoleAuth> {
    default void extendStartExpression(QueryExpressionDSL<SelectModel> start) {
        start.join(menu, on(menu.id, equalTo(roleAuth.menuId)));
    }

    default void addExtraColumns(List<BasicColumn> basicColumns) {
        basicColumns.add(menu.menuName.asCamelCase());
        basicColumns.add(menu.requestPath.asCamelCase());
    }
}
