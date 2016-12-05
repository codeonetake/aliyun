package com.aliyun.util;
import java.util.ResourceBundle;

public class OssConfig {
	/**
	 * 配置文件名称
	 */
	private static final String baseName = "oss";
	
	private static ResourceBundle rb = null;
	
	static {
		 if(rb == null ){
			 rb = ResourceBundle.getBundle(baseName);
		 }
	}

	/**
	 * 获取配置文件的值
	 * @param key
	 * @return value
	 */
	public static String getValue(String key){
		return getRBString(rb, key);
	}
	
	private static String getRBString(ResourceBundle rb,String key){
		if(rb != null && rb.containsKey(key)){
			return rb.getString(key);
		}
		return "";
	}
}