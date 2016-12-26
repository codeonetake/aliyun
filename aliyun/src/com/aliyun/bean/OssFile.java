package com.aliyun.bean;
/**
 * oss文件类
 * @author liuwenbin
 *
 */
public class OssFile implements Comparable<OssFile>{
	//文件名
	private String fileName;
	//文件当前路径
	private String currentName;
	//修改时间
	private String modifyTime;
	//文件大小
	private String size;
	//类型，0：文件；1：文件夹
	private Integer type;
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getCurrentName() {
		return currentName;
	}
	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return ((OssFile)obj).getFileName().equals(this.fileName);
	}
	
	public int compareTo(OssFile o) {
		return o.getType().compareTo(this.getType());
	}

}
