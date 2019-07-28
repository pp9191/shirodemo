package com.pp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pp.entity.Permission;
import com.pp.entity.Role;
import com.pp.entity.RolePermission;
import com.pp.entity.User;
import com.pp.entity.UserRole;
import com.pp.service.PermissionService;
import com.pp.service.RoleService;
import com.pp.service.UserService;
import com.pp.shiro.ShiroUtils;
import com.pp.util.JsonUtils;

@Controller
@RequestMapping("/perm")
public class RolePermController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PermissionService permService;
	
	private static final String basePath = "perm/";
	
	@RequestMapping(value="/{path}", method=RequestMethod.GET)
	public String urlMapping(@PathVariable String path) {
		
		return basePath.concat(path);
	}
	
	@ResponseBody
	@RequestMapping(value="/setUserinfo")
	public Map<String, Object> setUserinfo(@ModelAttribute @Valid User user, BindingResult bindResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(bindResult.hasErrors()) {
			result.put("result", "false");
			result.put("errors", bindResult.getAllErrors());
		} else if(user.getId() == null) {
			// 新增用户
			if(userService.selectByAccount(user.getAccount()) != null) {
				result.put("result", "false");
				result.put("errors", new String[] {"用户名已存在"});
			}else {
				user.setPassword(ShiroUtils.encryptPassword(user.getPassword(), user.getAccount()));
				userService.addUser(user);			
				result.put("result", "true");
			}
		} else {
			if(JsonUtils.isNotEmpty(user.getPassword())) {
				user.setPassword(ShiroUtils.encryptPassword(user.getPassword(), user.getAccount()));
			}
			userService.setUserinfo(user);		
			result.put("result", "true");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/addRole")
	public Map<String, Object> addRole(@ModelAttribute @Valid Role role, BindingResult bindResult) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(bindResult.hasErrors()) {
			map.put("result", "false");
			map.put("errors", bindResult.getAllErrors());
		}else if(role.getId() == null) {
			// 新增
			if(roleService.getRoleByName(role.getRolename()) != null){
				map.put("result", "false");
				map.put("errors", new String[] {"角色名已存在"});
			}else {			
				User user = (User) SecurityUtils.getSubject().getPrincipal();
				role.setCreateBy(user.getAccount());
				roleService.addRole(role);
				map.put("result", "true");
			}
		}else {
			roleService.updateRole(role);
			map.put("result", "true");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/deleteRole")
	public Map<String, Object> deleteRole(@ModelAttribute Role role) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!role.getRolename().equals("admin") && roleService.deleteRole(role) > 0) {			
			result.put("result", "true");
		}else {
			result.put("result", "false");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/allRoles")
	public Map<String, Object> getRoles(Integer offset, Integer limit) {
		
		Map<String, Object> params = new HashMap<>();
		int total = roleService.selectAllCount(params);
		params.put("offset", offset);
		params.put("limit", limit);
		List<Role> roles = roleService.selectAll(params);
		
		Map<String, Object> result = new HashMap<>();
		result.put("total", total);
		result.put("rows", roles);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/userRoles/{userId}")
	public Map<String, Object> getUserRoles(@PathVariable Long userId) {
		
		List<Role> owned = roleService.getRoles(userId);
		List<Role> roles = roleService.selectAll(null);
		// 除去已拥有的角色
		for (int i = 0; i < owned.size(); i++) {
			for (int j = 0; j < roles.size(); j++) {
				if(roles.get(j).getRolename().equals(owned.get(i).getRolename())) {
					roles.remove(j);
					break;
				}
			}
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("owned", owned);
		result.put("notOwned", roles);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/setUserRoles/{operate}")
	public Map<String, Object> setUserRoles(@PathVariable String operate, HttpServletRequest request) {
		
		Map<String, Object> result = new HashMap<>();
		String rolesStr = request.getParameter(operate);
		int count = 0;
		if(JsonUtils.isNotEmpty(rolesStr)) {
			List<UserRole> userroles = JsonUtils.toList(rolesStr, UserRole.class);
			if(operate.equals("add")) {
				count = roleService.addUserRole(userroles);
			}else if(operate.equals("delete")){
				count = roleService.removeUserRole(userroles);
			}
		}
		if(count > 0) {
			result.put("result", "true");
			result.put("message", count);			
		}else {
			result.put("result", "false");
			result.put("message", "设置失败");
		}		
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/addPermission")
	public Map<String, Object> addPermission(@ModelAttribute @Valid Permission perm, BindingResult bindResult){
		Map<String, Object> map = new HashMap<String, Object>();
		if(bindResult.hasErrors()) {
			map.put("result", "false");
			map.put("errors", bindResult.getAllErrors());
		}else if(perm.getId() == null) {
			// 新增
			if(permService.getPermByName(perm.getPermname()) != null){
				map.put("result", "false");
				map.put("errors", new String[] {"权限点已存在"});
			}else {	
				permService.addPermission(perm);
				map.put("result", "true");
			}
		}else {
			permService.updatePermission(perm);
			map.put("result", "true");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/deletePermission")
	public Map<String, Object> deletePermission(@ModelAttribute Permission perm){
		Map<String, Object> result = new HashMap<String, Object>();
		if(permService.deletePermission(perm) > 0){			
			result.put("result", "true");
		}else {
			result.put("result", "false");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/allPermissions")
	public Map<String, Object> getPermissions(Integer offset, Integer limit) {
		
		Map<String, Object> params = new HashMap<>();
		int total = permService.selectAllCount(params);
		params.put("offset", offset);
		params.put("limit", limit);
		List<Permission> perms = permService.selectAll(params);
		
		Map<String, Object> result = new HashMap<>();
		result.put("total", total);
		result.put("rows", perms);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/rolePerms/{roleId}")
	public Map<String, Object> getRolePerms(@PathVariable Long roleId) {
		
		List<Permission> owned = permService.getPermissions(roleId);
		List<Permission> perms = permService.selectAll(null);
		// 除去已拥有的角色
		for (int i = 0; i < owned.size(); i++) {
			for (int j = 0; j < perms.size(); j++) {
				if(perms.get(j).getId().longValue() == owned.get(i).getId().longValue()) {
					perms.remove(j);
					break;
				}
			}
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("owned", owned);
		result.put("notOwned", perms);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/setRolePerms/{operate}")
	public Map<String, Object> setRolePerms(@PathVariable String operate, HttpServletRequest request) {
		
		Map<String, Object> result = new HashMap<>();
		String permsStr = request.getParameter(operate);
		int count = 0;
		if(JsonUtils.isNotEmpty(permsStr)) {
			List<RolePermission> roleperms = JsonUtils.toList(permsStr, RolePermission.class);
			if(operate.equals("add")) {
				count = permService.addRolePerm(roleperms);
			}else if(operate.equals("delete")){
				count = permService.deleteRolePerm(roleperms);
			}
		}
		if (count > 0) {
			result.put("result", "true");
			result.put("message", count);
		}else {
			result.put("result", "false");
			result.put("message", "设置失败");
		}
		return result;
	}	
}
