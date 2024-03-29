package com.pp.service;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pp.dao.RoleMapper;
import com.pp.dao.RolePermissionMapper;
import com.pp.dao.UserRoleMapper;
import com.pp.entity.Role;
import com.pp.entity.User;
import com.pp.entity.UserRole;

@Repository("roleService")
public class RoleService {
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private RolePermissionMapper rolePermissionMapper;

	public List<Role> getRoles(Long userId) {		
		return roleMapper.getRoles(userId);
	}
	
	public Role getRoleByName(String rolename) {
		return roleMapper.selectByRoleName(rolename);
	}
	
	public int addRole(Role role) {
		return roleMapper.insertSelective(role);
	}
	
	public int updateRole(Role role) {
		return roleMapper.updateByPrimaryKeySelective(role);
	}
	
	@Transactional
	public int deleteRole(Role role) {
		// 删除角色授权
		userRoleMapper.deleteByRoleId(role.getId());
		// 删除权限点授权
		rolePermissionMapper.deleteByRoleId(role.getId());
		// 最后删除角色
		int r = roleMapper.deleteByPrimaryKey(role.getId());
		return r;
	}

	public List<Role> selectAll(Map<String, Object> params) {
		return roleMapper.selectAll(params);
	}

	public int selectAllCount(Map<String, Object> params) {
		return roleMapper.selectAllCount(params);
	}
	
	public int addUserRole(UserRole record) {
		User user = (User) SecurityUtils.getSubject().getPrincipal();		
		if(userRoleMapper.selectByPrimaryKey(record.getUserId(), record.getRoleId()) == null) {
			record.setCreateBy(user.getAccount());
			return userRoleMapper.insert(record);
		}
		return 0;
	}
	
	public int removeUserRole(UserRole record) {
		return userRoleMapper.deleteByPrimaryKey(record.getUserId(), record.getRoleId());
	}
	
	@Transactional
	public int addUserRole(List<UserRole> records) {
		int r = 0;
		for (UserRole record : records) {
			r += addUserRole(record);
		}
		return r;
	}
	
	@Transactional
	public int removeUserRole(List<UserRole> records) {
		int r = 0;
		for (UserRole record : records) {
			r += removeUserRole(record);
		}
		return r;
	}
	
}
