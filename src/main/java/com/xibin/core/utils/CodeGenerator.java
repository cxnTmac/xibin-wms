package com.xibin.core.utils;

import org.apache.commons.lang.time.FastDateFormat;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenerator {
	private static final FastDateFormat pattern = FastDateFormat.getInstance("yyyyMMddHHmmss");
	private static final AtomicInteger atomicInteger = new AtomicInteger(1);
	private static ThreadLocal<StringBuilder> threadLocal = new ThreadLocal<StringBuilder>();

	// static public String getCodeByCurrentTimeAndRandomNum(String prefix) {
	// Date date = new Date();
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	// Random random = new Random();
	// int s = random.nextInt(999) % (999 - 0 + 1) + 0;
	// String numStr = intToString(3, s);
	// return prefix + sdf.format(date) + numStr;
	// }
	// 新的方法
	static public String getCodeByCurrentTimeAndRandomNum(String prefix) {
		String lock = UUID.randomUUID().toString();
		StringBuilder builder = new StringBuilder(pattern.format(Instant.now().toEpochMilli()));// 取系统当前时间作为订单号前半部分
		builder.append(Math.abs(lock.hashCode()));// HASH-CODE
		builder.append(atomicInteger.getAndIncrement());// 自增顺序
		threadLocal.set(builder);
		return prefix + threadLocal.get().toString();
	}

	static private String intToString(int length, int num) {
		String result = "" + num;
		if (("" + num).length() < length) {
			for (int i = 0; i < length - ("" + num).length(); i++) {
				result = '0' + result;
			}
		}
		return result;
	}

}
