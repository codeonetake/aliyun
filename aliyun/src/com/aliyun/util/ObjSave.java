package com.aliyun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjSave {
	public static void objectToFile(Object obj,String filePath) {
		try {
			File file = new File(filePath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			FileOutputStream fs = new FileOutputStream(filePath);
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(obj);
			os.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Object fileToObject(String filePath) {
		try {
			FileInputStream fs = new FileInputStream(filePath);
			ObjectInputStream oi = new ObjectInputStream(fs);
			Object obj = oi.readObject();
			oi.close();
			fs.close();
			return obj;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
