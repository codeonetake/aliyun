package com.aliyun.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.aliyun.util.CacheByPage;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;

public class CDNCacheService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void refresh(){
		msg="";
		String url = OssConfig.getValue("cdnUrl");
		String[] urls = url.split("\\|");
		m("检测CDN的url开始");
		Map<String, String> maps = null;
		Set<String> keys = null;
		for (String u : urls) {
			u = "http://"+u;
			m("当前测试的URL：" + u);
			maps = CacheByPage.get(u);
			keys = maps.keySet();
			for (String key : keys) {
				m(key + "，状态：" + maps.get(key));
			}
			m("====================");
		}
		m("检测CDN的url结束");
		ObjSave.objectToFile(msg, "/root/data/aliyun/cdn.ser");
	}
	public static void main(String[] args) {
		refresh();
	}

	private static void m(String content){
		if(content.contains("(")){
			content = content.replaceAll("\\(", "<b>");
		}
		if(content.contains(")")){
			content = content.replaceAll("\\)", "</b>");
		}
		if(content.contains("[")){
			content = content.replaceAll("\\[", "<code>");
		}
		if(content.contains("]")){
			content = content.replaceAll("\\]", "</code>");
		}
		if(content.contains("成功")){
			content = content.replaceAll("成功", "<font color='green'><b>成功</b></font>");
		}
		if(content.contains("失败")){
			content = content.replaceAll("失败", "<font color='red'><b>失败</b></font>");
		}
		if(content.endsWith("开始") || content.endsWith("结束")){
			String time = dateTimeFormat.format(new Date());
			msg += content + "，时间："+time+"<br/>";
		}else{
			content = content.replaceAll("HIT", "<font color='green'>HIT</font>")
					.replaceAll("MISS", "<font color='red'>MISS</font>");
			msg += content + "<br/>";
		}
	}
}
