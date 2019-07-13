package com.pp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pp.dao.PermissionMapper;
import com.pp.dao.RolePermissionMapper;
import com.pp.entity.Permission;
import com.pp.entity.RolePermission;

@Repository("permissionService")
public class PermissionService {

	@Autowired
	private PermissionMapper permissionMapper;
	
	@Autowired
	private RolePermissionMapper roelPermissionMapper;
	
	public List<Permission> getPermissions(Long roleId) {		
		return permissionMapper.getPermissions(roleId);
	}
	
	public int addPermission(Permission permission) {
		return permissionMapper.insertSelective(permission);
	}
	
	public int addPermToRole(RolePermission record) {
		if(roelPermissionMapper.selectByPrimaryKey(record.getRoleId(), record.getPermssionId()) == null) {			
			return roelPermissionMapper.insert(record);
		}
		return 0;
	}
	
	public int deleteRolePerm(RolePermission record) {
		return roelPermissionMapper.deleteByPrimaryKey(record.getRoleId(), record.getPermssionId());
	}

}
