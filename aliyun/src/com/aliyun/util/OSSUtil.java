package com.aliyun.util;

import java.io.File;

public class OSSUtil {
	private static String savePath = null;
	static{
		savePath = OssConfig.getValue("downLoadTempPath");
		File file = new File(savePath);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	public static String moveTolocal(String bucketName,String file){
		File f = new File(savePath);
		if(!f.exists()){
			f.mkdirs();
		}
		System.out.println("savePath:"+savePath);
		System.out.println("new File(file):"+new File(file));
		String destFilePath = savePath+new File(file).getName();
		System.out.println("destFilePath:"+destFilePath);
		OssOperate.downloadFile(bucketName, file, destFilePath);
		return destFilePath;
	}
}
