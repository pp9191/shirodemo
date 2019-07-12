package com.pp.dao;

import java.util.List;
import java.util.Map;

import com.pp.entity.Role;

public interface RoleMapper {
    int deleteByPrimaryKey(Long id);

    int insertUserRole(Map<String, Object> record);
    
    int insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
    
    List<Role> getRoles(Long userId);
}