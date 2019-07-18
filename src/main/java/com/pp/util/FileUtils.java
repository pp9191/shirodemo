package com.pp.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pp.entity.FileInfo;

public class FileUtils {	

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static void deleteFile(FileInfo fileinfo) {
		if(fileinfo != null) {			
			File file = new File(fileinfo.getPath(), fileinfo.getFilename());
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	public static String getDatePath(Date date) {
		String dateStr = sdf.format(date);		
		return File.separator.concat(dateStr.replace("-", File.separator));
	}

}
