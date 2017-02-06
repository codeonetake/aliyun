package com.aliyun.service;

import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;
import com.common.util.EmailUtil;


public class EmailService {
	public static void sendEmail() throws Exception {
		String bakInfo = ObjSave.fileToObject(OssConfig.getValue("bakSerFile")).toString();
		String restartInfo = ObjSave.fileToObject("/root/data/aliyun/restart.ser").toString();
		String cdn = ObjSave.fileToObject("/root/data/aliyun/cdn.ser").toString();
		String imgCdn = ObjSave.fileToObject("/root/data/aliyun/imgCdn.ser").toString();
		String festival = ObjSave.fileToObject("/root/data/aliyun/festival.ser").toString();
		String baiduCount = ObjSave.fileToObject("/root/data/aliyun/baiduCount.ser").toString();
		EmailUtil.sendEmail("[CODEAWL]服务器备份重启等信息", bakInfo
				+"<br/>----------------<br/>"+restartInfo+"<br/>----------------<br/>"
				+cdn+"<br/>----------------<br/>"+imgCdn+"<br/>----------------<br/>"
				+festival+"<br/>----------------<br/>"+baiduCount, "codeawl@163.com");
	}
}