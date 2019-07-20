package com.pp.dao;

import java.util.List;
import java.util.Map;

import com.pp.entity.Permission;

public interface PermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);
    
    List<Permission> getPermissions(Long roleId);

	List<Permission> selectAll(Map<String, Object> params);

	int selectAllCount(Map<String, Object> params);
}