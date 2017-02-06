package com.aliyun.service;

import java.text.DecimalFormat;

public class NumberUtil {
	public static String formatDouble(double d){
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(d);
	}
}
