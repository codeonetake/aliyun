package com.aliyun.controller;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.util.ObjSave;

@Controller
@RequestMapping(value="/info")
public class InfoController {

	@RequestMapping(value="")
	public ModelAndView infoIndex(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("info");
		File[] files = new File("/root/data/aliyun/").listFiles();
		Map<String,String> content = new TreeMap<String, String>();
		for (File file : files) {
			if(file.isFile() && file.getName().endsWith(".ser")){
				content.put(file.getName(),ObjSave.fileToObject(file.getAbsolutePath()).toString());
			}
		}
		mav.addObject("content",content);
		return mav;
	}
}