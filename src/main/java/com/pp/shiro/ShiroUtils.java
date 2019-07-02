package com.pp.shiro;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class ShiroUtils {
	
	public static final String ALGORITHMNAME = "md5";
	public static final int ITERATIONS = 2;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String encryptPassword(String password, String salt) {
        SimpleHash hash = new SimpleHash(ALGORITHMNAME, password, getByteSource(salt), ITERATIONS);
        return hash.toString();
    }
	
	public static ByteSource getByteSource(String salt) {
		ByteSource btSalt = ByteSource.Util.bytes(salt);
        return btSalt;
    }
	
	public static String getDatePath(Date date) {
		String dateStr = sdf.format(date);		
		return File.separator.concat(dateStr.replace("-", File.separator));
	}

}
