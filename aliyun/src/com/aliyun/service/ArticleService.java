package com.aliyun.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;

import com.aliyun.bean.ArticleSpeed;
import com.aliyun.bean.ArticleSpeedTongji;
import com.aliyun.bean.Festival;
import com.aliyun.util.RedisPool;
import com.google.gson.Gson;

public class ArticleService {
	private static String url = "jdbc:mysql://59.110.54.171:3056/wordpress?"+ "user=root&password=root_liuwenbin&useUnicode=true&characterEncoding=UTF8";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static void articleSpeed() throws Exception{
		Connection conn = null;
        Statement stmt = null;
        String sql;
        List<ArticleSpeed> articleSpeeds = new ArrayList<ArticleSpeed>();
        ArticleSpeed articleSpeed = null;
        articleSpeed = new ArticleSpeed();
        articleSpeed.setTitle("首页");
        articleSpeed.setUrl("http://codeawl.com");
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_posts where post_status='publish' order by ID desc limit 0,30";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
            		articleSpeed = new ArticleSpeed();
            		articleSpeed.setTitle(rs.getString("post_title"));
            		articleSpeed.setUrl("http://codeawl.com/archives/"+rs.getInt("ID"));
            		articleSpeeds.add(articleSpeed);
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
        //开始检测速度
        for (ArticleSpeed as : articleSpeeds) {
			try {
				long start = System.currentTimeMillis();
				Jsoup.connect(as.getUrl()).timeout(20000).get();
				long period = System.currentTimeMillis() - start;
				as.setSpeed(period+"");
			} catch (Exception e) {
				as.setSpeed("超时");
			}
		}
        ArticleSpeedTongji tongji = new ArticleSpeedTongji();
        tongji.setTime(dateFormat.format(new Date()));
        tongji.setArticleSpeeds(articleSpeeds);
        RedisPool.set("articleSpeed", new Gson().toJson(tongji));
	}
}
