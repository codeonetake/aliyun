package com.aliyun.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.bean.BaiduDetail;
import com.aliyun.bean.BaiduTongji;
import com.aliyun.bean.CDNDetail;
import com.aliyun.bean.CDNTongji;
import com.aliyun.bean.DiskTongji;
import com.aliyun.bean.MemDetail;
import com.aliyun.bean.MemTongji;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

@Controller
@RequestMapping(value="/tongji")
public class TongjiController {

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
		for (MemDetail memDetail : details) {
			usedCount += memDetail.getUsed() + ",";
			buffCount += memDetail.getBuff() + ",";
			cachCount += memDetail.getCach() + ",";
			freeCount += memDetail.getFree() + ",";
			time += "'"+memDetail.getTime() + "',";
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
		
		DiskTongji diskTongji = new Gson().fromJson(RedisPool.get("diskTongji"), DiskTongji.class);
		mav.addObject("diskTongji", diskTongji);
		mav.addObject("memDetail", details.get(mSize - 1));
		mav.setViewName("/systemTongji");
		return mav;
	}
}
