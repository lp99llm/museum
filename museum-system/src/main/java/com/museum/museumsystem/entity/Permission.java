package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String permCode;      // 权限编码
    private String permName;      // 权限名称
    private Long parentId;        // 父权限ID
    private String type;          // 类型：menu-菜单，button-按钮
    private Integer sort;         // 排序
    private String path;          // 前端路由路径（可选）

    // 非数据库字段：子权限列表（用于构建树形结构）
    @TableField(exist = false)
    private List<Permission> children = new ArrayList<>();

    // 非数据库字段：标记角色是否拥有该权限（用于权限分配界面）
    @TableField(exist = false)
    private boolean owned;
}