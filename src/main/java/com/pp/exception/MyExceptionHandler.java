package com.pp.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class MyExceptionHandler {
	
	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;
    
	/* spring默认上传大小1MB 超出大小捕获异常MaxUploadSizeExceededException */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Map<String, Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    	Map<String, Object> map = new HashMap<>();
    	map.put("result", "false");
    	map.put("message", "文件大小超出" + maxFileSize + "限制, 请压缩或降低文件质量!");
        return map;
    }
}
