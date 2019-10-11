package com.pp.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
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
import com.pp.util.FileUtils;
import com.pp.util.JsonUtils;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Value("${server.naspath}")
	private String baseUploadPath;
	
	private static final List<String> imgType = Arrays.asList(".bmp", ".gif", ".png", ".jpg", ".jpeg");
	private static final String basePath = "user/";

	@RequestMapping(value="/{path}", method=RequestMethod.GET)
	public String urlMapping(@PathVariable String path, Model model) {
		if(path.equals("login") || path.equals("signup")){
			model.addAttribute("user", new User());
		}else if(path.equals("userinfo")) {
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			model.addAttribute("user", userService.selectByAccount(user.getAccount()));
		} 
		return basePath.concat(path);
	}
	
	@RequestMapping(value="/logout")
	public String logout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		return "redirect:/index";
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
			if(userService.addUser(user) == 1) {
				// 注册成功，跳转登录
				FieldError error = new FieldError("user", "account", "注册成功，请登录");
				result.addError(error);
				return basePath.concat("login");
			}
		}
		return basePath.concat("signup");
	}
		
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@ModelAttribute @Valid User user, BindingResult result, boolean rememberMe, HttpServletRequest request) {
		System.out.println(user.getAccount() + " || " + user.getPassword());
		if(!result.hasErrors()) {			
			Subject subject = SecurityUtils.getSubject();
			try{
				// 调用安全认证框架的登录方法
				subject.login(new UsernamePasswordToken(user.getAccount(), user.getPassword(), rememberMe));
				String url = "/index";
				SavedRequest saveRequest = WebUtils.getSavedRequest(request);
				if(saveRequest != null && saveRequest.getRequestUrl() != null) {
					url = saveRequest.getRequestUrl();
				}
				return "redirect:" + url;
			}catch(UnknownAccountException|LockedAccountException|ExcessiveAttemptsException ex){
				// 用户名不存在 | 账号被锁 | 密码错误次数达到5次
				FieldError error = new FieldError("user", "account", ex.getMessage());
				result.addError(error);
			}catch(AuthenticationException ex){				
				FieldError error = new FieldError("user", "account", "密码错误");
				result.addError(error);
			}
		}		
		// 登陆失败
		return basePath.concat("login");
	}	
	
	@ResponseBody
	@RequestMapping(value="/users", method=RequestMethod.POST)
	public Map<String, Object> getUsers(Integer offset, Integer limit) {
				  
		Map<String, Object> params = new HashMap<>();
		int total = userService.getUsersCount(params);
		params.put("offset", offset);
		params.put("limit", limit);
		List<User> users = userService.getUsers(params);
		
		Map<String, Object> result = new HashMap<>();
		result.put("total", total);
		result.put("rows", users);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/setHead/{account}")
	public Map<String, Object> setHeadImg(@RequestParam("file") MultipartFile file, @PathVariable String account) {
		Map<String, Object> result = new HashMap<String, Object>();		
		if (file.isEmpty()) {
			result.put("result", "false");
        	result.put("message", "请选择上传文件");
        } else if (file.getSize() > 2 * 1024 * 1024) {
    		result.put("result", "false");
    		result.put("message", "文件大小超出2MB限制");
    	} else {
			String originalName = file.getOriginalFilename();
			String type = originalName.substring(originalName.lastIndexOf("."));
			if (imgType.contains(type.toLowerCase())) {
				Date date = new Date();
				String uuid = UUID.randomUUID().toString();
				String filePath = baseUploadPath.concat(FileUtils.getDatePath(date));
				String filename = uuid.concat(type);
				
				File dir = new File(filePath);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				FileInfo fileinfo = new FileInfo();
				fileinfo.setId(uuid);
				fileinfo.setPath(filePath);
				fileinfo.setFilename(filename);
				fileinfo.setOriginalname(originalName);
				fileinfo.setCreateTime(date);
				User curUser = (User) SecurityUtils.getSubject().getPrincipal();
				fileinfo.setCreateBy(curUser.getAccount());

				try {
					file.transferTo(new File(dir, filename));
					userService.setHeadImg(account, fileinfo);
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
			if(user.getPassword() != null) {
				// 不支持修改密码
				user.setPassword(null);
			}
			User curUser = (User) SecurityUtils.getSubject().getPrincipal();
			user.setId(curUser.getId());
			user.setAccount(curUser.getAccount());
			userService.setUserinfo(user);
			result.put("result", "true");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/setPassword")
	public Map<String, Object> setPassword(String password, String password1) {
		Map<String, Object> result = new HashMap<String, Object>();
		int r = 0;
		if(password1.matches("^\\w{6,18}$")) {
			User curUser = (User) SecurityUtils.getSubject().getPrincipal();
			if(ShiroUtils.encryptPassword(password, curUser.getAccount()).equals(curUser.getPassword())) {
				User user = new User();
				user.setId(curUser.getId());
				user.setPassword(ShiroUtils.encryptPassword(password1, curUser.getAccount()));
				r = userService.setUserinfo(user);
			}
		}
		if(r > 0) {
			result.put("result", "true");			
		}else {			
			result.put("result", "false");
		}
		return result;
	}
}
