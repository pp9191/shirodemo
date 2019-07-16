package com.pp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pp.entity.Role;
import com.pp.entity.User;
import com.pp.service.RoleService;

@Controller
@RequestMapping("/perm")
public class RolePermissionController {
	
	@Autowired
	private RoleService roleService;
	
	private static final String basePath = "perm/";
	
	@RequestMapping(value="/{path}", method=RequestMethod.GET)
	public String urlMapping(@PathVariable String path, Model model) {
		
		return basePath.concat(path);
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
		Map<String, Object> map = new HashMap<String, Object>();
		if(roleService.deleteRole(role) > 0) {			
			map.put("result", "true");
		}else {
			map.put("result", "false");
		}
		return map;
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
	
}
