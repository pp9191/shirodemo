package com.pp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	@RequestMapping("/roleAndUsers")
	public List<Map<String, Object>> getRoleAndUsers() {
		
		return roleService.selectRoleAndUsers();
	}
	
	
}
