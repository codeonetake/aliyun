package com.aliyun.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.aliyun.util.DoShell;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;
import com.aliyun.util.OssOperate;

public class UploadBakService {
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
	private static String msg = "";
	private static int totalSuccessCount = 0;
	private static int totalFailCount = 0;
	public static void uploadBak(){
		msg = "";
		totalSuccessCount = 0;
		totalFailCount = 0;
		m("总备份开始");
		m("总备份配置如下：");
		String bakSerFile = OssConfig.getValue("bakSerFile");
		m("bakSerFile: " + bakSerFile);
		String bucketName = OssConfig.getValue("bakBucketName");
		m("bucketName: " + bucketName);
		String bakFileHead = fileNameFormat.format(new Date());
		m("bakFileHead: " + bakFileHead);
		m("==========================");
		bakTomcat(bucketName,bakFileHead);
		m("");
		m("");
		m("==========================");
		bakNginxServer(bucketName,bakFileHead);
		m("");
		m("");
		m("==========================");
		bakNginxProj(bucketName,bakFileHead);
		m("");
		m("");
		m("==========================");
		bakMysql(bucketName,bakFileHead);
		m("==========================");
		delOverTimeFile(bucketName);
		m("==========================");
		m("总备份结束，总成功条数为：("+totalSuccessCount+")，总失败条数：("+totalFailCount+")");
		//保存结果
		File bakSer = new File(bakSerFile);
		if(!bakSer.getParentFile().exists()){
			bakSer.getParentFile().mkdirs();
		}
		ObjSave.objectToFile(msg, bakSerFile);
		System.out.println(msg);
	}
	private static void m(String content){
		if(content.contains("(")){
			content = content.replaceAll("\\(", "<b>");
		}
		if(content.contains(")")){
			content = content.replaceAll("\\)", "</b>");
		}
		if(content.contains("[")){
			content = content.replaceAll("\\[", "<code>");
		}
		if(content.contains("]")){
			content = content.replaceAll("\\]", "</code>");
		}
		if(content.contains("成功")){
			content = content.replaceAll("成功", "<font color='green'><b>成功</b></font>");
			totalSuccessCount ++;
		}
		if(content.contains("失败")){
			content = content.replaceAll("失败", "<font color='red'><b>失败</b></font>");
			totalFailCount ++;
		}
		if(content.endsWith("开始") || content.endsWith("结束")){
			String time = dateTimeFormat.format(new Date());
			msg += content + "，时间："+time+"<br/>";
		}else{
			msg += content + "<br/>";
		}
	}
	private static void delOverTimeFile(String bucketName){
		m("查询并删除过期文件开始");
		try {
			String wpBakMaxCount = OssConfig.getValue("wpBakMaxCount");
			if(null == wpBakMaxCount || "".equals(wpBakMaxCount)){
				m("未设置最大备份文件保留数，不删除文件");
			}else{
				int maxCount = 0;
				try {
					maxCount = Integer.parseInt(wpBakMaxCount);
				} catch (Exception e) {
					m("最大备份文件保留数不是整数，设置失败");
				}
				if(0 != maxCount){
					m("最大备份文件保留数为("+maxCount+")");
				}
				Set<String> rootFiles = OssOperate.getAllRootFiles(bucketName);
				int currentSize = rootFiles.size();
				m("当前已经备份("+currentSize+")个文件");
				if(currentSize > maxCount){
					int needDelCount = currentSize - maxCount;
					m("需要删除("+needDelCount+")个文件");
					List<String> rootFileList = new ArrayList<String>();
					rootFileList.addAll(rootFiles);
					List<String> needToDelFiles = rootFileList.subList(0, needDelCount);
					m("删除过期备份文件开始");
					for (String needToDelFile : needToDelFiles) {
						m("删除文件: ("+needToDelFile+")");
						OssOperate.deleteDir(bucketName, needToDelFile);
					}
					m("删除过期备份文件结束");
				}else{
					m("没有过期文件，不进行删除");
				}
			}
			m("查询并删除过期文件成功结束");
		} catch (Exception e) {
			e.printStackTrace();
			m("查询并删除过期文件失败结束");
		}
	}
	
	private static void bakTomcat(String bucketName,String bakFileHead){
		m("(备份tomcat开始)");
		m("备份配置如下：");
		String tomcatsBakPath = OssConfig.getValue("tomcatsBakPath");
		String manageTomcatPath = OssConfig.getValue("manageTomcatPath");
		String removeBakTomcat = OssConfig.getValue("removeBakTomcat");
		m("tomcatsBakPath："+tomcatsBakPath);
		m("manageTomcatPath："+manageTomcatPath);
		m("removeBakTomcat："+removeBakTomcat);
		String shell = null;
		if(!"".equals(tomcatsBakPath)){
			//先备份tomcat的logs（会备份所有的文件）
			File[] tomcats = new File(tomcatsBakPath).listFiles();
			File logFile = null;
			String tarFileName = null;
			String tarFilePath = null;
			String bakFilePath = null;
			int logCount = 0;
			m("备份log开始");
			m("");
			for (File tomcat : tomcats) {
				if(tomcat.isDirectory()){
					logFile = new File(tomcat.getAbsolutePath()+"/logs");
					if(logFile.exists() && logFile.isDirectory()){
						logCount ++;
						try {
							m("备份(" + tomcat.getName() + ")的日志开始");
							tarFileName = tomcat.getName()+".tar";
							tarFilePath = tomcat.getAbsolutePath() + "/" + tarFileName;
							//打包
							shell = "tar -zcPf " + tarFilePath + " " + logFile.getAbsolutePath();
							m("["+shell+"]");
							DoShell.shell(shell);
							//上传
							bakFilePath = bakFileHead + "/logs/tomcat/" + tarFileName;
							m("上传文件开始");
							OssOperate.uploadFile(new File(tarFilePath), bucketName, bakFilePath, true);
							m("上传文件结束");
							//清空日志目录
							shell = "rm -rf " + logFile.getAbsolutePath()+"/*";
							m("["+shell+"]");
							DoShell.shell(shell);
							//清除打包文件 
							shell = "rm -rf " + tarFilePath;
							m("["+shell+"]");
							DoShell.shell(shell);
							m("备份(" + tomcat.getName() + ")的日志成功结束");
							m("");
						} catch (Exception e) {
							m("备份(" + tomcat.getName() + ")的日志失败结束");
							m("");
							e.printStackTrace();
						}
					}
				}
			}
			m("备份log结束");
			m("一共备份了("+logCount+")个tomcat的log");
			m("");
			m("");
			m("备份tomcat文件开始");
			//备份tomcat
			String tomcatTarName = null;
			String tomcatTarPath = null;
			int tomcatCount = 0;
			for (File tomcat : tomcats) {
				if(tomcat.isDirectory() && !removeBakTomcat.contains("|"+tomcat.getName()+"|")){
					try {
						tomcatCount ++;
						m("备份(" + tomcat.getName() + ")的文件开始");
						tomcatTarName = tomcat.getName() + ".tar";
						tomcatTarPath = tomcatsBakPath + tomcatTarName;
						//打包
						shell = "tar -zcPf " + tomcatTarPath + " " + tomcat.getAbsolutePath();
						m("["+shell+"]");
						DoShell.shell(shell);
						//上传
						m("上传文件开始");
						bakFilePath = bakFileHead + "/tomcats/" + tomcatTarName;
						m("上传文件结束");
						OssOperate.uploadFile(new File(tomcatTarPath), bucketName, bakFilePath, true);
						//删除tar
						shell = "rm -rf " + tomcatTarPath;
						m("["+shell+"]");
						DoShell.shell(shell);
						m("备份(" + tomcat.getName() + ")的文件成功结束");
						m("");
					} catch (Exception e) {
						m("备份(" + tomcat.getName() + ")的文件失败结束");
						m("");
						e.printStackTrace();
					}
				}
			}
			m("备份tomcat文件结束");
			m("一共备份了("+tomcatCount+")个tomcat文件");
			m("");
			m("");
		}else{
			m("没有设置tomcat路径，不进行备份");
		}
		if(!"".equals(manageTomcatPath)){
			try {
				m("备份tomcat管理项目开始");
				//先备份日志
				String logPath = manageTomcatPath + "logs/";
				String tomcatName = manageTomcatPath.endsWith("/")?manageTomcatPath.substring(0,manageTomcatPath.length() - 1):manageTomcatPath;
				tomcatName = tomcatName.substring(tomcatName.lastIndexOf("/") + 1);
				String logTarName = tomcatName + ".tar";
				String logTarPath = manageTomcatPath + logTarName;
				String bakFilePath = bakFileHead + "/logs/tomcat/" + logTarName;
				shell = "tar -zcPf " + logTarPath + " " + logPath;
				m("["+shell+"]");
				//开始打包
				DoShell.shell(shell);
				//开始上传
				m("上传日志开始");
				OssOperate.uploadFile(new File(logTarPath), bucketName, bakFilePath, true);
				m("上传日志结束");
				//清理日志
				shell = "rm -rf " + logPath + "*";
				m("["+shell+"]");
				DoShell.shell(shell);
				//清空tar
				shell = "rm -rf " + logTarPath;
				m("["+shell+"]");
				DoShell.shell(shell);
				//开始上传tomcat
				//开始打包
				String tarPath = manageTomcatPath.substring(0,manageTomcatPath.indexOf(tomcatName)) + logTarName;
				shell = "tar -zcPf " + tarPath + " " + manageTomcatPath;
				m("["+shell+"]");
				DoShell.shell(shell);
				//开始上传
				bakFilePath = bakFileHead + "/tomcats/" + logTarName;
				m("上传tomcat文件开始");
				OssOperate.uploadFile(new File(tarPath), bucketName, bakFilePath, true);
				m("上传tomcat文件结束");
				//删除tar
				shell = "rm -rf " + tarPath;
				m("["+shell+"]");
				DoShell.shell(shell);
				m("上传tomcat管理项目成功结束");
			} catch (Exception e) {
				m("上传tomcat管理项目失败结束");
				e.printStackTrace();
			}
		}else{
			m("没有设置tomcat管理项目的路径，不进行备份");
		}
		m("(备份tomcat结束)");
	}
	
	private static void bakNginxServer(String bucketName,String bakFileHead){
		String shell = null;
		m("(备份nginx服务器开始)");
		m("备份配置如下：");
		String nginxLog = OssConfig.getValue("nginxLog");
		m("nginxLog："+nginxLog);
		String nginxConf = OssConfig.getValue("nginxConf");
		m("nginxConf："+nginxConf);
		m("");
		m("备份nginx服务器日志开始");
		if(!"".equals(nginxLog)){
			//开始备份
			try {
				String tarFileName = "nginxLog.tar";
				String temp = nginxLog.endsWith("/")?nginxLog.substring(0,nginxLog.length() - 1):nginxLog;
				temp = temp.substring(0, temp.lastIndexOf("/"));
				String tarFilePath = temp + "/" + tarFileName;
				System.out.println(tarFilePath);
				String bakFilePath = bakFileHead + "/logs/nginx/"+tarFileName;
				shell = "tar -zcPf " + tarFilePath + " " + nginxLog;
				m("["+shell+"]");
				DoShell.shell(shell);
				m("上传Nginx日志开始");
				OssOperate.uploadFile(new File(tarFilePath), bucketName, bakFilePath, true);
				m("上传Nginx日志结束");
				//清空日志
				shell = "rm -rf " + nginxLog + "*";
				m("["+shell+"]");
				DoShell.shell(shell);
				//清除tar文件
				shell = "rm -rf " + tarFilePath;
				m("["+shell+"]");
				DoShell.shell(shell);
				m("备份nginx服务器日志成功结束");
			} catch (Exception e) {
				m("备份nginx服务器日志失败结束");
				e.printStackTrace();
			}
		}else{
			m("没有设置nginx服务器日志路径，不备份");
		}
		m("");
		m("备份nginx服务器配置开始");
		if(!"".equals(nginxConf)){
			try {
				String tarFileName = "conf.tar";
				String temp = nginxConf.endsWith("/")?nginxConf.substring(0,nginxConf.length() - 1):nginxConf;
				temp = temp.substring(0, temp.lastIndexOf("/"));
				String tarFilePath = temp + "/" + tarFileName;
				System.out.println(tarFilePath);
				String bakFilePath = bakFileHead + "/nginx/conf/" + tarFileName;
				shell = "tar -zcPf " + tarFilePath + " " + nginxConf;
				m("["+shell+"]"); 
				DoShell.shell(shell);
				m("上传Nginx配置开始");
				OssOperate.uploadFile(new File(tarFilePath), bucketName, bakFilePath, true);
				m("上传Nginx配置结束");
				//清除tar文件
				shell = "rm -rf " + tarFilePath;
				m("["+shell+"]");
				DoShell.shell(shell);
				m("备份nginx服务器配置成功结束");
			} catch (Exception e) {
				m("备份nginx服务器配置失败结束");
				e.printStackTrace();
			}
		}else{
			m("没有设置nginx服务器配置路径，不备份");
		}
		m("(备份nginx服务器结束)");
	}
	
	private static void bakNginxProj(String bucketName,String bakFileHead){
		String shell = null;
		m("(备份nginx服务器项目开始)");
		String nginxProjs = OssConfig.getValue("nginxProjs");
		String[] nginxs = nginxProjs.split(";");
		String[] proj = null;
		for (String nginx : nginxs) {
			try {
				proj = nginx.split(":");
				m("备份项目("+proj[0]+")开始");
				String tarFileName = proj[0] + ".tar";
				String projectPath = proj[1];
				String temp = projectPath.endsWith("/")?projectPath.substring(0,projectPath.length() - 1):projectPath;
				String tarFilePath = temp.substring(0,temp.lastIndexOf("/")) + "/" + tarFileName;
				System.out.println(tarFilePath);
				shell = "tar -zcPf " + tarFilePath + " " + projectPath;
				m("["+shell+"]");
				DoShell.shell(shell);
				String bakFileName = bakFileHead + "/nginx/projects/"+tarFileName;
				m("上传Nginx项目开始");
				OssOperate.uploadFile(new File(tarFilePath)	, bucketName, bakFileName, true);
				m("上传Nginx项目结束");
				//清理文件
				shell = "rm -rf " + tarFilePath;
				m("["+shell+"]");
				DoShell.shell(shell);
				m("备份项目("+proj[0]+")成功结束");
			} catch (Exception e) {
				e.printStackTrace();
				m("备份项目("+proj[0]+")失败结束");
			}
			m("");
		}
		m("(备份nginx服务器项目结束)");
	}
	
	private static void bakMysql(String bucketName,String bakFileHead){
		String shell = null;
		String filePath = "/root/data/mysql/";
		File dir = new File(filePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		m("(备份MySql数据库开始)");
		m("备份配置如下：");
		String bakMysqlDB = OssConfig.getValue("bakMysqlDB");
		m("bakMysqlDB：" + bakMysqlDB);
		String dbname = OssConfig.getValue("dbname");
		String dbpwd = OssConfig.getValue("dbpwd");
		if(!"".equals(bakMysqlDB)){
			String[] dbs = bakMysqlDB.split(";");
			for (String db : dbs) {
				try {
					shell = "mysqldump -u "+dbname+" -p"+dbpwd+" "+db+" > "+filePath+db+".sql";
					System.out.println(shell);
					m("["+shell+"]");
					DoShell.shell(shell);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				String temp = filePath.endsWith("/")?filePath.substring(0, filePath.length() - 1):filePath;
				String tarFileName = "mysql.tar";
				String tarFilePath = temp.substring(0,temp.lastIndexOf("/")) + "/" + tarFileName;
				shell = "tar -zcPf " + tarFilePath + " " + filePath;
				m("["+shell+"]");
				DoShell.shell(shell);
				m("上传MySql数据库文件开始");
				String bakFileName = bakFileHead + "/mysql/" + tarFileName;
				OssOperate.uploadFile(new File(tarFilePath), bucketName, bakFileName, true);
				m("上传MySql数据库文件结束");
				shell = "rm -rf " + tarFilePath;
				m("["+shell+"]");
				DoShell.shell(shell);
				shell = "rm -rf " + filePath; 
				m("["+shell+"]");
				DoShell.shell(shell);
				m("备份MySql数据库开始成功结束");
			} catch (Exception e) {
				m("备份MySql数据库开始失败结束");
				e.printStackTrace();
			}
		}else{
			m("数据库配置为空，不备份");
		}
	}
}