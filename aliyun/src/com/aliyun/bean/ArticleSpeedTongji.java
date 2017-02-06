package com.aliyun.bean;

import java.util.List;

public class ArticleSpeedTongji {
	private String time;
	private List<ArticleSpeed> articleSpeeds;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<ArticleSpeed> getArticleSpeeds() {
		return articleSpeeds;
	}
	public void setArticleSpeeds(List<ArticleSpeed> articleSpeeds) {
		this.articleSpeeds = articleSpeeds;
	}
	
}
