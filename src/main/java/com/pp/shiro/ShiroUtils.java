package com.pp.shiro;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class ShiroUtils {
	
	public static final String ALGORITHMNAME = "md5";
	public static final int ITERATIONS = 2;
	
	public static String encryptPassword(String password, String salt) {
        SimpleHash hash = new SimpleHash(ALGORITHMNAME, password, getByteSource(salt), ITERATIONS);
        return hash.toString();
    }
	
	public static ByteSource getByteSource(String salt) {
		ByteSource btSalt = ByteSource.Util.bytes(salt);
        return btSalt;
    }

}
