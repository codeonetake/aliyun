package com.aliyun.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliyun.bean.BaiduSpiderInfoDetail;
import com.aliyun.bean.BaiduSpiderIpDetail;
import com.aliyun.bean.BaiduSpiderTongji;
import com.aliyun.util.DoShell;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

public class BaiduSpiderService {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void getSpiderInfo() throws Exception{
		String time = dateFormat.format(new Date());
		String redisKey = "baiduSpiderTongji";
		List<String> res = DoShell.shell("cat /var/log/nginx/access.log | grep 'Baiduspider'");
		if(null == res || res.size() == 0){
			return;
		}
		BaiduSpiderTongji tongji = new BaiduSpiderTongji();
		tongji.setTime(time);
		tongji.setOriginInfo(res);
		//解析各个IP端和数量
		String ipSplit = null;
		List<BaiduSpiderIpDetail> ipDetails = new ArrayList<BaiduSpiderIpDetail>();
		BaiduSpiderIpDetail ipDetail = null;
		Map<String, Integer> ipCountMap = new HashMap<String, Integer>();
		
		List<BaiduSpiderInfoDetail> infoDetails = new ArrayList<BaiduSpiderInfoDetail>();
		BaiduSpiderInfoDetail infoDetail = null;
		String ip = null;
		String url = null;
		String responseCode = null;
		for (String re : res) {
			System.out.println(re);
			ip = re.substring(0,re.indexOf(" "));
			if(!isRealBaiduSpider(ip)){
				continue;
			}
			ipSplit = ip.substring(0,ip.lastIndexOf("."));
			if(ipCountMap.containsKey(ipSplit)){
				ipCountMap.put(ipSplit, ipCountMap.get(ipSplit) + 1);
			}else{
				ipCountMap.put(ipSplit, 1);
			}
			//获取详细信息
			try {
				infoDetail = new BaiduSpiderInfoDetail();
				infoDetail.setIp(ip);
				infoDetail.setTime(getTime(re));
				if(re.contains("GET")){
					url = re.split("GET")[1].trim();
					url = url.substring(0,url.indexOf(" "));
					infoDetail.setUrl(url);
				}else{
					infoDetail.setUrl("解析url失败");
				}
				if(re.contains("HTTP/1.1")){
					re = re.split("HTTP/1.1\" ")[1];
				}else if(re.contains("HTTP/1.0")){
					re = re.split("HTTP/1.0\" ")[1];
				}else{
					re = "获取返回码失败";
				}
				responseCode = re.substring(0,3);
				infoDetail.setResponseCode(responseCode);
				infoDetails.add(infoDetail);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		tongji.setInfoDetails(infoDetails);
		Set<String> ipSplits = ipCountMap.keySet();
		for (String ipKey : ipSplits) {
			ipDetail = new BaiduSpiderIpDetail();
			ipDetail.setIpSplit(ipKey);
			ipDetail.setCount(ipCountMap.get(ipKey));
			ipDetails.add(ipDetail);
		}
		tongji.setIpDetails(ipDetails);
		RedisPool.set(redisKey, new Gson().toJson(tongji));
	}
	private static boolean isRealBaiduSpider(String ip) {
		List<String> shells = null;
		try {
			shells = DoShell.shell("host "+ip);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(null == shells){
			return false;
		}
		String shell = shells.get(0);
		if(shell.contains("baidu.com") || shell.contains("baidu.jp")){
			return true;
		}
		return false;
	}
	public static void main(String[] args) throws Exception {
		getSpiderInfo();
		//System.out.println(isRealBaiduSpider("125.42.177.70"));
		//System.out.println(isRealBaiduSpider("123.125.71.19"));
		//getTime("220.181.108.163 - - [01/Feb/2017:12:45:40 +0800] \"GET /sitemap_baidu.xml HTTP/1.1\" 200 3706 \"-\" \"Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)\" \"-\"");
	}
	private static String getTime(String info) throws Exception{
		try {
			info = info.substring(info.indexOf("[")+1,info.indexOf("]")).trim().split(" ")[0];
			if(info.contains("Jan")){
				info = info.replaceAll("Jan", "01");
			}else if (info.contains("Feb")) {
				info = info.replaceAll("Feb", "02");
			}else if (info.contains("Mar")) {
				info = info.replaceAll("Mar", "03");
			}else if (info.contains("Apr")) {
				info = info.replaceAll("Apr", "04");
			}else if (info.contains("May")) {
				info = info.replaceAll("May", "05");
			}else if (info.contains("Jun")) {
				info = info.replaceAll("Jun", "06");
			}else if (info.contains("Jul")) {
				info = info.replaceAll("Jul", "07");
			}else if (info.contains("Agu")) {
				info = info.replaceAll("Agu", "08");
			}else if (info.contains("Sep")) {
				info = info.replaceAll("Sep", "09");
			}else if (info.contains("Oct")) {
				info = info.replaceAll("Oct", "10");
			}else if (info.contains("Nov")) {
				info = info.replaceAll("Nov", "11");
			}else if (info.contains("Dec")) {
				info = info.replaceAll("Dec", "12");
			}
			info = dateTimeFormat.format(timeFormat.parse(info));
		} catch (Exception e) {
			info = "时间解析有误";
		}
		return info;
	}
}
