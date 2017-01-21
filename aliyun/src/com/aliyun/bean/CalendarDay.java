package com.aliyun.bean;

public class CalendarDay {
	private String reason;
	private int error_code;
	private CalendarDayResult result;
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public CalendarDayResult getResult() {
		return result;
	}
	public void setResult(CalendarDayResult result) {
		this.result = result;
	}
}
