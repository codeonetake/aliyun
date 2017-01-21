package com.aliyun.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.aliyun.bean.Festival;
import com.aliyun.service.FestivalService;
import com.aliyun.util.OssConfig;
import com.aliyun.util.RedisPool;

@Controller
@RequestMapping(value="/festival")
public class FestivalController {

	@RequestMapping(value="")
	public ModelAndView cdnIndex(HttpServletRequest request,HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("festival");
		mav.addObject("festivals", FestivalService.getAll());
		return mav;
	}
	
	@RequestMapping(value="/add")
	public void add(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String date = request.getParameter("addDate");
		String name = request.getParameter("addName");
		String backColor = request.getParameter("addBackColor");
		String picId = request.getParameter("addPicId");
		String weixinPic = "";
		
		Festival festival = new Festival();
		festival.setBackColor(backColor);
		festival.setCreateTime(new Date());
		festival.setDate(date);
		festival.setModifyTime(new Date());
		festival.setName(name);
		festival.setPicId(picId);
		festival.setWeixinPic(weixinPic);
		
		try {
			FestivalService.addFestival(festival);
			out.write("添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("添加失败");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="/change")
	public void change(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String idString = request.getParameter("changeId");
		String date = request.getParameter("changeDate");
		String name = request.getParameter("changeName");
		String backColor = request.getParameter("changeBackColor");
		String picId = request.getParameter("changePicId");
		String weixinPic = "";
		
		int id = 0;
		try {
			id = Integer.parseInt(idString);
		} catch (Exception e) {
			e.printStackTrace();
			out.write("id不是整数");
			out.flush();
			out.close();
			return;
		}
		
		Festival festival = FestivalService.getById(id);
		if(null == festival){
			out.write("该节日不存在，请重新添加");
			out.flush();
			out.close();
			return;
		}
		
		festival.setBackColor(backColor);
		festival.setCreateTime(new Date());
		festival.setDate(date);
		festival.setModifyTime(new Date());
		festival.setName(name);
		festival.setPicId(picId);
		festival.setWeixinPic(weixinPic);
		
		try {
			FestivalService.changeFestival(festival);
			out.write("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("修改失败");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="/del")
	public void del(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String idString = request.getParameter("id");
		
		int id = 0;
		try {
			id = Integer.parseInt(idString);
		} catch (Exception e) {
			e.printStackTrace();
			out.write("id不是整数");
			out.flush();
			out.close();
			return;
		}
		
		try {
			FestivalService.delFestival(id);
			out.write("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("删除失败");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="/exec")
	public void exec(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String idString = request.getParameter("id");
		
		int id = 0;
		try {
			id = Integer.parseInt(idString);
		} catch (Exception e) {
			e.printStackTrace();
			out.write("id不是整数");
			out.flush();
			out.close();
			return;
		}
		Festival festival = FestivalService.getById(id);
		if(null == festival){
			out.write("对应的节日不存在");
			out.flush();
			out.close();
			return;
		}
		String currentBackColor = RedisPool.get("currentBackColor");
		String currentPicId = RedisPool.get("currentPicId");
		if(currentBackColor.equals(festival.getBackColor())){
			out.write("当前已经是该节日主题，不更改");
			out.flush();
			out.close();
			return;
		}
		String toChangeBackColor = festival.getBackColor();
		String toChangePicId = festival.getPicId();
		try {
			FestivalService.changeOptions("theme_mods_twentyfifteen", currentBackColor, toChangeBackColor);
			FestivalService.changeOptions("theme_mods_twentyfifteen", "i:"+currentPicId, "i:"+toChangePicId);
			FestivalService.copyPic(toChangeBackColor);
			RedisPool.set("currentBackColor", toChangeBackColor);
			RedisPool.set("currentPicId", toChangePicId);
			out.write("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("修改失败");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value="/defaultTheme")
	public void defaultTheme(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String currentBackColor = RedisPool.get("currentBackColor");
		String currentPicId = RedisPool.get("currentPicId");
		String defaultBackColor = RedisPool.get("defaultBackColor");
		String defaultPicId = RedisPool.get("defaultPicId");
		if(currentBackColor.equals(defaultBackColor)){
			out.write("当前已经是默认主题，不更改");
			out.flush();
			out.close();
			return;
		}
		try {
			FestivalService.changeOptions("theme_mods_twentyfifteen", currentBackColor, defaultBackColor);
			FestivalService.changeOptions("theme_mods_twentyfifteen", "i:"+currentPicId, "i:"+defaultPicId);
			FestivalService.copyPic(defaultBackColor);
			RedisPool.set("currentBackColor", defaultBackColor);
			RedisPool.set("currentPicId", defaultPicId);
			out.write("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("修改失败");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping(value = "uploadFileAjax")
	public void uploadFileAjax(@RequestParam MultipartFile weixinPic,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			if (weixinPic != null && !weixinPic.isEmpty()) {
				String originalFilename = weixinPic.getOriginalFilename();
				if (null != originalFilename && originalFilename.endsWith(".png")) {
					String newFileName = "";
					if(originalFilename.startsWith("weixin_")){
						newFileName = originalFilename;
					}else{
						newFileName = "weixin_"+originalFilename;
					}
					if(newFileName.length() != 17){
						out.write("文件名有误");
					}else{
						FileUtils.copyInputStreamToFile(weixinPic.getInputStream(),new File(OssConfig.getValue("weixinPicPath"), newFileName));
						out.write("上传成功，文件名为："+newFileName);
					}
				} else {
					out.write("请上传png文件");
					out.flush();
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}