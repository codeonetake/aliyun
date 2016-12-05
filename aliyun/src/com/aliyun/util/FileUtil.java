package com.aliyun.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class FileUtil {
	public static boolean saveFile(InputStream is, String fileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
		} catch (Exception e) {
			return false;
		}
		int ch = 0;
		try {
			while ((ch = is.read()) != -1) {
				fos.write(ch);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				fos.close();
				is.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return true;
	}
	
	private static String[] sizeUnit = {"B","KB","MB","GB"};
	
	public static String formatDouble(double d){
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(d);
	}
	
	public static String getSize(long size){
		double doubleSize = Double.parseDouble(size + "");
		int index = 0;
		while(doubleSize / 1024 > 1){
			index ++;
			doubleSize = doubleSize / 1024;
		}
		if(index < sizeUnit.length){
			return formatDouble(doubleSize) + " " +sizeUnit[index];
		}
		return "";
	}
}
