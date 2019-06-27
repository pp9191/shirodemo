package com.pp.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
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
			FieldError error = new FieldError("user", "account", "用户名已存在");
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
	public String login(@ModelAttribute @Valid User user, BindingResult result, boolean rememberMe) {
		System.out.println(user.getAccount() + " || " + user.getPassword());
		if(result.hasErrors()) {
			// 校验报错
			return "login";
		}
		
		Subject subject = SecurityUtils.getSubject();
		try{
			// 调用安全认证框架的登录方法
			subject.login(new UsernamePasswordToken(user.getAccount(), user.getPassword(), rememberMe));			
			return "redirect:/index";
		}catch(UnknownAccountException | LockedAccountException ex){
			
			FieldError error = new FieldError("user", "account", ex.getMessage());
			result.addError(error);
		}catch(ExcessiveAttemptsException ex){
			
			FieldError error = new FieldError("user", "account", "密码错误次数超过五次，请十分钟后登录!");
			result.addError(error);
		}catch(AuthenticationException ex){
			
			FieldError error = new FieldError("user", "account", "用户名或密码错误");
			result.addError(error);
		}
		// 登陆失败
		return "login";
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
