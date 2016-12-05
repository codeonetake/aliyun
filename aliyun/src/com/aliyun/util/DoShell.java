package com.aliyun.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DoShell {
	public static List<String> shell(String shell) throws Exception{
		List<String> list = new ArrayList<String>();
		String[] command = {"/bin/sh", "-c", shell };
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			System.out.println(line);
			list.add(line);
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
		return list;
	}
	public static void main(String[] args) throws Exception {
		List<String> list = shell("ls /Users/liuwenbin/Desktop/");
		for (String string : list) {
			System.out.println(string);
		}
	}
}
