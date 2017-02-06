package com.aliyun.controller;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import sun.management.counter.Variability;

import com.aliyun.bean.BaiduDetail;
import com.aliyun.bean.BaiduSpiderIpDetail;
import com.aliyun.bean.BaiduSpiderTongji;
import com.aliyun.bean.BaiduTongji;
import com.aliyun.bean.CDNDetail;
import com.aliyun.bean.CDNTongji;
import com.aliyun.bean.DiskTongji;
import com.aliyun.bean.LogTypeCountDetail;
import com.aliyun.bean.LogTypeCountTongji;
import com.aliyun.bean.LogTypeInfoTongji;
import com.aliyun.bean.MemDetail;
import com.aliyun.bean.MemTongji;
import com.aliyun.bean.MoneyDetail;
import com.aliyun.bean.MoneyTongji;
import com.aliyun.service.LogService;
import com.aliyun.service.MoneyService;
import com.aliyun.service.NumberUtil;
import com.aliyun.util.DoShell;
import com.aliyun.util.OssConfig;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

@Controller
@RequestMapping(value="/tongji")
public class TongjiController {

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	@RequestMapping(value="/cdn")
	public ModelAndView cdnIndex(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		String imgCdnTongji = RedisPool.get("imgCDNTongji");
		CDNTongji cdnTongji = new Gson().fromJson(imgCdnTongji, CDNTongji.class);
		String imgHit = "[";
		String imgMiss = "[";
		String imgTime = "[";
		
		List<CDNDetail> details = cdnTongji.getDetails();
		for (CDNDetail cdnDetail : details) {
			imgHit += cdnDetail.getHitCount() + ",";
			imgMiss += cdnDetail.getMissCount() + ",";
			imgTime += "'"+cdnDetail.getTime() + "',";
		}
		imgHit = imgHit.substring(0,imgHit.length() - 1)+"]";
		imgMiss = imgMiss.substring(0,imgMiss.length() - 1)+"]";
		imgTime = imgTime.substring(0,imgTime.length() - 1)+"]";
		mav.addObject("imgHit",imgHit);
		mav.addObject("imgMiss",imgMiss);
		mav.addObject("imgTime",imgTime);
		
		
		String cdnTongjiJson = RedisPool.get("CDNTongji");
		cdnTongji = new Gson().fromJson(cdnTongjiJson, CDNTongji.class);
		String hit = "[";
		String miss = "[";
		String time = "[";
		
		details = cdnTongji.getDetails();
		for (CDNDetail cdnDetail : details) {
			hit += cdnDetail.getHitCount() + ",";
			miss += cdnDetail.getMissCount() + ",";
			time += "'"+cdnDetail.getTime() + "',";
		}
		hit = hit.substring(0,hit.length() - 1)+"]";
		miss = miss.substring(0,miss.length() - 1)+"]";
		time = time.substring(0,time.length() - 1)+"]";
		mav.addObject("hit",hit);
		mav.addObject("miss",miss);
		mav.addObject("time",time);
		mav.setViewName("/cdnTongji");
		return mav;
	}
	
	@RequestMapping(value="/baidu")
	public ModelAndView baiduTongji(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		String baiduTongjiJson = RedisPool.get("BaiduCountTongji");
		BaiduTongji baiduTongji = new Gson().fromJson(baiduTongjiJson, BaiduTongji.class);
		String baiduCount = "[";
		String time = "[";
		
		List<BaiduDetail> details = baiduTongji.getDetails();
		for (BaiduDetail baiduDetail : details) {
			baiduCount += baiduDetail.getCount() + ",";
			time += "'"+baiduDetail.getTime() + "',";
		}
		baiduCount = baiduCount.substring(0,baiduCount.length() - 1)+"]";
		time = time.substring(0,time.length() - 1)+"]";
		mav.addObject("baiduCount",baiduCount);
		mav.addObject("time",time);
		BaiduSpiderTongji tongji = new Gson().fromJson(RedisPool.get("baiduSpiderTongji"), BaiduSpiderTongji.class);
		mav.addObject("baiduSpider", tongji);
		List<BaiduSpiderIpDetail> ipDetails = tongji.getIpDetails();
		String singleData = "[";
		String data = "[";
		for (BaiduSpiderIpDetail detail : ipDetails) {
			singleData += "'"+detail.getIpSplit()+"',";
			data += "{value:"+detail.getCount()+",name:\""+detail.getIpSplit()+"\"},";
		}
		singleData = singleData.substring(0,singleData.length() - 1)+"]";
		data = data.substring(0,data.length() - 1)+"]";
		mav.addObject("singleData", singleData);
		mav.addObject("data", data);
		mav.setViewName("/baiduTongji");
		return mav;
	}
	
	@RequestMapping(value="/system")
	public ModelAndView systemTongji(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		String memTongjiJson = RedisPool.get("memTongji");
		MemTongji memTongji = new Gson().fromJson(memTongjiJson, MemTongji.class);
		String usedCount = "[";
		String buffCount = "[";
		String cachCount = "[";
		String freeCount = "[";
		String time = "[";
		List<MemDetail> details = memTongji.getDetails();
		int mSize = details.size();
		String lastTime = null;
		for (MemDetail memDetail : details) {
			usedCount += memDetail.getUsed() + ",";
			buffCount += memDetail.getBuff() + ",";
			cachCount += memDetail.getCach() + ",";
			freeCount += memDetail.getFree() + ",";
			lastTime = memDetail.getTime();
			time += "'" + lastTime + "',";
		}
		usedCount = usedCount.substring(0,usedCount.length() - 1)+"]";
		buffCount = buffCount.substring(0,buffCount.length() - 1)+"]";
		cachCount = cachCount.substring(0,cachCount.length() - 1)+"]";
		freeCount = freeCount.substring(0,freeCount.length() - 1)+"]";
		time = time.substring(0,time.length() - 1)+"]";
		mav.addObject("usedCount",usedCount);
		mav.addObject("buffCount",buffCount);
		mav.addObject("cachCount",cachCount);
		mav.addObject("freeCount",freeCount);
		mav.addObject("time",time);
		mav.addObject("lastTime",lastTime);
		
		DiskTongji diskTongji = new Gson().fromJson(RedisPool.get("diskTongji"), DiskTongji.class);
		mav.addObject("diskTongji", diskTongji);
		mav.addObject("memDetail", details.get(mSize - 1));
		mav.setViewName("/systemTongji");
		return mav;
	}
	
	@RequestMapping(value="/money")
	public ModelAndView moneyTongji(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/moneyTongji");
		String time = format.format(new Date());
		MoneyDetail detail = new MoneyDetail();
		detail.setTime(time);
		
		String status = "";
		String baseTongjiJson = RedisPool.get("baseMoneyTongji");
		if(null == baseTongjiJson){
			mav.addObject("noBase","1");
		}else{
			MoneyTongji baseMoneyTongji = new Gson().fromJson(baseTongjiJson, MoneyTongji.class);
			String baseMoney = "[";
			String baseTime = "[";
			
			List<MoneyDetail> baseDetails = baseMoneyTongji.getDetails();
			//判断今天是否提交
			if(baseDetails.contains(detail)){
				status += "今天总资产数据已提交<br/>";
			}else{
				status += "今天总资产数据<b>未提交</b><br/>";
			}
			for (MoneyDetail moneyDetail : baseDetails) {
				baseMoney += moneyDetail.getMoney() + ",";
				baseTime += "'"+moneyDetail.getTime() + "',";
			}
			baseMoney = baseMoney.substring(0,baseMoney.length() - 1)+"]";
			baseTime = baseTime.substring(0,baseTime.length() - 1)+"]";
			
			int size = baseDetails.size();
			mav.addObject("baseToday",baseDetails.get(size - 1));
			mav.addObject("baseYesterday",baseDetails.get(size - 2));
			
			mav.addObject("baseMoney",baseMoney);
			mav.addObject("baseTime",baseTime);
			mav.addObject("noBase","0");
		}
		
		
		String upTongjiJson = RedisPool.get("upMoneyTongji");
		if(null == upTongjiJson){
			mav.addObject("noUp","1");
		}else{
			MoneyTongji upMoneyTongji = new Gson().fromJson(upTongjiJson, MoneyTongji.class);
			String upMoney = "[";
			String upTime = "[";
			
			List<MoneyDetail> upDetails = upMoneyTongji.getDetails();
			//判断今天是否提交
			if(upDetails.contains(detail)){
				status += "今天利息值数据已提交";
			}else{
				status += "今天利息值数据<b>未提交</b>";
			}
			double totalUp = 0;
			for (MoneyDetail moneyDetail : upDetails) {
				upMoney += moneyDetail.getMoney() + ",";
				totalUp += moneyDetail.getMoney();
				upTime += "'"+moneyDetail.getTime() + "',";
			}
			upMoney = upMoney.substring(0,upMoney.length() - 1)+"]";
			upTime = upTime.substring(0,upTime.length() - 1)+"]";
			
			int size = upDetails.size();
			mav.addObject("upToday",upDetails.get(size - 1));
			mav.addObject("upYesterday",upDetails.get(size - 2));
			
			mav.addObject("firstTime", upDetails.get(0).getTime());
			mav.addObject("totalUp", NumberUtil.formatDouble(totalUp));
			
			mav.addObject("upMoney",upMoney);
			mav.addObject("upTime",upTime);
			mav.addObject("noUp","0");
		}
		if("".equals(status)){
			status = "没有数据";
		}
		if((status).contains("没有数据") || status.contains("未提交")){
			mav.addObject("color", "danger");
		}else{
			mav.addObject("color", "success");
		}
		mav.addObject("status",status);
		return mav;
	}
	
	@RequestMapping(value = "/uploadFileAjax")
	public void uploadFileAjax(@RequestParam MultipartFile moneyPic,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("in");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			if (moneyPic != null && !moneyPic.isEmpty()) {
				String originalFilename = moneyPic.getOriginalFilename();
				if (null != originalFilename && originalFilename.toLowerCase().endsWith(".png")) {
					String newFileName = "/root/data/aliyun/image/3.PNG";
					FileUtils.copyInputStreamToFile(moneyPic.getInputStream(),new File(newFileName));
					MoneyService.dealMoney(newFileName);
				} else {
					out.flush();
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/uploadFileNavAjax")
	public void uploadFileNavAjax(@RequestParam MultipartFile moneyNavPic,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("in");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			if (moneyNavPic != null && !moneyNavPic.isEmpty()) {
				String originalFilename = moneyNavPic.getOriginalFilename();
				if (null != originalFilename && originalFilename.toLowerCase().endsWith(".png")) {
					String newFileName = "/root/data/aliyun/image/3.PNG";
					FileUtils.copyInputStreamToFile(moneyNavPic.getInputStream(),new File(newFileName));
					MoneyService.dealMoney(newFileName);
				} else {
					out.flush();
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/log")
	public ModelAndView logTongji(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		String logTypeCountJson = RedisPool.get("logTypeCountTongji");
		LogTypeCountTongji logTypeCountTongji = new Gson().fromJson(logTypeCountJson, LogTypeCountTongji.class);
		String datas = "[";
		List<LogTypeCountDetail> details = logTypeCountTongji.getDetails();
		String legendData = "[";
		String values = "[";
		for (LogTypeCountDetail detail : details) {
			datas+="{value:"+detail.getCount()+",name:\""+detail.getType()+"\"},";
			values += detail.getCount() + ",";
			legendData+="'"+detail.getType()+"',";
		}
		datas = datas.substring(0,datas.length() - 1)+"]";
		values = values.substring(0,values.length() - 1)+"]";
		legendData = legendData.substring(0,legendData.length() - 1)+"]";
		mav.addObject("logTypeCountTongji", logTypeCountTongji);
		mav.addObject("datas", datas);
		mav.addObject("values", values);
		mav.addObject("legendData", legendData);
		mav.addObject("errors", LogService.getError());
		mav.addObject("warns", LogService.getWarn());
		mav.setViewName("/logTongji");
		return mav;
	}
	
	@RequestMapping(value = "/getInfoByType")
	public @ResponseBody LogTypeInfoTongji getInfoByType(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String type = request.getParameter("type");
		if(null == type || "".equals(type)){
			return null;
		}
		String json = RedisPool.get("logTypeInfo_"+type);
		if(null == json){
			return null;
		}
		return new Gson().fromJson(json, LogTypeInfoTongji.class);
	}
	
	@RequestMapping(value = "/getByKeyword")
	public @ResponseBody List<String> getByKeyword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String keyword = request.getParameter("keyword");
		String logFile = request.getParameter("logFile");
		String fileName = "/var/log/nginx/"+logFile+".log";
		return DoShell.shell("cat "+fileName+" | grep '"+keyword+"'");
	}
}
