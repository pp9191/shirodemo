package com.pp.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
	
	@RequestMapping({"/","/index"})
	public String homepage() {
		System.out.println("index");
		
		return "index";
	}
	
	@RequestMapping("/403")
	public String unauthorized() {
		System.out.println("unauthorized");
		
		return "403";
	}
	
	@RequestMapping("/common/validateCode")
	public void validateCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        int width = 60;
        int height = 32;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(0xDCDBD2));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.black);
//        g.drawRect(0, 0, width - 1, height - 1);
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        System.out.println(hash1);
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        String capstr = hash1.substring(0, 4);
         Session session = SecurityUtils.getSubject().getSession();
        //将生成的验证码存入session
        session.setAttribute("validateCode", capstr);
        g.setColor(new Color(53, 61, 255));
        g.setFont(new Font("宋体", Font.PLAIN, 24));
        g.drawString(capstr, 8, 24);
        g.dispose();
        //输出图片
        resp.setContentType("image/jpeg");
        ServletOutputStream os = resp.getOutputStream();
        ImageIO.write(image, "jpeg", os);
        os.close(); 
    }
	
}
