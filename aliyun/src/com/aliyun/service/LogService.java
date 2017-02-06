package com.aliyun.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliyun.bean.LogTypeCountDetail;
import com.aliyun.bean.LogTypeCountTongji;
import com.aliyun.bean.LogTypeInfoTongji;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

public class LogService {
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static void get() throws Exception {
		String time = format.format(new Date());
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/var/log/nginx/access.log")));
		String line;
		Map<String, Integer> typeMap = new HashMap<String, Integer>();
		Map<String, List<String>> typeInfoMap = new HashMap<String, List<String>>();
		String type = null;
		List<String> infoList = null;
		String originLine = null;
		while((line = bufferedReader.readLine()) != null){
			if(line.contains("alert(")){
				line = line.replaceAll("alert\\(", "\\(");
			}
			originLine = line;
			if(line.equals("")){
				continue;
			}
			if(line.contains("HTTP/1.1")){
				line = line.split("HTTP/1.1\" ")[1];
			}else if(line.contains("HTTP/1.0")){
				line = line.split("HTTP/1.0\" ")[1];
			}else{
				continue;
			}
			type = line.substring(0,3);
			if(!"200".equals(type)){
				infoList = typeInfoMap.get(type);
				if(infoList == null){
					infoList = new ArrayList<String>();
				}
				if(infoList.size() < 11){
					infoList.add(originLine);
					typeInfoMap.put(type, infoList);
				}
			}
			if(typeMap.containsKey(type)){
				typeMap.put(type, typeMap.get(type) + 1);
			}else{
				typeMap.put(type, 1);
			}
		}
		bufferedReader.close();
		Set<String> keys = typeMap.keySet();
		LogTypeCountTongji logTypeCountTongji = new LogTypeCountTongji();
		LogTypeCountDetail detail = null;
		List<LogTypeCountDetail> details = new ArrayList<LogTypeCountDetail>();
		logTypeCountTongji.setTime(time);
		for (String key : keys) {
			detail = new LogTypeCountDetail();
			detail.setType(key);
			detail.setCount(typeMap.get(key));
			details.add(detail);
		}
		logTypeCountTongji.setDetails(details);
		RedisPool.set("logTypeCountTongji", new Gson().toJson(logTypeCountTongji));
		
		keys = typeInfoMap.keySet();
		LogTypeInfoTongji tongji = null;
		for (String logType : keys) {
			List<String> list = typeInfoMap.get(logType);
			tongji = new LogTypeInfoTongji();
			tongji.setTime(time);
			tongji.setInfos(list);
			RedisPool.set("logTypeInfo_"+logType, new Gson().toJson(tongji));
		}
	}
	
	public static List<String> getError() throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/var/log/nginx/error.log")));
		String line;
		List<String> errorInfo = new ArrayList<String>();
		while((line = bufferedReader.readLine()) != null){
			if(line.contains("[error]")){
				if(line.contains("alert(")){
					line = line.replaceAll("alert\\(", "\\(");
				}
				errorInfo.add(line);
			}
		}
		bufferedReader.close();
		return errorInfo;
	}
	public static List<String> getWarn() throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/var/log/nginx/error.log")));
		String line;
		List<String> errorInfo = new ArrayList<String>();
		while((line = bufferedReader.readLine()) != null){
			if(line.contains("[warn]")){
				if(line.contains("alert(")){
					line = line.replaceAll("alert\\(", "\\(");
				}
				errorInfo.add(line);
			}
		}
		bufferedReader.close();
		return errorInfo;
	}
}
