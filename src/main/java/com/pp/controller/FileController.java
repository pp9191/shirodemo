package com.pp.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pp.entity.FileInfo;
import com.pp.service.FileService;

@Controller
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	@RequestMapping("/common/headImg/{id}")
	public void validateCode(@PathVariable String id, HttpServletResponse response) throws UnsupportedEncodingException {
		
		FileInfo fileInfo = fileService.getFileInfo(id);
		
		File file = null;
		String type = null;
		if(fileInfo != null) {			
			String fileName = fileInfo.getId() + "." + fileInfo.getType(); 
			String pathname = fileInfo.getPath() + File.separator + fileName;
			file = new File(pathname);			
			type = fileInfo.getType();
		} 
		// 实现文件下载
		try {
			if(file == null || !file.exists()) {				
				file = ResourceUtils.getFile("classpath:static/common/img/default.jpg");
				type = "jpg";
			}
			
			BufferedImage image = ImageIO.read(file);
			ServletOutputStream os = response.getOutputStream();
			ImageIO.write(image, type, os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
	
}
