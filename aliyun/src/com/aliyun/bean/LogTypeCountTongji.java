package com.aliyun.bean;

import java.util.List;

public class LogTypeCountTongji {
	private String time;
	private List<LogTypeCountDetail> details;

	public List<LogTypeCountDetail> getDetails() {
		return details;
	}

	public void setDetails(List<LogTypeCountDetail> details) {
		this.details = details;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
