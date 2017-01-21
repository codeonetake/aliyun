package com.aliyun.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HolidayUtil {
	
	private static String url = "jdbc:mysql://59.110.54.171:3056/wordpress?"+ "user=root&password=root_liuwenbin&useUnicode=true&characterEncoding=UTF8";
	
	public static void changeColor(String fromColor,String toColor) {
		Connection conn = null;
        Statement stmt = null;
        String sql;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "update UPDATE wp_options SET option_value =  REPLACE (option_value,'bf2b5d','c2345d') where option_name = 'theme_mods_twentyfifteen';";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        		if(null != stmt){
        			//stmt.close();
        		}
        		if(null != conn){
        			//conn.close();
        		}
        }
	}
}
