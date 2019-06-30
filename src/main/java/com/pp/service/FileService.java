package com.pp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pp.dao.FileMapper;
import com.pp.entity.File;

@Repository("fileService")
public class FileService {
	
	@Autowired
	private FileMapper fileMapper;
	
	private Integer insert(File record) {
		
		return fileMapper.insertSelective(record);
	}
	
}
