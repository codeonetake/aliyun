package com.aliyun.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CacheByPage {
	private static String[] cdnHead = {"http://i.codeawl","http://cdn.codeawl","http://img.codeawl"};
	public static Map<String, String> get(String url){
		Map<String, String> result = new HashMap<String, String>();
		try {
			Set<String> tempSet = new TreeSet<String>();
			Document document = Jsoup.connect(url).get();
			//获取所有的links
			Elements urls = document.getElementsByTag("link");
			String cdnUrl = null;
			for (Element element : urls) {
				cdnUrl = element.attr("href");
				if(cdnUrl.startsWith("images") || cdnUrl.startsWith("css") || cdnUrl.startsWith("assets") || cdnUrl.startsWith("js")){
					cdnUrl = url + "/" + cdnUrl;
				}
				if(isCdn(cdnUrl)){
					tempSet.add(cdnUrl);
				}
			}
			//获取所有的js
			urls = document.getElementsByTag("script");
			for (Element element : urls) {
				cdnUrl = element.attr("src");
				if(cdnUrl.startsWith("images") || cdnUrl.startsWith("css") || cdnUrl.startsWith("assets") || cdnUrl.startsWith("js")){
					cdnUrl = url + "/" + cdnUrl;
				}
				if(isCdn(cdnUrl)){
					tempSet.add(cdnUrl);
				}
			}
			//获取所有的img
			urls = document.getElementsByTag("img");
			for (Element element : urls) {
				cdnUrl = element.attr("src");
				if(cdnUrl.startsWith("images") || cdnUrl.startsWith("css") || cdnUrl.startsWith("assets") || cdnUrl.startsWith("js")){
					cdnUrl = url + "/" + cdnUrl;
				}
				if(isCdn(cdnUrl)){
					tempSet.add(cdnUrl);
				}
			}
			String cdnType = null;
			for (String temp : tempSet) {
				cdnType = CacheType.get(temp);
				result.put(temp, cdnType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	private static boolean isCdn(String url){
		if(!url.startsWith("http")){
			url = "http://" + url;
		}
		for(String cdn:cdnHead){
			if(url.startsWith(cdn)){
				return true;
			}
		}
		return false;
	}
}
