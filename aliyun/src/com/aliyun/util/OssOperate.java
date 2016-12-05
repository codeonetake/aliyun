package com.aliyun.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.aliyun.bean.OssFile;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.LocationConstraint;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectRequest;

public class OssOperate {
	private static String endpoint = OssConfig.getValue("endpoint");
	private static String accessKeyId = OssConfig.getValue("accessKeyId");
	private static String accessKeySecret = OssConfig.getValue("accessKeySecret");
	private static ClientConfiguration configuration = null;
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static{
		configuration = new ClientConfiguration();
		configuration.setConnectionTimeout(6000);
		configuration.setConnectionRequestTimeout(6000);
	}

	public static OSSClient getClient(){
		System.out.println("configuration:");
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret, configuration);
		return ossClient;
	}
	
	public static void createBucket(String bucketName, boolean isPrivate) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
			if (isPrivate) {
				createBucketRequest.setCannedACL(CannedAccessControlList.Private);
			} else {
				createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
			}
			createBucketRequest.setLocationConstraint(LocationConstraint.OSS_CN_HONGKONG);
			ossClient.createBucket(createBucketRequest);
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}

	public static List<Bucket> listBucket() {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			return ossClient.listBuckets();
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	
	public static List<String> getAllFiles(String bucketName,String prefix){
		OSSClient ossClient = null;
		List<String> fileNames = new ArrayList<String>();
		try {
			ossClient = getClient();
			List<OSSObjectSummary> objects = ossClient.listObjects(bucketName, prefix).getObjectSummaries();
			for (OSSObjectSummary ossObjectSummary : objects) {
				fileNames.add(ossObjectSummary.getKey());
			}
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
		return fileNames;
	}
	
	public static void main(String[] args) {
		List<String> keys = getAllFiles("test-codeonetake","aliyun");
		for (String string : keys) {
			System.out.println(string);
		}
	}
	
	public static Set<String> getAllRootFiles(String bucketName){
		OSSClient ossClient = null;
		Set<String> fileNames = new TreeSet<String>();
		try {
			ossClient = getClient();
			ListObjectsRequest request = new ListObjectsRequest();
			request.setBucketName(bucketName);
			request.setMaxKeys(1000);
			List<OSSObjectSummary> objects = ossClient.listObjects(request).getObjectSummaries();
			String key = null;
			for (OSSObjectSummary ossObjectSummary : objects) {
				key = ossObjectSummary.getKey();
				if(key.indexOf("/")!=-1){
					fileNames.add(key.substring(0,key.indexOf("/")));
				}else{
					fileNames.add(key);
				}
			}
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
		return fileNames;
	}
	
	public static List<OssFile> getAllFileDetail(String bucketName,String prefix){
		OSSClient ossClient = null;
		List<OssFile> ossFiles = new ArrayList<OssFile>();
		try {
			ossClient = getClient();
			List<OSSObjectSummary> objects = ossClient.listObjects(bucketName, prefix).getObjectSummaries();
			OssFile ossFile = null;
			for (OSSObjectSummary ossObjectSummary : objects) {
				ossFile = new OssFile();
				ossFile.setFileName(ossObjectSummary.getKey());
				ossFile.setModifyTime(format.format(ossObjectSummary.getLastModified()));
				ossFile.setSize(FileUtil.getSize(ossObjectSummary.getSize()));
				ossFiles.add(ossFile);
			}
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
		return ossFiles;
	}
	
	public static void uploadFile(File file,String bucketName,String fileName,boolean isPrivate){
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			ossClient.putObject(new PutObjectRequest(bucketName, fileName, file));
			if(isPrivate){
				ossClient.setObjectAcl(bucketName, fileName, CannedAccessControlList.Private);
			}else{
				ossClient.setObjectAcl(bucketName, fileName, CannedAccessControlList.PublicRead);
			}
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	
	public static void uploadFile(File file,String bucketName,boolean isPrivate){
		uploadFile(file, bucketName, file.getName(), isPrivate);
	}
	
	public static boolean downloadFile(String bucketName,String fileName,String saveFilePath){
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			if(ossClient.doesObjectExist(bucketName, fileName)){
				OSSObject object = ossClient.getObject(bucketName, fileName);
				return FileUtil.saveFile(object.getObjectContent(), saveFilePath);
			}
			return false;
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	
	public static void deleteFile(String bucketName,String fileName){
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			ossClient.deleteObject(bucketName, fileName);
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
}
