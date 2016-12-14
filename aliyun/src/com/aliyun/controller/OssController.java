package com.aliyun.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.bean.OssFile;
import com.aliyun.oss.model.Bucket;
import com.aliyun.util.DoShell;
import com.aliyun.util.OSSUtil;
import com.aliyun.util.OssConfig;
import com.aliyun.util.OssOperate;

@RequestMapping(value="/oss")
@Controller
public class OssController {
	
	@RequestMapping("")
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		ModelAndView mav = new ModelAndView();
		List<Bucket> buckets = OssOperate.listBucket();
		mav.addObject("buckets", buckets);
		mav.setViewName("oss");
		return mav;
	}
	@RequestMapping("/rootFile")
	public @ResponseBody List<OssFile> getRootFile(HttpServletRequest request,HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String bucket = request.getParameter("bucket");
		String prefix = request.getParameter("prefix");
		return OssOperate.getAllRootOssFile(bucket, prefix);
	}
	
	@RequestMapping("/checkPath")
	public void checkPath(HttpServletRequest request,HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String path = request.getParameter("path");
		String bucket = request.getParameter("bucket");
		if(path.startsWith("/")){
			path = path.substring(1);
		}
		boolean fileExist = OssOperate.fileExist(bucket, path);
		boolean dirExist = false;
		if(!fileExist){
			int size = OssOperate.getAllRootOssFile(bucket, path).size();
			if(0 == size){
				dirExist = false;
			}else{
				dirExist = true;
			}
		}
		if(fileExist){
			if(path.contains("/")){
				path = path.substring(0,path.lastIndexOf("/"));
			}else{
				path = "";
			}
		}
		if(!fileExist && !dirExist){
			out.write("|not file|");
		}else{
			out.write(path);
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="download")
	public void download(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String bucket = request.getParameter("bucket");
		String filePath = request.getParameter("filePath");
		int type = Integer.parseInt(request.getParameter("type"));
		//先下载到本地
		filePath = OSSUtil.moveTolocal(bucket, filePath, type);
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			System.out.println("no file");
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
	
	@RequestMapping(value="deleteFile")
	public void deleteFile(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String bucket = request.getParameter("bucket");
		String filePath = request.getParameter("filePath");
		PrintWriter out = response.getWriter();
		int type = Integer.parseInt(request.getParameter("type"));
		try {
			if(0 == type){
				OssOperate.deleteFile(bucket, filePath);
			}else{
				OssOperate.deleteDir(bucket, filePath);
			}
			out.write("success");
		} catch (Exception e) {
			out.write("error");
		}
		out.flush();
		out.close();
	}
	@RequestMapping(value = "uploadFileAjax")
	public void uploadFileAjax(@RequestParam MultipartFile ossUpFile,HttpServletRequest request, HttpServletResponse response)throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String filePath = OssConfig.getValue("downLoadTempPath");
		String shell = "rm -rf "+filePath+"*";
		String bucket = request.getParameter("bucket");
		String prefix = request.getParameter("prefix");
		try {
			DoShell.shell(shell);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			if (ossUpFile != null && !ossUpFile.isEmpty()) {
				String originalFilename = ossUpFile.getOriginalFilename();
				if (null != originalFilename) {
					File newFile = new File(filePath, originalFilename);
					FileUtils.copyInputStreamToFile(ossUpFile.getInputStream(),newFile);
					//开始上传到oss上
					if(!prefix.endsWith("/") && !"".equals(prefix)){
						prefix += "/";
					}
					OssOperate.uploadFile(newFile, bucket, prefix + newFile.getName(), true);
					try {
						DoShell.shell(shell);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					out.write("请上传文件");
					out.flush();
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
