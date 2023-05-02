package com.xibin.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.xibin.core.costants.WXConstants;

public class SignUtil {
	public static Map<String, String> sign(String jsapi_ticket, String url) {
		Map<String, String> ret = new HashMap<String, String>();
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
		System.out.println(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8")); // 对string1 字符串进行SHA-1加密处理
			signature = byteToHex(crypt.digest()); // 对加密后字符串转成16进制
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);
		ret.put("appId", WXConstants.APP_ID);
		System.out.println("jsapi_ticket" + jsapi_ticket);
		System.out.println("nonceStr" + nonce_str);
		System.out.println("timestamp" + timestamp);
		System.out.println("url" + url);
		System.out.println("string1:" + string1);
		System.out.println("signature:" + signature);
		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	// 生成随机字符串
	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	// 生成时间戳字符串
	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}
}
