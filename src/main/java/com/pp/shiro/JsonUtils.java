package com.pp.shiro;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonUtils {
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static <T> String toJsonStr(T obj){
		if(obj != null) {			
			if(obj.getClass().isArray() || obj instanceof List) {
				return JSONArray.fromObject(obj).toString();
			}else {			
				return JSONObject.fromObject(obj).toString();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T toBean(String str, Class<T> clazz){
		if(isNotEmpty(str) && clazz != null) {			
			JSONObject object = JSONObject.fromObject(str);	
			return (T) JSONObject.toBean(object, clazz);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(String str, Class<T> clazz) {
		if(isNotEmpty(str) && clazz != null) {
			JSONArray json = JSONArray.fromObject(str);
			List<T> list = new ArrayList<T>();
			for (int i = 0; i < json.size(); i++) {
				list.add((T) JSONObject.toBean(JSONObject.fromObject(json.get(i)), clazz));
			}
			return list;
		}
		return null;
    }
}