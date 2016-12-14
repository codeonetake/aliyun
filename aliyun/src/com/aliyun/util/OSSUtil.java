package com.aliyun.util;

import java.io.File;
import java.util.List;

import com.aliyun.bean.OssFile;
import com.aliyun.oss.model.OSSObjectSummary;

public class OSSUtil {
	private static String savePath = null;
	static{
		savePath = OssConfig.getValue("downLoadTempPath");
		File file = new File(savePath);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	public static String moveTolocal(String bucketName,String file,int type){
		System.out.println("bucketName:"+bucketName);
		System.out.println("file:"+file);
		System.out.println("type:"+type);
		System.out.println("savePath:"+savePath);
		File f = new File(savePath);
		if(!f.exists()){
			f.mkdirs();
		}
		String destFilePath = null;
		if(0 == type){
			destFilePath = savePath+new File(file).getName();
			OssOperate.downloadFile(bucketName, file, destFilePath);
		}else{
			List<OSSObjectSummary> list = OssOperate.getAllFile(bucketName, file);
			String fileName = null;
			String totalName = null;
			File fi = null;
			for (OSSObjectSummary ossObjectSummary : list) {
				fileName = ossObjectSummary.getKey();
				totalName = savePath + fileName;
				System.out.println(totalName);
				fi = new File(totalName);
				if(!fi.getParentFile().exists()){
					fi.getParentFile().mkdirs();
				}
				OssOperate.downloadFile(bucketName, fileName, totalName);
			}
			//下载完成后压缩
			destFilePath = savePath + file+".tar";
			String dirFile = savePath + file;
			String shell = "tar -zcPf " + destFilePath + " " + dirFile;
			try {
				DoShell.shell(shell);
			} catch (Exception e) {
				return null;
			}
			//删除文件夹
			shell = "rm -rf " + dirFile;
			try {
				DoShell.shell(shell);
			} catch (Exception e) {
				return null;
			}
		}
		return destFilePath;
	}
}
