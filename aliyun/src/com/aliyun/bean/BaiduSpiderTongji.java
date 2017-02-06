package com.aliyun.bean;

import java.util.List;

public class BaiduSpiderTongji {
	private String time;
	private List<String> originInfo;
	private List<BaiduSpiderIpDetail> ipDetails;
	private List<BaiduSpiderInfoDetail> infoDetails;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<String> getOriginInfo() {
		return originInfo;
	}
	public void setOriginInfo(List<String> originInfo) {
		this.originInfo = originInfo;
	}
	public List<BaiduSpiderIpDetail> getIpDetails() {
		return ipDetails;
	}
	public void setIpDetails(List<BaiduSpiderIpDetail> ipDetails) {
		this.ipDetails = ipDetails;
	}
	public List<BaiduSpiderInfoDetail> getInfoDetails() {
		return infoDetails;
	}
	public void setInfoDetails(List<BaiduSpiderInfoDetail> infoDetails) {
		this.infoDetails = infoDetails;
	}
}
