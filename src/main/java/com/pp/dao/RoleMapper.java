package com.pp.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import com.pp.entity.Role;

public interface RoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Role record);
    
    int insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
    
    @Cacheable(value="roleAndPermission", key="'roles_'+#userId")
    List<Role> getRoles(Long userId);
}