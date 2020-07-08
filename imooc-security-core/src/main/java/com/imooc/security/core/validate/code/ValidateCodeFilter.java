package com.imooc.security.core.validate.code;

import com.imooc.security.core.properties.SecurityProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 继承OncePerRequestFilter： 保证我们的过滤器，每次只被调用一次。
 * 实现InitializingBean，重写其afterPropertiesSet方法：在其他初始化完毕后，执行该方法。
 */
public class ValidateCodeFilter  extends OncePerRequestFilter implements InitializingBean {

    // 登录失败处理
    private  AuthenticationFailureHandler authenticationFailureHandler;

    // 这个对象用于存取session。这个类比较实用。
    private SessionStrategy sessionStrategy =new HttpSessionSessionStrategy();

    private SecurityProperties securityProperties;

    private Set<String> urls = new HashSet<String>();

    // spring提供的工具类。用来匹配带有表达式的URL。如：此处的/user/*
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 初始化urls。重写afterPropertiesSet方法：在其他初始化完毕后，执行该方法。
     * 此处的作用是等待spring将配置文件内注入到securityProperties对象，之后执行本方法
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        // 工具类。将一个字符串，根据分隔符进行分隔，分隔为一个字符串数组。
        String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                securityProperties.getCode().getImage().getUrl(), ",");
        if(null != configUrls){
            for (String configUrl: configUrls) {
                urls.add(configUrl);
            }
        }
        urls.add("/authentication/form");
        System.out.println("应该没成功："+urls);
    };


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean action = false;
        for(String url : urls){
            if(pathMatcher.match(url , request.getRequestURI())){
                action=true;
            }
        }

        // 只处理登录页面表单提交的请求
        if(action){
            try {
                validate(new ServletWebRequest(request));
            }catch (ValidateCodeException e){
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
                System.out.println(e.getMessage());
                return ;
            }
        }
        // 放行
        filterChain.doFilter(request, response);
    }

    /**
     * 校验验证码。这里验证码校验逻辑 值得借鉴。
     */
    public void validate(ServletWebRequest request) throws ServletRequestBindingException {
        // 获取我们在ValidateCodeController存到session里的ImageCode
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request, ValidateCodeController.SESSION_KEY);
        // 获取前端传递过来的参数imageCode（就是用户输入的验证码）
        String  codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "imageCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在");
        }

        // 验证码是否过期
        if (codeInSession.isExpried()) {
            // 验证码过期，我们要移除session中的ImageCode
            sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }

        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }
        // 移除session中的ImageCode
        sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public SessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
