package com.aliyun.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.mina.util.Base64;

public class CdnUtil {
	public static void main(String[] args) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.UK);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String url = "https://cdn.aliyuncs.com/?";
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("Action", "DescribeDomainHttpCodeData");
		//treeMap.put("DomainName", "img.codeawl.com");
		treeMap.put("Format", "JSON");
		treeMap.put("Version", "2014-11-11");
		treeMap.put("AccessKeyId", "LTAIOWHFyQYc3gQN");
		treeMap.put("SignatureMethod", "HMAC-SHA1");
		treeMap.put("TimeStamp", format.format(new Date()));
		treeMap.put("SignatureVersion", "1.0");
		treeMap.put("SignatureNonce", System.currentTimeMillis()+"");
		
		Set<String> keys = treeMap.keySet();
		String value = "";
		String param = "";
		System.out.println("TimeStamp:"+treeMap.get("TimeStamp"));
		for (String key : keys) {
			value += "&"+key+"="+URLEncoder.encode(treeMap.get(key));
			param += key+"="+URLEncoder.encode(treeMap.get(key))+"&";
		}
		value = value.substring(1);
		value = "GET&"+URLEncoder.encode("/")+"&"+URLEncoder.encode(value);
		System.out.println(value);
		
		String key = "kkN3gdS3x32g4etD7lpYbNnpYZmlmr&";
		String sign = getSign(value, key);
		System.out.println(sign);
		String finalUrl = url + param + "Signature="+URLEncoder.encode(sign);
		System.out.println(finalUrl);
		
	}
	
	public static String getSign(String value,String key) throws Exception{
		final String ALGORITHM = "HmacSHA1";
        final String ENCODING = "UTF-8";

        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(
                 key.getBytes(ENCODING), ALGORITHM));
        byte[] signData = mac.doFinal(value.getBytes(ENCODING));
        String signature = new String(Base64.encodeBase64(signData));
        return signature;
	}
}
