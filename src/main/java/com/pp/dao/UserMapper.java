package com.pp.dao;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.pp.entity.User;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    @CacheEvict(value="users", key="#record.account")
    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    @Cacheable(value="users", key="#account")
	User selectByAccount(String account);

	List<User> getUsers(Map<String, Object> params);
	
	int getUsersCount(Map<String, Object> params);
}