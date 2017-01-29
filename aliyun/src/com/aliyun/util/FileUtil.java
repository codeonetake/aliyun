package com.aliyun.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * 文件操作类
 * @author liuwenbin
 *
 */
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
	/**
	 * 数据单位数组
	 */
	private static String[] sizeUnit = {"B","KB","MB","GB"};
	
	/**
	 * 保留两位小数
	 * @param d 数字
	 * @return 保留两位小数后的数字
	 */
	public static String formatDouble(double d){
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(d);
	}
	/**
	 * 获取大小
	 * @param size 文件大小数值
	 * @return 格式化后的文件大小，包含单位
	 */
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
	
	public static void write(String filePath,String content) throws Exception{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filePath),true));
		bufferedWriter.write(content+"\r\n");
		bufferedWriter.close();
	}
}
