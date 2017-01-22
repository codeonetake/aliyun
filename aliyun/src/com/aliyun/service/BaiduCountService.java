package com.aliyun.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.aliyun.util.ObjSave;

public class BaiduCountService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void get() throws Exception {
		String url = "http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=site%3Acodeawl.com";
		Document document = Jsoup.connect(url).get();
		Element element = document.getElementById("content_left").getElementsByTag("b").get(0);
		m("codeawl.com");
		m(element.html()+"，结束");
		ObjSave.objectToFile(msg, "/root/data/aliyun/baiduCount.ser");
	}
	public static void main(String[] args) {
		try {
			get();
		} catch (Exception e) {
			// TODO: handle exception
		}
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
