package com.aliyun.bean;

import java.util.List;

import com.aliyun.oss.model.OSSObjectSummary;

public class OssPage {
	private String nextMarker;
	private List<OSSObjectSummary> summrayList;
	
	public String getNextMarker() {
		return nextMarker;
	}
	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}
	public List<OSSObjectSummary> getSummrayList() {
		return summrayList;
	}
	public void setSummrayList(List<OSSObjectSummary> summrayList) {
		this.summrayList = summrayList;
	}
}
