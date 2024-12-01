package com.example.ms.security.entity;

import com.example.ms.annotation.TableColumn;
import com.example.tools.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"menuId", "roleId"}, callSuper = false)
@Data
public class RoleAuth extends BaseEntity {
    private long roleId;
    private long menuId;
    private String auth;
    @TableColumn(ignore = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String label;
    @TableColumn(ignore = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String requestPath;
    @TableColumn(ignore = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long parentId;
}
