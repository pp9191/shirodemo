package com.pp.dao;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.pp.entity.FileInfo;

public interface FileInfoMapper {
	
	@CacheEvict(value="fileinfo", key="#id")
    int deleteByPrimaryKey(String id);

    @CachePut(value="fileinfo", key="#record.id")
    int insert(FileInfo record);

    @CachePut(value="fileinfo", key="#record.id")
    int insertSelective(FileInfo record);

    @Cacheable(value="fileinfo", key="#id")
    FileInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FileInfo record);

    int updateByPrimaryKey(FileInfo record);
}