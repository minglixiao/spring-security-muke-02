package com.imooc.security.core.validate.code;

import com.imooc.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidateCodeBeanConfig {
	
	@Autowired
	private SecurityProperties securityProperties;

	/**
	 *  3. 	@Bean：将默认的验证码生成逻辑作为一个Bean，注入到spring，Bean的名称就是方法名称。
	 *  	@ConditionalOnMissingBean： 如果项目其他地方没有imageValidateCodeGenerator这个bean，
	 *  		就采用当前这个imageValidateCodeGenerator。
	 */
	@Bean
	@ConditionalOnMissingBean(name = "imageCodeGenerator")
	public ValidateCodeGenerator imageCodeGenerator() {
		ImageCodeGenerator codeGenerator = new ImageCodeGenerator(); 
		codeGenerator.setSecurityProperties(securityProperties);
		return codeGenerator;
	}
}
