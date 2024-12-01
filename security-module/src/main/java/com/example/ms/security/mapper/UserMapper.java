package com.example.ms.security.mapper;

import com.example.ms.mapper.BaseMapper;
import com.example.ms.security.entity.Role;
import com.example.ms.security.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "UserResult", value = {
            @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
            @Result(column = "username", property = "username", jdbcType = JdbcType.VARCHAR),
            @Result(column = "password", property = "password", jdbcType = JdbcType.VARCHAR),
            @Result(column = "displayName", property = "displayName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "enable", property = "enable", jdbcType = JdbcType.BOOLEAN),
            @Result(column = "createName", property = "createName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "createTime", property = "createTime", jdbcType = JdbcType.DATE),
            @Result(column = "updateName", property = "updateName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "updateTime", property = "updateTime", jdbcType = JdbcType.DATE),
            @Result(column = "roleNames", property = "roleNames", jdbcType = JdbcType.VARCHAR),
            @Result(column = "roleIds", property = "roleIds", jdbcType = JdbcType.VARCHAR),
            @Result(property = "roles", many = @Many(resultMap = "RoleResult"))
    })
    List<User> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "RoleResult", value = {
            @Result(column = "roleId", property = "id", jdbcType = JdbcType.BIGINT, id = true),
            @Result(column = "roleName", property = "roleName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "roleEnable", property = "enable", jdbcType = JdbcType.BOOLEAN)
    })
    List<Role> selectManyRoles(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("UserResult")
    User selectOne(SelectStatementProvider selectStatement);
}
