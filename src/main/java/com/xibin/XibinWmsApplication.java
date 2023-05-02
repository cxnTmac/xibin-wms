package com.xibin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

//@MapperScan("com.xibin.wms.dao")
@ComponentScan(basePackages = {"com.xibin"})
@SpringBootApplication
// 启动定时任务注解
@EnableScheduling
public class XibinWmsApplication extends SpringBootServletInitializer {

//	public static void main(String[] args) {
//		SpringApplication.run(XibinWmsApplication.class, args);
//	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// 注意这里要指向原先用main方法执行的Application启动类
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		return builder.sources(XibinWmsApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(XibinWmsApplication.class, args);
	}
}
