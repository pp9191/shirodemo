package com.pp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pp.dao.RoleMapper;
import com.pp.entity.Role;

@Repository("roleService")
public class RoleService {
	
	@Autowired
	private RoleMapper roleMapper;

	public List<Role> getRoles(Long userId) {		
		return roleMapper.getRoles(userId);
	}
	
	public int addRole(Role role) {
		return roleMapper.insertSelective(role);
	}
	
	public int addRoleToUser(int userId, int roleId) {
		return 0;
	}
	
	public int addRoleToUser(int userId, int[] roleIds) {
		return 0;
	}

}
