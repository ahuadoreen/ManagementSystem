package com.example.ms.security.entity;

import com.example.ms.annotation.TableColumn;
import com.example.ms.annotation.TableName;
import com.example.tools.entity.BaseEntity;
import com.example.tools.entity.Insert;
import com.example.tools.entity.Update;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(uniqueKeys = {"username"})
public class User extends BaseEntity {
    @NotBlank(groups = {Insert.class}, message = "用户名不能为空")
    @TableColumn(updatable = false)
    private String username;
    @TableColumn(updatable = false, exportable = false, importable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank(groups = {Insert.class}, message = "昵称不能为空")
    private String displayName;
    private Boolean enable;
    @TableColumn(ignore = true)
    private List<Role> roles;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @TableColumn(ignore = true)
    private List<String> authList;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @TableColumn(ignore = true)
    private String roleNames;
    @TableColumn(ignore = true)
    private String roleIds;
}
