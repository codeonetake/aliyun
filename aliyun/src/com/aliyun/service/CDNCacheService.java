package com.aliyun.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliyun.bean.CDNDetail;
import com.aliyun.bean.CDNTongji;
import com.aliyun.bean.ImgCdn;
import com.aliyun.util.CacheByPage;
import com.aliyun.util.CacheType;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;
import com.aliyun.util.OssOperate;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

public class CDNCacheService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd HH:mm");
	private static int maxCount = 168;
	
	public static void refresh(){
		msg="";
		String url = OssConfig.getValue("cdnUrl");
		String[] urls = url.split("\\|");
		m("检测CDN的url开始");
		Map<String, String> maps = null;
		Set<String> keys = null;
		Map<String, Integer> typeCountMap = new HashMap<String, Integer>();
		String type = null;
		Integer typeCount = 1;
		for (String u : urls) {
			u = "http://"+u;
			m("当前测试的URL：" + u);
			maps = CacheByPage.get(u);
			keys = maps.keySet();
			for (String key : keys) {
				type = maps.get(key);
				m(key + "，状态：" + type);
				typeCount = typeCountMap.get(type);
				if(typeCount == null){
					typeCountMap.put(type, 1);
				}else{
					typeCountMap.put(type, typeCount + 1);
				}
			}
			m("====================");
		}
		m("检测CDN的url结束");
		ObjSave.objectToFile(msg, "/root/data/aliyun/cdn.ser");
		//添加统计
		String redisKey = "CDNTongji";
		String tongjiJson = RedisPool.get(redisKey);
		CDNDetail imgCDNDetail = new CDNDetail();
		Integer hitCount = typeCountMap.get("HIT");
		if(hitCount == null){
			hitCount = 0;
		}
		imgCDNDetail.setHitCount(hitCount);
		
		Integer missCount = typeCountMap.get("MISS");
		if(missCount == null){
			missCount = 0;
		}
		imgCDNDetail.setMissCount(missCount);
		imgCDNDetail.setTime(timeFormat.format(new Date()));
		
		CDNTongji imgCDNTongji = null;
		if(null != tongjiJson){
			imgCDNTongji = new Gson().fromJson(tongjiJson, CDNTongji.class);
			List<CDNDetail> details = imgCDNTongji.getDetails();
			int size = details.size();
			if(size>maxCount){
				details = details.subList(size - maxCount,size);
			}
			details.add(imgCDNDetail);
			imgCDNTongji.setDetails(details);
		}else{
			imgCDNTongji = new CDNTongji();
			List<CDNDetail> details = new ArrayList<CDNDetail>();
			details.add(imgCDNDetail);
			imgCDNTongji.setDetails(details);
		}
		RedisPool.set(redisKey, new Gson().toJson(imgCDNTongji));
	}
	
	public static void refreshAllImg(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = "图片开始刷新时间："+format.format(new Date())+"\n";
		long l = System.currentTimeMillis();
		List<String> list = null;
		String key = "imgCdnUrl";
		String timeKey = "imgCdnTime";
		if(RedisPool.isExist(key)){
			result += "从Redis获取数据\n";
			ImgCdn imgCdn = new Gson().fromJson(RedisPool.get(key), ImgCdn.class);
			list = imgCdn.getCdnUrl();
		}else{
			result += "从OSS中获取数据\n";
			list = OssOperate.listLastPage("wp-codeawl",-1);
			ImgCdn imgCdn = new ImgCdn();
			imgCdn.setCdnUrl(list);
			String date = dateTimeFormat.format(new Date());
			RedisPool.set(key, new Gson().toJson(imgCdn), 60 * 60 * 24);
			RedisPool.set(timeKey, date);
		}
		if(RedisPool.isExist(timeKey)){
			result += "Redis缓存时间："+RedisPool.get(timeKey)+"\n";
		}
		String u = null;
		String type = null;
		Map<String, Integer> typeMap = new HashMap<String, Integer>();
		List<String> missUrls = new ArrayList<String>();
		for (String string : list) {
			u = "http://img.codeawl.com/"+string;
			type = CacheType.get(u);
			if(typeMap.containsKey(type)){
				typeMap.put(type, typeMap.get(type) + 1);
			}else{
				typeMap.put(type, 1);
			}
			if("MISS".equals(type) && missUrls.size() <= 5){
				missUrls.add(u);
			}
		}
		Set<String> keys = typeMap.keySet();
		for (String string : keys) {
			result += string + "有" + typeMap.get(string) + "个\n";
		}
		
		if(missUrls.size() != 0){
			result += "MISS的图片示例：\n";
			for (String missUrl : missUrls) {
				result += missUrl + "\n";
			}
		}
		result += "图片刷新结束时间："+format.format(new Date())+"\n";
		result += "耗时："+(System.currentTimeMillis() - l)+"ms";
		ObjSave.objectToFile(result, "/root/data/aliyun/imgCdn.ser");
		//添加统计信息
		String redisKey = "imgCDNTongji";
		String tongjiJson = RedisPool.get(redisKey);
		CDNDetail imgCDNDetail = new CDNDetail();
		Integer hitCount = typeMap.get("HIT");
		if(hitCount == null){
			hitCount = 0;
		}
		imgCDNDetail.setHitCount(hitCount);
		
		Integer missCount = typeMap.get("MISS");
		if(missCount == null){
			missCount = 0;
		}
		imgCDNDetail.setMissCount(missCount);
		imgCDNDetail.setTime(timeFormat.format(new Date()));
		
		CDNTongji imgCDNTongji = null;
		if(null != tongjiJson){
			imgCDNTongji = new Gson().fromJson(tongjiJson, CDNTongji.class);
			List<CDNDetail> details = imgCDNTongji.getDetails();
			int size = details.size();
			if(size>maxCount){
				details = details.subList(size - maxCount,size);
			}
			details.add(imgCDNDetail);
			imgCDNTongji.setDetails(details);
		}else{
			imgCDNTongji = new CDNTongji();
			List<CDNDetail> details = new ArrayList<CDNDetail>();
			details.add(imgCDNDetail);
			imgCDNTongji.setDetails(details);
		}
		RedisPool.set(redisKey, new Gson().toJson(imgCDNTongji));
	}
	public static void main(String[] args) {
		refreshAllImg();
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
