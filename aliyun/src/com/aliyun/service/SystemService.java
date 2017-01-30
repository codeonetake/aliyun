package com.aliyun.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aliyun.bean.DiskTongji;
import com.aliyun.bean.MemDetail;
import com.aliyun.bean.MemTongji;
import com.aliyun.util.DoShell;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

public class SystemService {
	
	private static int maxCount = 288;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static void get() {
		String shell = "dstat --freespace --top-cpu --top-mem --proc-count -mrc 1 0";
		List<String> list = null;
		try {
			list = DoShell.shell(shell);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(null != list){
			String line = list.get(list.size() - 1);
			try {
				getInfo(line);
			} catch (Exception e) {
			}
		}
	}
	
	public static void main(String[] args) {
		get();
	}
	
	private static void getInfo(String line){
		String time = format.format(new Date());
		String memRedisKey = "memTongji";
		String diskRedisKey = "diskTongji";
		line = line.trim();
		String[] infos = line.split("\\|");
		//内存使用情况
		String info = infos[4];
		String[] use = info.split("\\s+");
		MemTongji memTongji = null;
		if(RedisPool.isExist(memRedisKey)){
			memTongji = new Gson().fromJson(RedisPool.get(memRedisKey),MemTongji.class);
		}else{
			memTongji = new MemTongji();
		}
		List<MemDetail> details = null;
		if(null == memTongji.getDetails()){
			details = new ArrayList<MemDetail>();
		}else{
			details = memTongji.getDetails();
		}
		int size = details.size();
		if(size>maxCount){
			details = details.subList(size - maxCount,size);
		}
		MemDetail memDetail = new MemDetail();
		memDetail.setTime(time);
		memDetail.setBuff(getMemory(use[1]));
		memDetail.setCach(getMemory(use[2]));
		memDetail.setFree(getMemory(use[3]));
		memDetail.setUsed(getMemory(use[0]));
		details.add(memDetail);
		memTongji.setDetails(details);
		RedisPool.set(memRedisKey, new Gson().toJson(memTongji));
		
		//解析服务器磁盘使用量
		info = infos[0];
		String is = info.split(":")[0].trim();
		use = is.split("\\s+");
		DiskTongji diskTongji = new DiskTongji();
		diskTongji.setFree(getDisk(use[1]));
		diskTongji.setUse(getDisk(use[0]));
		diskTongji.setTime(time);
		RedisPool.set(diskRedisKey, new Gson().toJson(diskTongji));
	}
	
	public static String formatDouble(double d){
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(d);
	}
	
	public static String getMemory(String memory){
		memory = memory.trim().toLowerCase();
		String mSize = memory.substring(0,memory.length() - 1);
		if(memory.endsWith("k")){
			mSize = formatDouble(Double.parseDouble(mSize) / 1024);
		}
		return mSize;
	}
	
	public static String getDisk(String disk){
		disk = disk.trim().toLowerCase();
		String dSize = disk.substring(0,disk.length() - 1);
		if(disk.endsWith("g")){
			dSize = formatDouble(Double.parseDouble(dSize) * 1024);
		}
		return dSize;
	}
}
