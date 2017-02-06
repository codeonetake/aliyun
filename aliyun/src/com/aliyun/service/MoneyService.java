package com.aliyun.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aliyun.bean.MoneyDetail;
import com.aliyun.bean.MoneyTongji;
import com.aliyun.util.ImgUtil;
import com.aliyun.util.OssConfig;
import com.aliyun.util.RedisPool;
import com.common.util.EmailUtil;
import com.google.gson.Gson;

public class MoneyService {
	private static String destFilePath = OssConfig.getValue("ocrFilePath");
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private static int maxCount = 365;
	public static void dealMoney(String filePath){
		//利息
		try {
			boolean res = dealSinglePic("upMoneyTongji",filePath,"1.PNG",0, 200, 450, 100);
			if(!res){
				EmailUtil.sendEmail("[CODEAWL]记录利息失败", "RT", "codeawl@163.com");
			}
		} catch (Exception e) {
			try {
				EmailUtil.sendEmail("[CODEAWL]记录利息失败", "RT", "codeawl@163.com");
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		//本金
		try {
			boolean res = dealSinglePic("baseMoneyTongji",filePath,"2.PNG",450, 200, 300, 100);
			if(!res){
				EmailUtil.sendEmail("[CODEAWL]记录总资产失败", "RT", "codeawl@163.com");
			}
		} catch (Exception e) {
			try {
				EmailUtil.sendEmail("[CODEAWL]记录总资产失败", "RT", "codeawl@163.com");
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
	}
	
	private static boolean dealSinglePic(String redisKey,String origName,String fileName,int x,int y,int width,int height) throws Exception{
		String date = format.format(new Date());
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
		
		String url = "http://aliyun.codeawl.com/img/"+fileName;
		ImgUtil.cutCenterImage(origName,destFilePath+fileName, x, y, width, height);
		String text = ImgUtil.recognizeText(url);
		if(text.contains("wait")){
			return false;
		}
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
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		//dealMoney("/root/data/aliyun/image/3.PNG");
		checkTodayStatus();
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
