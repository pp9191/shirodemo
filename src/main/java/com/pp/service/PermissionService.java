package com.pp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pp.dao.PermissionMapper;
import com.pp.entity.Permission;

@Repository("permissionService")
public class PermissionService {

	@Autowired
	private PermissionMapper permissionMapper;
	
	public List<Permission> getPermissions(Long roleId) {		
		return permissionMapper.getPermissions(roleId);
	}
	
	public int addPermission(Permission permission) {
		return permissionMapper.insertSelective(permission);
	}

}
