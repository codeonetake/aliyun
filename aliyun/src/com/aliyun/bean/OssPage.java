package com.aliyun.bean;

import java.util.List;

import com.aliyun.oss.model.OSSObjectSummary;

/**
 * oss分页查询类
 * @author liuwenbin
 *
 */
public class OssPage {
	//下一页开始的文件名，查询下一页文件时使用
	private String nextMarker;
	//当前页的文件列表
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
