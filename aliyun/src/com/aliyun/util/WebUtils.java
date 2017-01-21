package com.aliyun.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * web请求帮助类
 * @author yangjl
 *
 */
public class WebUtils {
	
	/**默认字符编码**/
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	public static final String CHARSET_GBK = "GBK";

	/**
	 * 请求http
	 * @param web_url
	 * @return 响应内容
	 */
	public static String doReq(String web_url){
		try{
			if(web_url.length() > 0){
				URL url = new URL(web_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
				httpURLConnection.setConnectTimeout(5000);
				httpURLConnection.setReadTimeout(10000);
				
				InputStream inputStream = httpURLConnection.getInputStream();
				
				return getStreamAsString(inputStream, DEFAULT_CHARSET);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	/**
	 * 把数据流转成string
	 * @param stream
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}
	/**
	 * 获得POST 过来参数设置到新的params中
	 * @param request
	 * @return
	 */
	public static Map<String,String> getParams(HttpServletRequest request){
		Map<String,String> params = new HashMap<String,String>();
		Map<String,String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter
				.hasNext();) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";				
			}
			params.put(name, valueStr);
		}
		return params;
	}
}
