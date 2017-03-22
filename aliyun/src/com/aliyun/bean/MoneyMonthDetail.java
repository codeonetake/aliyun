package com.aliyun.bean;

public class MoneyMonthDetail implements Comparable<MoneyMonthDetail>{
	private String month;
	private String money;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public int compareTo(MoneyMonthDetail o) {
		return this.month.compareTo(o.month);
	}
}
