package com.aliyun.task;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aliyun.service.RestartTomcatService;
import com.aliyun.service.UploadBakService;

@Component
public class ServerTask {

	@Scheduled(cron = "0 10 0 * * *")   
    public void show(){
		System.out.println("定时备份开启");
		UploadBakService.uploadBak();
        System.out.println("定时备份结束");
        System.out.println("定时重启开启");
		RestartTomcatService.start();
        System.out.println("定时重启结束");
    }
	
	@PostConstruct
	public void init(){
		System.out.println("start tomcat aliyun");
	}
}
