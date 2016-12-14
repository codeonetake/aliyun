package com.aliyun.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.aliyun.bean.OssFile;
import com.aliyun.bean.OssPage;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.HeadObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.LocationConstraint;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectRequest;
import com.google.gson.Gson;
import com.sun.org.apache.commons.beanutils.BeanComparator;

/**
 * 阿里云oss基础操作类
 * @author liuwenbin
 *
 */
public class OssOperate {
	//阿里云的endpoint，请去oss的控制台查看
	private static String endpoint = OssConfig.getValue("endpoint");
	//阿里云的accessKeyId和accessKeySecret
	private static String accessKeyId = OssConfig.getValue("accessKeyId");
	private static String accessKeySecret = OssConfig.getValue("accessKeySecret");
	
	private static ClientConfiguration configuration = null;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		configuration = new ClientConfiguration();
		configuration.setConnectionTimeout(6000);
		configuration.setConnectionRequestTimeout(6000);
	}

	/**
	 * 获取oss客户端
	 * @return oss客户端
	 */
	public static OSSClient getClient() {
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret, configuration);
		return ossClient;
	}
	/**
	 * 创建bucket
	 * @param bucketName bucket的名称
	 * @param isPrivate 是否是私有（如果为tue，权限为私有，如果为false，权限是公共读）
	 */
	public static void createBucket(String bucketName, boolean isPrivate) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(
					bucketName);
			if (isPrivate) {
				createBucketRequest
						.setCannedACL(CannedAccessControlList.Private);
			} else {
				createBucketRequest
						.setCannedACL(CannedAccessControlList.PublicRead);
			}
			createBucketRequest
					.setLocationConstraint(LocationConstraint.OSS_CN_HONGKONG);
			ossClient.createBucket(createBucketRequest);
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	public static boolean fileExist(String bucketName, String path) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			return ossClient.doesObjectExist(bucketName, path);
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	public static void main(String[] args) {
		System.out.println(fileExist("test-codeonetake", "1/1"));
	}
	/**
	 * 获取所有的bucket
	 * @return bucket对象列表
	 */
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
	/**
	 * 获取所有的根目录
	 * @param bucketName bucket名称
	 * @return 根目录文件名列表
	 */
	public static Set<String> getAllRootFiles(String bucketName) {
		OSSClient ossClient = null;
		Set<String> rootDirs = new TreeSet<String>();
		try {
			ossClient = getClient();
			ObjectListing objectListing = null;
			String nextMarker = null;
			final int maxKeys = 1000;
			String dirName = null;
			do {
				ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withPrefix("").withMarker(nextMarker).withMaxKeys(maxKeys);
				objectListing = ossClient.listObjects(listObjectsRequest);
				List<OSSObjectSummary> summrayList = objectListing.getObjectSummaries();
				for (OSSObjectSummary ossObjectSummary : summrayList) {
					dirName = ossObjectSummary.getKey();
					if(dirName.contains("/")){
						dirName = dirName.substring(0,dirName.indexOf("/"));
					}
					rootDirs.add(dirName);
				}
				nextMarker = objectListing.getNextMarker();
			} while (objectListing.isTruncated());
			return rootDirs;
		}finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	
	public static List<OssFile> getAllRootOssFile(String bucketName,String prefix) {
		if(!prefix.endsWith("/") && !prefix.equals("")){
			prefix += "/";
		}
		OSSClient ossClient = null;
		List<OssFile> rootDirs = new ArrayList<OssFile>();
		try {
			ossClient = getClient();
			ObjectListing objectListing = null;
			String nextMarker = null;
			final int maxKeys = 1000;
			String dirName = null;
			OssFile ossFile = null;
			do {
				ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withPrefix(prefix).withMarker(nextMarker).withMaxKeys(maxKeys);
				objectListing = ossClient.listObjects(listObjectsRequest);
				List<OSSObjectSummary> summrayList = objectListing.getObjectSummaries();
				for (OSSObjectSummary ossObjectSummary : summrayList) {
					ossFile = new OssFile();
					dirName = ossObjectSummary.getKey();
					if(dirName.endsWith("/")){
						//文件夹
						ossFile.setType(1);
					}else{
						//文件
						ossFile.setType(0);
					}
					if(!"".equals(prefix)){
						//替换前缀
						dirName = dirName.replaceAll(prefix, "");
					}
					if(dirName.contains("/")){
						//获取文件名
						dirName = dirName.substring(0,dirName.indexOf("/"));
						ossFile.setType(1);
					}
					if("".equals(dirName)){
						continue;
					}
					if(0 == ossFile.getType()){
						ossFile.setModifyTime(format.format(ossObjectSummary.getLastModified()));
						ossFile.setSize(FileUtil.getSize(ossObjectSummary.getSize()));
					}
					ossFile.setFileName(dirName);
					ossFile.setCurrentName(prefix + dirName);
					if(!rootDirs.contains(ossFile)){
						rootDirs.add(ossFile);
					}
				}
				nextMarker = objectListing.getNextMarker();
			} while (objectListing.isTruncated());
			Collections.sort(rootDirs);
			return rootDirs;
		}finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	/**
	 * 上传文件
	 * @param file 本地文件
	 * @param bucketName bucket名称
	 * @param fileName oss文件路径，如test/test.txt
	 * @param isPrivate 是否是私有（为true，访问权限为私有，为false，访问权限为公共读）
	 */
	public static void uploadFile(File file, String bucketName,String fileName, boolean isPrivate) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			ossClient.putObject(new PutObjectRequest(bucketName, fileName, file));
			if (isPrivate) {
				ossClient.setObjectAcl(bucketName, fileName,CannedAccessControlList.Private);
			} else {
				ossClient.setObjectAcl(bucketName, fileName,CannedAccessControlList.PublicRead);
			}
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	
	/**
	 * 上传文件
	 * @param file 本地文件
	 * @param bucketName bucket名称
	 * @param isPrivate 是否是私有（为true，访问权限为私有，为false，访问权限为公共读）
	 */
	public static void uploadFile(File file, String bucketName,boolean isPrivate) {
		uploadFile(file, bucketName, file.getName(), isPrivate);
	}
	/**
	 * 下载文件
	 * @param bucketName bucket名称
	 * @param fileName oss文件路径
	 * @param saveFilePath 本地文件路径
	 * @return 是否下载成功
	 */
	public static boolean downloadFile(String bucketName, String fileName,String saveFilePath) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			if (ossClient.doesObjectExist(bucketName, fileName)) {
				OSSObject object = ossClient.getObject(bucketName, fileName);
				return FileUtil.saveFile(object.getObjectContent(),saveFilePath);
			}
			return false;
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	/**
	 * 删除目录
	 * @param bucketName bucket名称
	 * @param dirName 路径名称
	 */
	public static void deleteDir(String bucketName, String dirName) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			List<OSSObjectSummary> objects = ossClient.listObjects(bucketName,dirName).getObjectSummaries();
			for (OSSObjectSummary ossObjectSummary : objects) {
				ossClient.deleteObject(bucketName, ossObjectSummary.getKey());
			}
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	/**
	 * 删除文件
	 * @param bucketName bucket名称
	 * @param fileName 文件名称
	 */
	public static void deleteFile(String bucketName, String fileName) {
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
	
	/**
	 * 分页查询文件
	 * @param bucketName bucket名称
	 * @param dir 文件前缀
	 * @param nextMarker 下一个文件名
	 * @param maxKeys 最大查询条数
	 */
	public static OssPage listPage(String bucketName, String dir,String nextMarker, Integer maxKeys) {
		OSSClient ossClient = null;
		try {
			ossClient = getClient();
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
			listObjectsRequest.setPrefix(dir);
			listObjectsRequest.setMarker(nextMarker);
			if (maxKeys != null) {
				listObjectsRequest.setMaxKeys(maxKeys);
			}
			ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
			List<OSSObjectSummary> summrayList = objectListing.getObjectSummaries();
			OssPage page = new OssPage();
			String newxNextMarker = objectListing.getNextMarker();
			page.setNextMarker(newxNextMarker);
			page.setSummrayList(summrayList);
			return page;
		}finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
	/**
	 * 一次迭代，获得某个目录下的所有文件列表
	 * @param bucketName bucket名称
	 * @param dir 文件前缀
	 * @return OSSObjectSummary列表
	 */
	public static List<OSSObjectSummary> getAllFile(String bucketName, String dir) {
		if(!dir.endsWith("/")){
			dir += "/";
		}
		OSSClient ossClient = null;
		List<OSSObjectSummary> allSummaries = new ArrayList<OSSObjectSummary>();
		try {
			ossClient = getClient();
			ObjectListing objectListing = null;
			String nextMarker = null;
			final int maxKeys = 1000;
			do {
				ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withPrefix(dir).withMarker(nextMarker).withMaxKeys(maxKeys);
				objectListing = ossClient.listObjects(listObjectsRequest);
				List<OSSObjectSummary> summrayList = objectListing.getObjectSummaries();
				allSummaries.addAll(summrayList);
				nextMarker = objectListing.getNextMarker();
			} while (objectListing.isTruncated());
			return allSummaries;
		}finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
	}
}