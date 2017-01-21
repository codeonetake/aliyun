package com.aliyun.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aliyun.bean.CalendarDay;
import com.google.gson.Gson;

public class CalendarUtil {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
	public static CalendarDay getCalendar(String date) {
		String url = "http://v.juhe.cn/calendar/day?key=cd2606361b6637b355cc797820c37285&date="+date;
		String json = WebUtils.doReq(url);
		return new Gson().fromJson(json, CalendarDay.class);
	}
	
	public static CalendarDay getCalendar(Date date) {
		return getCalendar(format.format(date));
	}
	
	public static void main(String[] args) {
		System.out.println(new Gson().toJson(getCalendar(format.format(new Date()))));
	}
}
