package com.imooc.security.core.properties;


import lombok.Data;

@Data
public class BrowserProperties {

	// 默认/imooc-signIn.html
	private String loginPage = "/imooc-signIn.html";

	// 默认JSON
	private LoginResponseType loginType = LoginResponseType.JSON;

	private int rememberMeSeconds = 60*60 ;

}
