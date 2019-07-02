package com.pp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pp.dao.FileInfoMapper;
import com.pp.dao.UserMapper;
import com.pp.entity.FileInfo;
import com.pp.entity.User;

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
		User user =new User();
		user.setId(Long.decode(userId));
		user.setHeadImg(fileinfo.getId());
		fileMapper.insertSelective(fileinfo);
		userMapper.updateByPrimaryKeySelective(user);
	}
}
