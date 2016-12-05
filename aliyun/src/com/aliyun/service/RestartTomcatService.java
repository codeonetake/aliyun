package com.aliyun.service;

import com.aliyun.util.DoShell;

public class RestartTomcatService {
	
	public static void start(){
		String path = RestartTomcatService.class.getClassLoader().getResource("").toString().replaceAll("classes/", "") + "lib/aliyun-restart-tomcat.jar";
		if(path.startsWith("file:")){
			path = path.replaceAll("file:", "");
		}
		try {
			String shell = "java -jar " + path;
			DoShell.shell(shell);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
