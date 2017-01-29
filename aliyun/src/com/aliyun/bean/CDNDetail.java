package com.aliyun.bean;

public class CDNDetail {
	private String time;
	private int MissCount;
	private int HitCount;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getMissCount() {
		return MissCount;
	}
	public void setMissCount(int missCount) {
		MissCount = missCount;
	}
	public int getHitCount() {
		return HitCount;
	}
	public void setHitCount(int hitCount) {
		HitCount = hitCount;
	}
}
