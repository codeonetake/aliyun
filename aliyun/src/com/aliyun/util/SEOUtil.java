package com.aliyun.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SEOUtil {

	private static String url = "jdbc:mysql://59.110.54.171:3056/wordpress?" + "user=root&password=root_liuwenbin&useUnicode=true&characterEncoding=UTF8";
	private static String indexUrl = "http://codeawl.com";
	private static String articleUrl = "http://codeawl.com/archives/";
	private static String categoryUrl = "http://codeawl.com/archives/category/";
	private static String changefreq = "weekly";
	private static String filePath = "/usr/share/nginx/html/sitemap_baidu.xml";
	
	//private static String filePath = "/Users/liuwenbin/Desktop/sitemap_baidu.xml";
	
	private static String fileHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<urlset>\r\n";
	private static String fileFoot = "</urlset>";
	
	public static int makeSiteMap() throws Exception {
		int count = 0;
		String xml = "";
		String articleXml = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String indexTime = null;
		Connection conn = null;
        Statement stmt = null;
        String sql;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_posts where post_status = 'publish' order by post_modified desc;";
            ResultSet rs = stmt.executeQuery(sql);
            Date date = null;
            while (rs.next()) {
            		count ++;
            		date = rs.getDate("post_modified");
            		if(null == indexTime){
            			indexTime = simpleDateFormat.format(date);
            		}
            		articleXml += makeUrlItem(articleUrl + rs.getString("ID"), simpleDateFormat.format(date), "0.7");
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
        //生成首页item
        count ++;
        String indexXml = makeUrlItem(indexUrl, indexTime, "1.0");
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            sql = "select * from wp_terms where slug != 'uncategorized'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
            		count ++;
            		articleXml += makeUrlItem(categoryUrl + rs.getString("slug"), indexTime, "0.3");
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
        xml = fileHead + indexXml + articleXml + fileFoot;
        	FileUtil.writeNotContinue(filePath, xml);
        return count;
	}
	private static String makeUrlItem(String url,String date,String priority){
		return "\t<url>\r\n" +
				"\t\t<loc>" + url + "</loc>\r\n" +
				"\t\t<lastmod>" + date + "</lastmod>\r\n" +
				"\t\t<changefreq>" + changefreq + "</changefreq>\r\n" +
				"\t\t<priority>" + priority + "</priority>\r\n" +
				"\t</url>\r\n";
	}
}
