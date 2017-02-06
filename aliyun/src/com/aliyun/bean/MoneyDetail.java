package com.aliyun.bean;

import java.util.ArrayList;
import java.util.List;

public class MoneyDetail {
	private String time;
	private double money;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	@Override
	public boolean equals(Object obj) {
		return this.time.equals(((MoneyDetail)obj).time);
	}
	@Override
	public int hashCode() {
		return this.hashCode();
	}
}
