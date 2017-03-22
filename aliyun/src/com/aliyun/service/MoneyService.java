package com.aliyun.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliyun.bean.MoneyDetail;
import com.aliyun.bean.MoneyMonthDetail;
import com.aliyun.bean.MoneyMonthTongji;
import com.aliyun.bean.MoneyTongji;
import com.aliyun.util.ImgUtil;
import com.aliyun.util.OssConfig;
import com.aliyun.util.RedisPool;
import com.common.util.EmailUtil;
import com.google.gson.Gson;

public class MoneyService {
	private static String destFilePath = OssConfig.getValue("ocrFilePath");
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	private static int maxCount = 365;
	private static int moneyMonthMaxCount = 24;
	public static void dealMoney(String filePath){
		//利息
		try {
			boolean res = dealSinglePic("upMoneyTongji",filePath,"1.png",0, 210, 300, 135);
			if(!res){
				EmailUtil.sendEmail("[CODEAWL]记录利息失败", "RT", "codeawl@163.com");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				EmailUtil.sendEmail("[CODEAWL]记录利息失败", "RT", "codeawl@163.com");
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		//本金
		try {
			boolean res = dealSinglePic("baseMoneyTongji",filePath,"2.png",380, 250, 370, 120);
			if(!res){
				EmailUtil.sendEmail("[CODEAWL]记录总资产失败", "RT", "codeawl@163.com");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				EmailUtil.sendEmail("[CODEAWL]记录总资产失败", "RT", "codeawl@163.com");
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
	}
	
	private static boolean dealSinglePic(String redisKey,String origName,String fileName,int x,int y,int width,int height) throws Exception{
		Date time = new Date();
		String date = format.format(time);
		MoneyTongji tongji = null;
		if(RedisPool.isExist(redisKey)){
			tongji = new Gson().fromJson(RedisPool.get(redisKey), MoneyTongji.class);
		}else{
			tongji = new MoneyTongji();
		}
		List<MoneyDetail> details = tongji.getDetails();
		if(null == details){
			details = new ArrayList<MoneyDetail>();
		}
		MoneyDetail moneyDetail = new MoneyDetail();
		moneyDetail.setTime(date);
		if(details.contains(moneyDetail)){
			System.out.println("已经存在"+date+"号的记录，跳过");
			return true;
		}
		
		ImgUtil.cutCenterImage(origName,destFilePath+fileName, x, y, width, height);
		String text = ImgUtil.recognizeText(destFilePath+fileName);
		Double money = Double.parseDouble(text);
		moneyDetail.setMoney(money);
		
		int size = details.size();
		if(size>maxCount){
			details = details.subList(size - maxCount,size);
		}
		details.add(moneyDetail);
		tongji.setDetails(details);
		
		RedisPool.set(redisKey, new Gson().toJson(tongji));
		//删除图片
		new File(destFilePath+fileName).delete();
		//开始添加月度
		try {
			if(redisKey.equals("upMoneyTongji")){
				String month = monthFormat.format(time);
				String rKey = "moneyMonth";
				String json = RedisPool.get(rKey);
				MoneyMonthTongji moneyMonthTongji = new Gson().fromJson(json, MoneyMonthTongji.class);
				List<MoneyMonthDetail> moneyMonthDetails = moneyMonthTongji.getDetails();
				boolean isAdd = false;
				for (MoneyMonthDetail moneyMonthDetail : moneyMonthDetails) {
					if(moneyMonthDetail.getMonth().equals(month)){
						isAdd = true;
						moneyMonthDetail.setMoney(NumberUtil.formatDouble(Double.parseDouble(moneyMonthDetail.getMoney()) + money));
						break;
					}
				}
				if(!isAdd){
					MoneyMonthDetail moneyMonthDetail = new MoneyMonthDetail();
					moneyMonthDetail.setMonth(month);
					moneyMonthDetail.setMoney(NumberUtil.formatDouble(money));
					moneyMonthDetails.add(moneyMonthDetail);
				}
				size = moneyMonthDetails.size();
				if(size > moneyMonthMaxCount){
					moneyMonthDetails = moneyMonthDetails.subList(size - moneyMonthMaxCount,size);
				}
				moneyMonthTongji.setDetails(moneyMonthDetails);
				RedisPool.set(rKey, new Gson().toJson(moneyMonthTongji));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		String json = RedisPool.get("upMoneyTongji");
		MoneyTongji tongji = new Gson().fromJson(json, MoneyTongji.class);
		List<MoneyDetail> list = tongji.getDetails();
		Date date = null;
		String month = "";
		Map<String, String> moneyMap = new HashMap<String, String>();
		for (MoneyDetail moneyDetail : list) {
			date = format.parse(moneyDetail.getTime());
			month = monthFormat.format(date);
			if(moneyMap.containsKey(month)){
				moneyMap.put(month,NumberUtil.formatDouble(Double.parseDouble(moneyMap.get(month)) + moneyDetail.getMoney()));
			}else{
				moneyMap.put(month, NumberUtil.formatDouble(moneyDetail.getMoney()));
			}
		}
		Set<String> moneyKey = moneyMap.keySet();
		List<MoneyMonthDetail> moneyMonthDetails = new ArrayList<MoneyMonthDetail>();
		MoneyMonthDetail moneyMonthDetail = null;
		for (String mk : moneyKey) {
			moneyMonthDetail = new MoneyMonthDetail();
			moneyMonthDetail.setMonth(mk);
			moneyMonthDetail.setMoney(moneyMap.get(mk));
			moneyMonthDetails.add(moneyMonthDetail);
		}
		MoneyMonthTongji moneyMonthTongji = new MoneyMonthTongji();
		moneyMonthTongji.setDetails(moneyMonthDetails);
		String key = "moneyMonth";
		RedisPool.set(key, new Gson().toJson(moneyMonthTongji));
		
		//dealSinglePic("baseMoneyTongji","/Users/liuwenbin/Desktop/IMG_8372.PNG","2.png",380, 250, 370, 120);
		//ImgUtil.cutCenterImage("/Users/liuwenbin/Downloads/IMG_8377.PNG","/Users/liuwenbin/Desktop/test.png", 0, 210, 300, 135);
		//ImgUtil.cutCenterImage("/Users/liuwenbin/Downloads/IMG_8372.PNG","/Users/liuwenbin/Desktop/test.png", 380, 250, 370, 120);
		//String text = ImgUtil.recognizeText("http://aliyun.codeawl.com/img/test.png");
		//System.out.println(text);
	}
	
	public static void checkTodayStatus() {
		MoneyDetail moneyDetail = new MoneyDetail();
		moneyDetail.setTime(format.format(new Date()));
		
		MoneyTongji upMoneyTongji = new Gson().fromJson(RedisPool.get("upMoneyTongji"), MoneyTongji.class);
		List<MoneyDetail> upMoneyDetails = upMoneyTongji.getDetails();
		String content = "";
		if(!upMoneyDetails.contains(moneyDetail)){
			content += "今天没有提交利息值数据<br/>";
		}
		
		MoneyTongji baseMoneyTongji = new Gson().fromJson(RedisPool.get("baseMoneyTongji"), MoneyTongji.class);
		List<MoneyDetail> baseMoneyDetails = baseMoneyTongji.getDetails();
		if(!baseMoneyDetails.contains(moneyDetail)){
			content += "今天没有提交总资产数据<br/>";
		}
		
		if(!"".equals(content)){
			try {
				EmailUtil.sendEmail("[CODEAWL]没有提交资产数据，请尽快提交", content, "codeawl@163.com");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
