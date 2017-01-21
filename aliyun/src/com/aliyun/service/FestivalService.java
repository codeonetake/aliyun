package com.aliyun.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aliyun.bean.CalendarDay;
import com.aliyun.bean.Festival;
import com.aliyun.util.CalendarUtil;
import com.aliyun.util.DoShell;
import com.aliyun.util.ObjSave;
import com.aliyun.util.OssConfig;
import com.aliyun.util.RedisPool;

public class FestivalService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MM-dd");
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
	private static String url = "jdbc:mysql://59.110.54.171:3056/wordpress?"+ "user=root&password=root_liuwenbin&useUnicode=true&characterEncoding=UTF8";
	
	public static void deleteErrorImg(){
		List<Festival> festivals = null;
		try {
			festivals = getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null == festivals){
			return;
		}
		Set<String> fileNames = new HashSet<String>();
		for (Festival festival : festivals) {
			fileNames.add("weixin_"+festival.getBackColor()+".png");
		}
		File[] files = new File(OssConfig.getValue("weixinPicPath")).listFiles();
		for (File file : files) {
			if(!fileNames.contains(file.getName())){
				file.delete();
			}
		}
	}
	
	public static void autoRunFestival(){
		m("判断节日开始");
		Date now = new Date();
		String date_yl = format.format(now);
		String date_yl_ym = yearMonthFormat.format(now);
		String date_nl = "";
		CalendarDay calendarDay = null;
		
		String defaultBackColor = RedisPool.get("defaultBackColor");
		String defaultPicId = RedisPool.get("defaultPicId");
		String currentBackColor = RedisPool.get("currentBackColor");
		String currentPicId = RedisPool.get("currentPicId");
		
		String inStatus = "";
		try {
			calendarDay = CalendarUtil.getCalendar(date_yl);
			date_nl = calendarDay.getResult().getData().getLunar();
			inStatus = "'"+date_nl+"','"+date_yl_ym+"'";
			m("日期："+date_yl_ym+" "+date_nl);
		} catch (Exception e) {
			inStatus = "'"+date_yl_ym+"'";
			m("日期："+date_yl_ym);
		}
		Festival festival = null;
		try {
			festival = getByStatus(inStatus);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(null == festival){
			m("没有对应的节日");
			//判断当前的值是不是默认值
			if(!defaultBackColor.equals(currentBackColor)){
				//不是就发生变化
				m("更改成默认值");
				try {
					changeOptions("theme_mods_twentyfifteen", currentBackColor, defaultBackColor);
					changeOptions("theme_mods_twentyfifteen", "i:"+currentPicId, "i:"+defaultPicId);
					copyPic(defaultBackColor);
					RedisPool.set("currentBackColor", defaultBackColor);
					RedisPool.set("currentPicId", defaultPicId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			m("有对应的节日，"+festival.getName());
			String toChangeBackColor = festival.getBackColor();
			String toChangePicId = festival.getPicId();
			if(!currentBackColor.equals(toChangeBackColor)){
				m("更换节日主题");
				try {
					changeOptions("theme_mods_twentyfifteen", currentBackColor, toChangeBackColor);
					changeOptions("theme_mods_twentyfifteen", "i:"+currentPicId, "i:"+toChangePicId);
					copyPic(toChangeBackColor);
					RedisPool.set("currentBackColor", toChangeBackColor);
					RedisPool.set("currentPicId", toChangePicId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		ObjSave.objectToFile(msg, "/root/data/aliyun/festival.ser");
	}
	
	public static void main(String[] args) {
		autoRunFestival();
		System.out.println(msg);
	}
	
	public static void copyPic(String colorName){
		String fromPic = OssConfig.getValue("weixinPicPath")+"weixin_"+colorName+".png";
		String toPic = "/usr/share/nginx/html/image/weixin.png";
		try {
			DoShell.shell("cp -r -f "+fromPic+" "+toPic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void changeOptions(String key,String fromVal,String toVal) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "update wp_options set option_value =  REPLACE (option_value,'"+fromVal+"','"+toVal+"') where option_name='"+key+"';";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
	}
	
	public static Festival getByStatus(String status) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        Festival festival = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_festival where date in("+status+")";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
            		festival = new Festival();
            		festival.setBackColor(rs.getString("backColor"));
            		festival.setCreateTime(rs.getTimestamp("createTime"));
            		festival.setDate(rs.getString("date"));
            		festival.setId(rs.getInt("id"));
            		festival.setModifyTime(rs.getTimestamp("modifyTime"));
            		festival.setName(rs.getString("name"));
            		festival.setPicId(rs.getString("picId"));
            		festival.setWeixinPic(rs.getString("weixinPic"));
            		break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
        return festival;
	}
	
	public static void addFestival(Festival festival) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "insert into wp_festival (date,name,backColor,picId,weixinPic,createTime,modifyTime) values" +
            		" ('"+festival.getDate()+"','"+festival.getName()+"','"+festival.getBackColor()+"','"+festival.getPicId()+"'," +
            		"'"+festival.getWeixinPic()+"','"+dateTimeFormat.format(festival.getCreateTime())+"','"+dateTimeFormat.format(festival.getModifyTime())+"')";
            System.out.println(sql);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
	}
	
	public static void delFestival(int id) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "delete from wp_festival where id = " + id;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
	}
	
	public static void changeFestival(Festival festival) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        festival.setModifyTime(new Date());
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "update wp_festival set date='"+festival.getDate()+"'" +
            		",name='"+festival.getName()+"',backColor='"+festival.getBackColor()+"'" +
            		",picId='"+festival.getPicId()+"',weixinPic='"+festival.getWeixinPic()+"'" +
            		",modifyTime='"+dateTimeFormat.format(festival.getModifyTime())+"' where id="+festival.getId();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
	}
	
	public static List<Festival> getAll() throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        List<Festival> festivals = new ArrayList<Festival>();
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_festival";
            ResultSet rs = stmt.executeQuery(sql);
            Festival festival = null;
            while(rs.next()){
            		festival = new Festival();
            		festival.setBackColor(rs.getString("backColor"));
            		festival.setCreateTime(rs.getTimestamp("createTime"));
            		festival.setDate(rs.getString("date"));
            		festival.setId(rs.getInt("id"));
            		festival.setModifyTime(rs.getTimestamp("modifyTime"));
            		festival.setName(rs.getString("name"));
            		festival.setPicId(rs.getString("picId"));
            		festival.setWeixinPic(rs.getString("weixinPic"));
            		festivals.add(festival);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
        return festivals;
	}
	
	public static Festival getById(int id) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        Festival festival = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_festival where id="+id;
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
            		festival = new Festival();
            		festival.setBackColor(rs.getString("backColor"));
            		festival.setCreateTime(rs.getTimestamp("createTime"));
            		festival.setDate(rs.getString("date"));
            		festival.setId(rs.getInt("id"));
            		festival.setModifyTime(rs.getTimestamp("modifyTime"));
            		festival.setName(rs.getString("name"));
            		festival.setPicId(rs.getString("picId"));
            		festival.setWeixinPic(rs.getString("weixinPic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			stmt.close();
        		}
        		if(null != conn){
        			conn.close();
        		}
        }
        return festival;
	}
	private static void m(String content){
		if(content.contains("(")){
			content = content.replaceAll("\\(", "<b>");
		}
		if(content.contains(")")){
			content = content.replaceAll("\\)", "</b>");
		}
		if(content.contains("[")){
			content = content.replaceAll("\\[", "<code>");
		}
		if(content.contains("]")){
			content = content.replaceAll("\\]", "</code>");
		}
		if(content.contains("成功")){
			content = content.replaceAll("成功", "<font color='green'><b>成功</b></font>");
		}
		if(content.contains("失败")){
			content = content.replaceAll("失败", "<font color='red'><b>失败</b></font>");
		}
		if(content.endsWith("开始") || content.endsWith("结束")){
			String time = dateTimeFormat.format(new Date());
			msg += content + "，时间："+time+"<br/>";
		}else{
			content = content.replaceAll("HIT", "<font color='green'>HIT</font>")
					.replaceAll("MISS", "<font color='red'>MISS</font>");
			msg += content + "<br/>";
		}
	}
}