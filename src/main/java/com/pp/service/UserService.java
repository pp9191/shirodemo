package com.pp.service;

import java.util.List;
import java.util.Map;

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
	
	public int addUser(User user) {
		
		return userMapper.insertSelective(user);
	}
	
	public User selectByAccount(String account) {
		
		return userMapper.selectByAccount(account);
	}

	public List<User> getUsers(Map<String, Object> params) {
		
		return userMapper.getUsers(params);
	}
	
	public int getUsersCount(Map<String, Object> params) {
		
		return userMapper.getUsersCount(params);
	}

	@Transactional
	public void setHeadImg(String account, FileInfo fileinfo) {
		
		User user = userMapper.selectByAccount(account);		
		if(user.getHeadImg() != null && !user.getHeadImg().isEmpty()) {
			FileInfo old_f = fileMapper.selectByPrimaryKey(user.getHeadImg());
			// 删除旧的图像文件
			if(old_f != null) {		
				fileMapper.deleteByPrimaryKey(old_f.getId());
				ShiroUtils.deleteFile(old_f);
			}
		}
		// 更新图像
		user.setHeadImg(fileinfo.getId());
		fileMapper.insertSelective(fileinfo);
		userMapper.updateByPrimaryKeySelective(user);
	}

	public int setUserinfo(User user) {
		return userMapper.updateByPrimaryKeySelective(user);
	}
}
