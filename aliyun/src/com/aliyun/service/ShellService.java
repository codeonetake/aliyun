package com.aliyun.service;

import com.aliyun.util.DoShell;

public class ShellService {
	public static void cleanMemery(){
		try {
			DoShell.shell("sync");
			DoShell.shell("echo 3 > /proc/sys/vm/drop_caches");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
