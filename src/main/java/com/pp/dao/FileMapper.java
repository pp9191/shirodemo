package com.pp.dao;

import org.apache.ibatis.annotations.Mapper;

import com.pp.entity.File;

@Mapper
public interface FileMapper {
    Integer deleteByPrimaryKey(Long id);

    Integer insert(File record);

    Integer insertSelective(File record);

    File selectByPrimaryKey(Long id);

    Integer updateByPrimaryKeySelective(File record);

    Integer updateByPrimaryKey(File record);
}