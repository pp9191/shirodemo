package com.pp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pp.dao.UserMapper;
import com.pp.entity.User;

@Repository("userService")
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
	public Integer addUser(User user) {
		
		return userMapper.insertSelective(user);
	}
	
	public User selectByAccount(String account) {
		
		return userMapper.selectByAccount(account);
	}

	public List<User> getUsers() {
		
		return userMapper.getUsers();
	}
}
