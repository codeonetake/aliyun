package com.aliyun.util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.jsoup.Jsoup;

public class ImgUtil {
	private final String LANG_OPTION = "-l";
	private final String EOL = System.getProperty("line.separator");
	/**
	 * 文件位置我防止在，项目同一路径
	 */
	private String tessPath = new File("tesseract").getAbsolutePath();

	public static void cutCenterImage(String src, String dest, int x, int y,
			int w, int h) throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("png");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "png", new File(dest));
	}

	private static int totalTryCount = 0;

	public static String recognizeText(String PNGUrl) throws Exception {
		System.out.println(totalTryCount);
		String url = "http://www.free-ocr.com";
		String result = WebUtils.sendPost(url,
						"userfile=&userfile_url="
								+ PNGUrl
								+ "&requiredfile_userfile=1&language=eng&user_screen_width=980");
		if (null != result && result.contains("FW")) {
			result = result.trim();
			result = result.substring(result.indexOf("'") + 1,
					result.length() - 2);
		}
		url = url + result;
		System.out.println(url);
		result = WebUtils.doReq(url);
		if (null != result && result.contains("resultarea")) {
			result = Jsoup.parse(result).getElementById("resultarea").html()
					.trim();
		}
		if (null != result && result.contains("wait") && totalTryCount < 5) {
			totalTryCount++;
			Thread.sleep(5000);
			recognizeText(PNGUrl);
		}
		totalTryCount = 0;
		if (result.contains(",")) {
			result = result.replaceAll(",", "");
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		//System.out.println(recognizeText("http://aliyun.codeawl.com/img/1.PNG"));
		//System.out.println(recognizeText("http://aliyun.codeawl.com/img/2.PNG"));
		// cutCenterImage("/Users/liuwenbin/Desktop/IMG_8127.PNG",
		// "/Users/liuwenbin/Desktop/1.PNG", 0, 200, 450, 100);
		// cutCenterImage("/Users/liuwenbin/Desktop/IMG_8127.PNG",
		// "/Users/liuwenbin/Desktop/2.PNG", 450, 200, 300, 100);
	}
}
