package com.aliyun.task;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aliyun.service.RestartTomcatService;
import com.aliyun.service.UploadBakService;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;

@Component
public class ServerTask {

	private String ruleFile = OssConfig.getValue("ruleSerFile");
		
	@Scheduled(cron = "0 10 0 * * *")   
    public void show(){
		String rules = (String)ObjSave.fileToObject(ruleFile);
		if(rules.startsWith("1")){
			System.out.println("定时备份开启");
			UploadBakService.uploadBak();
	        System.out.println("定时备份结束");
		}
        if(rules.endsWith("1")){
        	 	System.out.println("定时重启开启");
     		RestartTomcatService.start();
            System.out.println("定时重启结束");
        }
    }
	
	@PostConstruct
	public void init(){
		System.out.println("start tomcat aliyun");
		//判断规则信息是否存在，如果没有就保存
		System.out.println("ruleFile:"+ruleFile);
		File file = new File(ruleFile);
		if(!file.exists()){
			ObjSave.objectToFile("1|1", ruleFile);
		}
		System.out.println("add rule finish");
	}
}
