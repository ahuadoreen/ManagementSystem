package com.example.ms.security.entity;

import com.example.ms.annotation.TableName;
import com.example.tools.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"menuName"}, callSuper = false)
@Data
@TableName(uniqueKeys = {"menuName"})
public class Menu extends BaseEntity {
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 显示在前端的菜单标题
     */
    private String label;
    /**
     * 前端的访问路径
     */
    private String url;
    /**
     * 后端的路径
     */
    private String requestPath;
    /**
     * 可配置的权限，通常如增删改查可配置为search,add,update,delete
     */
    private String auth;
    /**
     * 前端显示的菜单图标
     */
    private String icon;
    /**
     * 备注
     */
    private String remark;
    /**
     * 父级菜单id
     */
    private Long parentId;
    /**
     * 菜单排序，数字小的在前
     */
    private Integer orderNo;
    /**
     * 是否显示在主菜单栏中
     */
    private Boolean isShow;
    /**
     * 是否启用
     */
    private Boolean enable;
}
