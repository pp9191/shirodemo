package com.pp.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import com.pp.entity.Permission;

public interface PermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);
    
    @Cacheable(value="roleAndPermission", key="'permission_'+#roleId")
    List<Permission> getPermissions(Long roleId);
}