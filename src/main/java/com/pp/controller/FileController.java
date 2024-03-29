package com.pp.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pp.entity.FileInfo;
import com.pp.entity.User;
import com.pp.service.FileService;
import com.pp.service.UserService;

@Controller
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/common/headImg/{id}")
	public void validateCode(@PathVariable String id, HttpServletResponse response) {
		
		if(id.equals("0")) {
			String account = SecurityUtils.getSubject().getPrincipal().toString();
			User user = userService.selectByAccount(account);
			id = user .getHeadImg();
		}
		
		FileInfo fileInfo = null;
		if(id != null) {			
			fileInfo = fileService.getFileInfo(id);
		}
		
		File file = null;
		String format = null;
		if(fileInfo != null) {			
			String fileName = fileInfo.getFilename();
			file = new File(fileInfo.getPath(), fileName);
			format = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		
		// 实现文件下载
		try {
			if(file == null || !file.exists()) {				
				file = ResourceUtils.getFile("classpath:static/common/img/default.jpg");
				format = "jpg";
			}
			
			BufferedImage image = ImageIO.read(file);
			ServletOutputStream os = response.getOutputStream();
			ImageIO.write(image, format, os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
	
}
