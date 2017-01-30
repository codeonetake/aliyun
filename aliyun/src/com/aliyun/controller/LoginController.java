package com.aliyun.controller;

import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aliyun.util.GoogleAuthenticator;

@Controller
@RequestMapping(value="")
public class LoginController {

	@RequestMapping("validatePassword")
	public void validatePassword(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception {
		System.out.println("OK");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String password = request.getParameter("password");
		System.out.println("password:"+password);
		if(null != password && GoogleAuthenticator.authcode(password, "OHXU6PLMZMDJIDY2")){
			out.write("success");
			Cookie cookie = new Cookie("isLogin", "1");
			cookie.setMaxAge(60 * 60 * 24 *30);
			response.addCookie(cookie);
		}else{
			out.write("error");
		}
		out.flush();
		out.close();
	}
}
