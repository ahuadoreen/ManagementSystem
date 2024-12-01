package com.example.ms.security.entity;

import com.example.ms.annotation.TableColumn;
import com.example.ms.annotation.TableName;
import com.example.tools.entity.BaseEntity;
import com.example.tools.entity.Insert;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(uniqueKeys = {"roleName"})
public class Role extends BaseEntity {
    @TableColumn(updatable = false)
    @NotBlank(groups = {Insert.class}, message = "角色名称不能为空")
    private String roleName;
    private Long parentId;
    private Integer orderNo;
    private Boolean enable;
}
