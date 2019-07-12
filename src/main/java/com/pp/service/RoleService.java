package com.pp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pp.dao.RoleMapper;
import com.pp.entity.Role;
import com.pp.entity.User;
import com.pp.shiro.ShiroRealm;

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
		return addRoleToUser(userId, new int[] {roleId} );
	}
	
	@Transactional
	public int addRoleToUser(int userId, int[] roleIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("roleIds", roleIds);
		
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		map.put("createBy", user.getAccount());
		
		DefaultWebSecurityManager defaultManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
		ShiroRealm shiroRealm = (ShiroRealm) defaultManager.getRealms();
		shiroRealm.getAuthorizationCache().clear();
		
		return roleMapper.insertUserRole(map);
	}

}
