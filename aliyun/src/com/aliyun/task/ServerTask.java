package com.aliyun.task;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aliyun.service.CDNCacheService;
import com.aliyun.service.EmailService;
import com.aliyun.service.FestivalService;
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
        //发送报告
        try {
        		EmailService.sendEmail();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
	
	@Scheduled(cron = "0 0 0 * * *") 
	public void changeFestival(){
		System.out.println("修改节假日开始");
		FestivalService.autoRunFestival();
		System.out.println("修改节假日结束");
		System.out.println("删除节假日多余图片开始");
		FestivalService.deleteErrorImg();
		System.out.println("删除节假日多余图片结束");
	}
	
	@Scheduled(cron = "0 0/9 * * * ?")   
    public void refreshCdn(){
		CDNCacheService.refresh();
		try {
			CDNCacheService.refreshAllImg();
		} catch (Exception e) {
			// TODO: handle exception
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
		//
		/*RedisPool.set("defaultBackColor", "55b3db");
		RedisPool.set("defaultPicId", "16");
		
		RedisPool.set("currentBackColor", "55b3db");
		RedisPool.set("currentPicId", "16");*/
	}
}
