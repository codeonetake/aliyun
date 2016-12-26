package com.aliyun.util;

import java.util.List;

public class CacheType {
	public static String get(String url){
		String type = "NONE";
		try {
			List<String> list = DoShell.shell("curl -I " + url + "|grep 'X-Cache'");
			for (String content : list) {
				if(content.startsWith("X-Cache")){
					type = content.split("\\s+")[1]; 
					break;
				}
			}
		} catch (Exception e) {
			return "ERROR";
		}
		return type;
	}
	
}
