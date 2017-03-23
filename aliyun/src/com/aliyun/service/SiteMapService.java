package com.aliyun.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aliyun.util.ObjSave;
import com.aliyun.util.SEOUtil;

public class SiteMapService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void make(){
		msg="";
		m("生成sitemap开始");
		try {
			int count = SEOUtil.makeSiteMap();
			m("生成sitemap成功结束，URL一共有"+count+"个");
		} catch (Exception e) {
			m("生成sitemap失败结束");
		}
		System.out.println(msg);
		ObjSave.objectToFile(msg, "/root/data/aliyun/sitemap.ser");
	}
	public static void main(String[] args) {
		make();
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
