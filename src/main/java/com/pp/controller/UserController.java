package com.pp.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pp.entity.FileInfo;
import com.pp.entity.User;
import com.pp.service.UserService;
import com.pp.shiro.ShiroUtils;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Value("${server.naspath}")
	private String basePath;
	
	private List<String> imgType = Arrays.asList("bmp", "gif", "png", "jpg", "jpeg");

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signup(User user) {		
		return "signup";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@ModelAttribute @Valid User user, BindingResult result, String codeNo) {
		Session session = SecurityUtils.getSubject().getSession();
		String vallidateCode = session.getAttribute("validateCode").toString();
		if(result.hasErrors()) {
			// 校验报错
			
		} else if(!codeNo.equals(vallidateCode)) {
			FieldError error = new FieldError("user", "account", "验证码错误");
			result.addError(error);			
		} else if(userService.selectByAccount(user.getAccount()) != null) {
			FieldError error = new FieldError("user", "account", "用户名已存在");
			result.addError(error);			
		} else {
			// 密码加密
			user.setPassword(ShiroUtils.encryptPassword(user.getPassword(), user.getAccount()));
			System.out.println(user.getPassword() + "|" + user.getPassword().length());
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
			subject.getSession().setAttribute("userinfo", subject.getPrincipal());
			return "redirect:/index";
		}catch(UnknownAccountException|LockedAccountException|ExcessiveAttemptsException ex){
			// 用户名不存在 | 账号被锁 | 密码错误次数达到5次
			FieldError error = new FieldError("user", "account", ex.getMessage());
			result.addError(error);
		}catch(AuthenticationException ex){
			
			FieldError error = new FieldError("user", "account", "密码错误");
			result.addError(error);
		}
		// 登陆失败
		return "login";
	}
	
	@RequestMapping(value="/logout")
	public String logout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.getSession().removeAttribute("userinfo");
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
	
	@RequestMapping(value="/userinfo", method=RequestMethod.GET)
	public String userinfo(Model model) {
		model.addAttribute("user", SecurityUtils.getSubject().getSession().getAttribute("userinfo"));
		return "userinfo";
	}
	
	@ResponseBody
	@RequestMapping(value="/setHead/{userId}")
	public Map<String, String> setHeadImg(@RequestParam("file") MultipartFile file, @PathVariable String userId) {
		Map<String, String> result = new HashMap<String, String>();
		
		if (!file.isEmpty()) {
			String fileName = file.getOriginalFilename();
			String type = fileName.substring(fileName.lastIndexOf(".") + 1);
			
			if(imgType.contains(type)) {
				Date date =  new Date();
				String uuid = UUID.randomUUID().toString();
				String filePath = basePath.concat(ShiroUtils.getDatePath(date));
				String pathname = filePath.concat(File.separator).concat(uuid).concat(".").concat(type);
				File dir = new File(filePath);
				
				if (!dir.exists()) {
					dir.mkdirs();
				}
				
				FileInfo fileinfo = new FileInfo();
				fileinfo.setId(uuid);
				fileinfo.setType(type);
				fileinfo.setPath(filePath);
				fileinfo.setOriginalname(fileName);
				fileinfo.setCreateTime(date);
				User curUser = (User) SecurityUtils.getSubject().getSession().getAttribute("userinfo");
				fileinfo.setCreateBy(curUser.getAccount());
				
				File dest = new File(pathname); 
				try { 
					file.transferTo(dest);
					userService.setHeadImg(userId, fileinfo);
					// 更新subject信息
					curUser.setHeadImg(uuid);
					
					result.put("result", "true");
					result.put("headImg", uuid);
				} catch (IOException e) { 
					result.put("result", "false");
					result.put("message", e.getMessage());
				}		
				
			} else {
				result.put("result", "false");
	        	result.put("message", "不支持的图片格式，仅支持以下格式：" + imgType.toString());
			}
        } else {
        	result.put("result", "false");
        	result.put("message", "请选择上传文件");
        }
		return result;		
	}
	
	@ResponseBody
	@RequestMapping(value="/setUserinfo")
	public Map<String, Object> setUserinfo(@ModelAttribute @Valid User user, BindingResult bindResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(bindResult.hasErrors()) {
			result.put("result", "false");
			result.put("errors", bindResult.getAllErrors());
		} else {			
			userService.setUserinfo(user);
			// 更新session
			user = userService.selectByAccount(user.getAccount());
			SecurityUtils.getSubject().getSession().setAttribute("userinfo", user);
			result.put("result", "true");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/setPassword")
	public Map<String, Object> setPassword(String password, String password1) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(password1.matches("^\\w{6,18}$")) {			
			User user = (User) SecurityUtils.getSubject().getSession().getAttribute("userinfo");
			if(ShiroUtils.encryptPassword(password, user.getAccount()).equals(user.getPassword())) {
				user.setPassword(ShiroUtils.encryptPassword(password1, user.getAccount()));
				userService.setUserinfo(user);
				result.put("result", "true");
			} else {
				result.put("result", "false");
				result.put("message", "旧密码输入错误");
			}
		} else {
			result.put("result", "false");
			result.put("message", "新密码格式不对：密码只能由4-18位数字、字母或下划线组成。");
		}
		return result;
	}
}
