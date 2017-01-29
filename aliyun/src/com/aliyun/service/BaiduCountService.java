package com.aliyun.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.aliyun.bean.BaiduDetail;
import com.aliyun.bean.BaiduTongji;
import com.aliyun.util.ObjSave;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

public class BaiduCountService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static int maxCount = 365;
	
	public static void get() throws Exception {
		String url = "http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=site%3Acodeawl.com";
		Document document = Jsoup.connect(url).get();
		Element element = document.getElementById("content_left").getElementsByTag("b").get(0);
		m("codeawl.com");
		String html = element.html();
		m(html+"，结束");
		ObjSave.objectToFile(msg, "/root/data/aliyun/baiduCount.ser");
		try {
			int count = Integer.parseInt(html.substring(html.indexOf("约")+1,html.length()-1));
			BaiduDetail detail = new BaiduDetail();
			detail.setCount(count);
			detail.setTime(dateFormat.format(new Date()));
			String redisKey = "BaiduCountTongji";
			BaiduTongji baiduTongji = null;
			if(RedisPool.isExist(redisKey)){
				baiduTongji = new Gson().fromJson(RedisPool.get(redisKey), BaiduTongji.class);
				List<BaiduDetail> details = baiduTongji.getDetails();
				int size = details.size();
				if(details.size()>maxCount){
					details = details.subList(size - maxCount,size);
				}
				details.add(detail);
				baiduTongji.setDetails(details);
			}else{
				baiduTongji = new BaiduTongji();
				List<BaiduDetail> details = new ArrayList<BaiduDetail>();
				details.add(detail);
				baiduTongji.setDetails(details);
			}
			RedisPool.set(redisKey, new Gson().toJson(baiduTongji));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
