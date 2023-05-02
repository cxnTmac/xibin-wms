package com.xibin.core.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.WXConstants;
import com.xibin.core.pojo.AccessToken;
import com.xibin.core.pojo.JsApiTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;


@Component
@EnableScheduling
public class AccessTokenUtil {
	private static Logger log = LoggerFactory.getLogger(AccessTokenUtil.class);
	public static AccessToken accessToken=null;
	public static JsApiTicket jsApiTicket=null;

	/**
	 * @Author:yangwl
	 * @date 2016年5月11日 下午5:25:03
	 * @Description: //设置access_token
	 */
	@Scheduled(initialDelay = 100000, fixedDelay = 7100000)
	public static void initAndSetAccessToken() {
		System.out.println("获取AccessToken");
		getAccessToken(WXConstants.APP_ID, WXConstants.APP_SECRET);
		getJsApiTicket();
		log.info("execute initAndSetAccessToken End   : {}");
	}

	/**
	 * 获取access_token
	 * 
	 * @param appid
	 *            凭证
	 * @param appsecret
	 *            密钥
	 * @return
	 */
	private static void getAccessToken(String appid, String appsecret) {
		String requestUrl = WXConstants.ACCESS_TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				AccessTokenUtil.accessToken = new AccessToken();
				AccessTokenUtil.accessToken.setToken(jsonObject.getString("access_token"));
				AccessTokenUtil.accessToken.setExpiresIn(jsonObject.getIntValue("expires_in"));
			} catch (JSONException e) {
				AccessTokenUtil.accessToken = null;
				// 获取token失败
				log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"),
						jsonObject.getString("errmsg"));
			}
		}
	}

	private static void getJsApiTicket() {
		if(AccessTokenUtil.accessToken == null){
			log.error("accessToken为空，无法获取JsApiTicket");
			return;
		}
		String requestUrl = WXConstants.JSAPI_TICKET.replace("ACCESS_TOKEN", AccessTokenUtil.accessToken.getToken());
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				AccessTokenUtil.jsApiTicket = new JsApiTicket();
				AccessTokenUtil.jsApiTicket.setTicket(jsonObject.getString("ticket"));
				AccessTokenUtil.jsApiTicket.setExpiresIn(jsonObject.getIntValue("expires_in"));
			} catch (JSONException e) {
				AccessTokenUtil.jsApiTicket = null;
				// 获取jsApiTicket失败
				log.error("获取jsApiTicket失败 errcode:{} errmsg:{}");
			}
		}
	}

	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.");
		} catch (Exception e) {
			log.error("https request error:{}", e);
		}
		return jsonObject;
	}
}
