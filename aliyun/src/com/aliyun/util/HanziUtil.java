package com.aliyun.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class HanziUtil {
	public static long getCount(String content) {
		String E1 = "[\u4e00-\u9fa5]";
		long chineseCount = 0;

		String temp;
		for (int i = 0; i < content.length(); i++) {
			temp = String.valueOf(content.charAt(i));
			if (temp.matches(E1)) {
				chineseCount++;
			}
		}
		return chineseCount;
	}
	
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static String url = "jdbc:mysql://59.110.54.171:3056/wordpress?"+ "user=root&password=root_liuwenbin&useUnicode=true&characterEncoding=UTF8";

	public static void getByAll() throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_posts where post_status='publish'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
            		System.out.println(rs.getString("post_title")+" : "+getCount(rs.getString("post_content")));
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
	}
	public static void main(String[] args) throws Exception {
		getByAll();
	}
}
