package com.pp.dao;

import org.apache.ibatis.annotations.Mapper;

import com.pp.entity.FileInfo;

@Mapper
public interface FileInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(FileInfo record);

    int insertSelective(FileInfo record);

    FileInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FileInfo record);

    int updateByPrimaryKey(FileInfo record);
}