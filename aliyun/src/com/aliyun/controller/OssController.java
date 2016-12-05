package com.aliyun.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.oss.model.Bucket;
import com.aliyun.util.OSSUtil;
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
	public @ResponseBody Set<String> getRootFile(HttpServletRequest request,HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String bucket = request.getParameter("bucket");
		return OssOperate.getAllRootFiles(bucket);
	}
	
	@RequestMapping(value="download")
	public void download(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String bucket = request.getParameter("bucket");
		String filePath = request.getParameter("filePath");
		//先下载到本地
		filePath = OSSUtil.moveTolocal(bucket, filePath);
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
}
