package com.pp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pp.dao.FileInfoMapper;
import com.pp.entity.FileInfo;

@Repository("fileService")
public class FileService {
	
	@Autowired
	private FileInfoMapper fileMapper;
	
	public FileInfo getFileInfo(String id) {
		
		return fileMapper.selectByPrimaryKey(id);
	}
	
}
