package com.pp.service;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pp.dao.PermissionMapper;
import com.pp.dao.RolePermissionMapper;
import com.pp.entity.Permission;
import com.pp.entity.RolePermission;
import com.pp.entity.User;

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
	
	public int updatePermission(Permission permission) {
		return permissionMapper.updateByPrimaryKeySelective(permission);
	}
	
	@Transactional
	public int deletePermission(Permission permission) {
		// 删除相应角色权限
		roelPermissionMapper.deleteByPermissionId(permission.getId());
		int r = permissionMapper.deleteByPrimaryKey(permission.getId());
		return r;
	}
	
	public int addRolePerm(RolePermission record) {
		if(roelPermissionMapper.selectByPrimaryKey(record.getRoleId(), record.getPermissionId()) == null) {
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			record.setCreateBy(user.getAccount());
			return roelPermissionMapper.insert(record);
		}
		return 0;
	}
	
	public int deleteRolePerm(RolePermission record) {
		return roelPermissionMapper.deleteByPrimaryKey(record.getRoleId(), record.getPermissionId());
	}
	
	@Transactional
	public int addRolePerm(List<RolePermission> records) {
		int r = 0;
		for (RolePermission record : records) {
			r += addRolePerm(record);
		}
		return r;
	}
	
	@Transactional
	public int deleteRolePerm(List<RolePermission> records) {
		int r = 0;
		for (RolePermission record : records) {
			r += deleteRolePerm(record);
		}
		return r;
	}

	public List<Permission> selectAll(Map<String, Object> params) {
		return permissionMapper.selectAll(params);
	}

	public int selectAllCount(Map<String, Object> params) {
		return permissionMapper.selectAllCount(params);
	}

	public Permission getPermByName(String permname) {
		return permissionMapper.selectByPermname(permname);
	}
}
