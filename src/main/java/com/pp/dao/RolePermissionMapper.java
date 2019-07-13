package com.pp.dao;

import com.pp.entity.RolePermission;
import org.apache.ibatis.annotations.Param;

public interface RolePermissionMapper {
    int deleteByPrimaryKey(@Param("roleId") Long roleId, @Param("permssionId") Long permssionId);

    int insert(RolePermission record);

    int insertSelective(RolePermission record);

    RolePermission selectByPrimaryKey(@Param("roleId") Long roleId, @Param("permssionId") Long permssionId);

    int updateByPrimaryKeySelective(RolePermission record);

    int updateByPrimaryKey(RolePermission record);
}