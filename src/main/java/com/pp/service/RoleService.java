package com.pp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.pp.dao.RoleMapper;
import com.pp.dao.UserRoleMapper;
import com.pp.entity.Role;
import com.pp.entity.UserRole;

@Repository("roleService")
public class RoleService {
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;

	public List<Role> getRoles(Long userId) {		
		return roleMapper.getRoles(userId);
	}
	
	public int addRole(Role role) {
		return roleMapper.insertSelective(role);
	}
	
	public int addRoleToUser(UserRole record) {
		if(userRoleMapper.selectByPrimaryKey(record.getUserId(), record.getRoleId()) == null) {			
			return userRoleMapper.insert(record);
		}
		return 0;
	}
	
	public int deleteUserRole(UserRole record) {
		return userRoleMapper.deleteByPrimaryKey(record.getUserId(), record.getRoleId());
	}
}
