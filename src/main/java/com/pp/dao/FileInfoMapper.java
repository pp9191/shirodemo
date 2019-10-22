package com.pp.dao;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.pp.entity.FileInfo;

public interface FileInfoMapper {
	
	@CacheEvict(value="fileinfo", key="#id")
    int deleteByPrimaryKey(String id);
    
    int insert(FileInfo record);

    int insertSelective(FileInfo record);

    @Cacheable(value="fileinfo", key="#id", unless="#result == null")
    FileInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FileInfo record);

    int updateByPrimaryKey(FileInfo record);
}