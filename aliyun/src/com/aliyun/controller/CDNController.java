package com.aliyun.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.util.CacheByPage;
import com.aliyun.util.ObjSave;

@Controller
@RequestMapping(value="/cdn")
public class CDNController {

	@RequestMapping(value="")
	public ModelAndView cdnIndex(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("cdn");
		return mav;
	}
	
	@RequestMapping("/getType")
	public @ResponseBody Map<String, String> getType(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String url = request.getParameter("url");
		if(null != url && !"".equals(url)){
			if(!url.startsWith("http")){
				url = "http://" + url;
			}
			return CacheByPage.get(url);
		}
		return new HashMap<String, String>();
	}
	
	@RequestMapping("/getMsg")
	public void getMsg(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String msg = ObjSave.fileToObject("/root/data/aliyun/cdn.ser").toString();
		out.write(msg);
		out.flush();
		out.close();
	}
}
