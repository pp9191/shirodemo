package com.pp.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pp.entity.User;
import com.pp.service.UserService;
import com.pp.shiro.ShiroUtils;

@Controller
@RequestMapping("/userinfo")
public class UserController {
	
	@Autowired
	private UserService userService;

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signup(User user) {		
		return "signup";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@ModelAttribute @Valid User user, BindingResult result) {	
		if(result.hasErrors()) {
			// 校验报错
			
		} else if(userService.selectByAccount(user.getAccount()) != null) {
			FieldError error = new FieldError("user", "account", "账号已存在");
			result.addError(error);			
		} else {
			// 密码加密
			user.setPassword(ShiroUtils.encryptPassword(user.getPassword(), user.getAccount()));
			if(userService.addUser(user) == 1) {				
				// 注册成功，跳转登录
				return "login";
			}
		}
		return "signup";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(User user) {
		
		return "login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@ModelAttribute @Valid User user, BindingResult result) {
		System.out.println(user.getAccount() + " || " + user.getPassword());
		if(result.hasErrors()) {
			// 校验报错
			return "login";
		}
		
		Subject subject = SecurityUtils.getSubject();
		try{
			// 调用安全认证框架的登录方法
			subject.login(new UsernamePasswordToken(user.getAccount(), user.getPassword()));			
		}catch(AuthenticationException ex){
			System.out.println(ex.getMessage());
			FieldError error = new FieldError("user", "account", "账号或密码错误");
			result.addError(error );
			// 登陆失败
			return "login";
		}
		return "redirect:/index";
	}
	
	@RequestMapping(value="/logout")
	public String logout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout(); 
		}
		return "redirect:/index";
	}
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public String getUsers(Model model) {
		List<User> users = userService.getUsers();
		model.addAttribute("users", users);
		return "users";
	}
}
