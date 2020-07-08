package com.imooc.security.core.properties;

import lombok.Data;

/**
 * 图形验证码----基本参数配置
 */
@Data
public class ImageCodeProperties {

	// 默认配置。其他地方没有给这些属性赋值时，这里赋予属性默认值。
	private int width = 67;

	private int height = 23;

	private int length = 4;

	private int expireIn = 60;

	private String url;
}
