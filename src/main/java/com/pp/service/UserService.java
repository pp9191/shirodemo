package com.pp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pp.dao.FileInfoMapper;
import com.pp.dao.UserMapper;
import com.pp.entity.FileInfo;
import com.pp.entity.User;
import com.pp.shiro.ShiroUtils;

@Repository("userService")
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private FileInfoMapper fileMapper;
	
	public Integer addUser(User user) {
		
		return userMapper.insertSelective(user);
	}
	
	public User selectByAccount(String account) {
		
		return userMapper.selectByAccount(account);
	}

	public List<User> getUsers() {
		
		return userMapper.getUsers();
	}

	@Transactional
	public void setHeadImg(String userId, FileInfo fileinfo) {
		
		User user = userMapper.selectByPrimaryKey(Long.decode(userId));		
		if(user.getHeadImg() != null && !user.getHeadImg().isEmpty()) {
			FileInfo old_ = fileMapper.selectByPrimaryKey(user.getHeadImg());
			// 删除旧的图像文件
			ShiroUtils.deleteFile(old_);
		}
		// 更新图像
		user.setHeadImg(fileinfo.getId());
		fileMapper.insertSelective(fileinfo);
		userMapper.updateByPrimaryKeySelective(user);
	}
}
