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
import java.util.List;

import com.aliyun.util.DoShell;
import com.aliyun.util.FileUtil;
import com.aliyun.util.ObjSave;
import com.aliyun.util.RedisPool;

public class MipService {
	private static String msg = "";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String url = "jdbc:mysql://59.110.54.171:3056/wordpress?"+ "user=root&password=root_liuwenbin&useUnicode=true&characterEncoding=UTF8";

	public static void submitMipUrl(){
		m("提交MIP页面开始");
		String redisKey = "maxMipId";
		String fileName = "/root/data/aliyun/urls.txt";
		File urlsFile = new File(fileName);
		if(urlsFile.exists()){
			urlsFile.delete();
		}
		int maxId = 0;
		if(RedisPool.isExist(redisKey)){
			maxId = Integer.parseInt(RedisPool.get(redisKey));
		}
		m("最大的提交ID为："+maxId);
		int currentMaxId = 0;
		List<Integer> ids = null;
		try {
			ids = getIds(maxId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(null == ids || ids.size() == 0){
			m("没有需要提交的MIP页面");
			m("提交MIP页面结束");
			System.out.println(msg);
			ObjSave.objectToFile(msg, "/root/data/aliyun/mip.ser");
			return;
		}
		currentMaxId = ids.get(0);
		m("当前需要提交"+ids.size()+"个MIP页面");
		for (Integer id : ids) {
			try {
				FileUtil.write(fileName, "http://mip.codeawl.com/article/"+id+".html");
			} catch (Exception e) {
			}
		}
		try {
			List<String> res = DoShell.shell("curl -H 'Content-Type:text/plain' --data-binary @"+fileName+" \"http://data.zz.baidu.com/urls?site=mip.codeawl.com&token=vbAKhQsHRMjdYKjk&type=mip\"");
			for (String re : res) {
				m(re);
			}
			RedisPool.set(redisKey, currentMaxId+"");
		} catch (Exception e) {
			System.out.println("MIP提交失败");
		}
		m("提交MIP页面结束");
		System.out.println(msg);
		ObjSave.objectToFile(msg, "/root/data/aliyun/mip.ser");
	}
	public static void main(String[] args) {
		submitMipUrl();
	}
	public static List<Integer> getIds(int maxId) throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        List<Integer> ids = new ArrayList<Integer>();
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_posts where post_status='publish' and ID > " + maxId + " order by ID desc";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
            		ids.add(rs.getInt("ID"));
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
        return ids;
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
