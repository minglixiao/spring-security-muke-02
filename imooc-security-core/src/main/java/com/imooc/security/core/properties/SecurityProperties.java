package com.imooc.security.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

// 读取配置文件中所有以imooc.security开头的配置项
@ConfigurationProperties(prefix = "imooc.security")
@Data
public class SecurityProperties {

	// 读取配置文件中所有以imooc.security.browser开头的配置项
	private BrowserProperties browser = new BrowserProperties();

	/** 这里有个奇怪的问题。spring可以读取配置文件内容到成员变量browser，却读不进code。我弄了一下午也没解决。
	 *  第二天早上，这个问题就不存在了。我也不清楚为什么。
	 */
	// 验证码配置
	private ValidateCodeProperties code = new ValidateCodeProperties();

}

