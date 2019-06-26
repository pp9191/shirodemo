package com.pp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
	
	@RequestMapping({"/","/index"})
	public String homepage() {
		System.out.println("index");
		
		return "index";
	}
	
	@RequestMapping({"/403","/unauthorized"})
	public String unauthorized() {
		System.out.println("unauthorized");
		
		return "403";
	}
	
}
