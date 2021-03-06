//Google  Authenticator

// 只从google出了双重身份验证后，就方便了大家，等同于有了google一个级别的安全，但是我们该怎么使用google authenticator (双重身份验证)，

//下面是java的算法，这样大家都可以得到根据key得到公共的秘钥了,直接复制，记得导入JAR包：
//
//commons-codec-1.8.jar


//测试方法：
//
//1、执行测试代码中的"genSecret"方法，将生成一个KEY（用户为mapbar），URL打开是一张二维码图片。
//
//2、在手机中下载"GOOGLE身份验证器"。
//
//3、在身份验证器中配置账户，输入账户名（第一步中的用户mapbar）、密钥（第一步生成的KEY），选择基于时间。
//
//4、运行authcode方法将key和要测试的验证码带进去（codes，key），就可以知道是不是正确的秘钥了！返回值布尔

//main我就不写了大家~~因为这个可以当做util工具直接调用就行了
//

package com.aliyun.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;



public class GoogleAuthenticator {
    
    public static final int SECRET_SIZE = 10;
    
    public static final String SEED = "g8GjEvTbW5oVSV7avLBdwIHqGlUYNzKFI7izOF8GwLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";
    
    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
    
    int window_size = 3; // default 3 - max 17 (from google docs)最多可偏移的时间
  
    public void setWindowSize(int s) {
        if (s >= 1 && s <= 17)
            window_size = s;
    }
    
    public static Boolean authcode(String codes, String savedSecret) {
        long code = 0;
        try {
			code = Long.parseLong(codes);
		} catch (Exception e) {
			// TODO: handle exception
		}
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3); // should give 5 * 30 seconds of grace...
        boolean r = ga.check_code(savedSecret, code, t);
        return r;
    }
    public static String genSecret() {
        String secret = GoogleAuthenticator.generateSecretKey();
        GoogleAuthenticator.getQRBarcodeURL("aliyun","server59", secret);
        return secret;
    }
    public static String generateSecretKey() {
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            String encodedKey = new String(bEncodedKey);
            return encodedKey;
        }catch (NoSuchAlgorithmException e) {
            // should never occur... configuration error
        }
        return null;
    }
    
 
    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s";
        return String.format(format, user, host, secret);
    }
    

    public boolean check_code(String secret, long code, long timeMsec) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        long t = (timeMsec / 1000L) / 30L;
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = verify_code(decodedKey, t + i);
            }catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            if (hash == code) {
                return true;
            }
        }
        return false;
    }
    
    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }
    
    public static void main(String[] args) {
    		//只能运行一次--开始
		/*String secret = genSecret();
		System.out.println(secret);
		String url = getQRBarcodeURL("mapbar", "lieche", secret);
		//只能运行一次--结束，将获取到的secret填写到autocode方法中
		System.out.println(url);
		System.out.println(authcode("197498", "XEPIIXDSYQM43SWK"));*/
    		/*String secret = genSecret();
		System.out.println(secret);
		String url = getQRBarcodeURL("aliyun", "server59", secret);
		//只能运行一次--结束，将获取到的secret填写到autocode方法中
		System.out.println(url);*/
		System.out.println(authcode("271239", "OHXU6PLMZMDJIDY2"));
	}
}