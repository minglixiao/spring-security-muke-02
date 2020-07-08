package com.imooc.security.core.validate.code;

import com.imooc.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 生成图形验证码
 */
@RestController
public class ValidateCodeController {

	public static  final  String SESSION_KEY="SESSION_KEY_IMAGE_CODE";

	// 这个对象用于存取session
	private SessionStrategy sessionStrategy =new HttpSessionSessionStrategy();

	@Autowired
	private SecurityProperties securityProperties;

	/**
	 * 4. 当我们项目中需要引用验证码生成逻辑时，注入接口。
	 */
	@Autowired
	private ValidateCodeGenerator validateCodeGenerator;


	@GetMapping("/code/image")
	public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 1. 生成随机数图片
		ImageCode imageCode = validateCodeGenerator.generate(new ServletWebRequest(request));
		// 2. 将随机数存到session中
		sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY, imageCode);
		// 3. 将生成的图片写到接口响应中
		ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
	}
}
