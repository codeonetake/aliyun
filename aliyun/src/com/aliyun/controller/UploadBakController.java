package com.aliyun.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.jar.service.JarRestartService;
import com.aliyun.service.RestartTomcatService;
import com.aliyun.service.UploadBakService;
import com.aliyun.util.OSSUtil;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;
import com.aliyun.util.OssOperate;

@Controller
@RequestMapping(value="")
public class UploadBakController {

	@RequestMapping(value="uploadBak")
	public void uploadWpBak(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			System.out.println("开始进行备份");
			UploadBakService.uploadBak();
			out.write("success");
		} catch (Exception e) {
			out.write("fail");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="restartTomcat")
	public void restartTomcat(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		new Thread(new Runnable() {
			
			public void run() {
				//先清空
				JarRestartService.emptyRestartSerFile();
				System.out.println("开始进行重启");
				RestartTomcatService.start();
			}
		}).start();
	}
	
	@RequestMapping(value="getRestartInfo")
	public void getRestartInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(JarRestartService.getRestartInfo());
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="bakpage")
	public ModelAndView bakpage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("bakpage");
		//获取规则
		String ruleFile = OssConfig.getValue("ruleSerFile");
		String rule = (String) ObjSave.fileToObject(ruleFile);
		modelAndView.addObject("rule", rule);
		return modelAndView;
	}
	
	@RequestMapping(value="getFileTable")
	public @ResponseBody Set<String> getFileTable(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String bucketName = OssConfig.getValue("bakBucketName");
		return OssOperate.getAllRootFiles(bucketName);
	}
	
	@RequestMapping(value="getUpdateInfo")
	public void getUpdateInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String bakSerFile = OssConfig.getValue("bakSerFile");
		out.write((String)ObjSave.fileToObject(bakSerFile));
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="changeRule")
	public void changeRule(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		int type = Integer.parseInt(request.getParameter("type"));
		PrintWriter out = response.getWriter();
		String ruleSerFile = OssConfig.getValue("ruleSerFile");
		String ruleStr = (String)ObjSave.fileToObject(ruleSerFile);
		String[] rules = ruleStr.split("\\|");
		if("0".equals(rules[type])){
			rules[type] = "1";
		}else{
			rules[type] = "0";
		}
		String newRule = "";
		for (String rule : rules) {
			newRule += rule + "|";
		}
		newRule = newRule.substring(0,newRule.length() - 1);
		ObjSave.objectToFile(newRule, ruleSerFile);
		out.write((String)ObjSave.fileToObject(ruleSerFile));
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="download")
	public void download(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String bucket = request.getParameter("bucket");
		String filePath = request.getParameter("fileName");
		//先下载到本地
		filePath = OSSUtil.moveTolocal(bucket, filePath);
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return;
		}
		OutputStream out = null;
		try {
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename="+ file.getName());
			out = response.getOutputStream();
			out.write(FileUtils.readFileToByteArray(file));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
