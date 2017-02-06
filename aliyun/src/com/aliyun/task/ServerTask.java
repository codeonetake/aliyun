package com.aliyun.task;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aliyun.service.ArticleService;
import com.aliyun.service.BaiduCountService;
import com.aliyun.service.BaiduSpiderService;
import com.aliyun.service.CDNCacheService;
import com.aliyun.service.EmailService;
import com.aliyun.service.FestivalService;
import com.aliyun.service.LogService;
import com.aliyun.service.MipService;
import com.aliyun.service.MoneyService;
import com.aliyun.service.RestartTomcatService;
import com.aliyun.service.ShellService;
import com.aliyun.service.SystemService;
import com.aliyun.service.UploadBakService;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;

@Component
public class ServerTask {

	private String ruleFile = OssConfig.getValue("ruleSerFile");
		
	@Scheduled(cron = "0 10 0 * * *")   
    public void show(){
		System.out.println("提交MIP开始");
		try {
			MipService.submitMipUrl();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("提交MIP结束");
		System.out.println("获取百度收录开始");
		try {
			BaiduCountService.get();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("获取百度收录结束");
		String rules = (String)ObjSave.fileToObject(ruleFile);
		if(rules.startsWith("1")){
			System.out.println("定时备份开启");
			UploadBakService.uploadBak();
	        System.out.println("定时备份结束");
		}
		try {
			//先暂停一分钟再执行重启tomcat，以防止重启之后时间为00:10，还会执行一次备份。
			Thread.sleep(60000);
		} catch (Exception e) {
			// TODO: handle exception
		}
        if(rules.endsWith("1")){
        	 	System.out.println("定时重启开启");
     		RestartTomcatService.start();
            System.out.println("定时重启结束");
        }
    }
	
	@Scheduled(cron = "0 20 0 * * *")   
    public void sendEmail(){
		ShellService.cleanMemery();
        try {
        		EmailService.sendEmail();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
	
	@Scheduled(cron = "0 0 22 * * *")   
    public void checkMoney(){
		System.out.println("check money start");
        try {
        		MoneyService.checkTodayStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("check money end");
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
	
	@Scheduled(cron = "0 0/5 * * * ?")   
    public void getLogTongji(){
		try {
			LogService.get();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
	
	@Scheduled(cron = "0 0/3 * * * ?")   
    public void getSystemTongji(){
		try {
			SystemService.get();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
	
	@Scheduled(cron = "0 0/30 * * * ?")   
    public void getBaiduSpider(){
		try {
			BaiduSpiderService.getSpiderInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*try {
			ArticleService.articleSpeed();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
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