package com.imooc.security.browser;

import com.imooc.security.browser.authentication.ImoocAuthenctiationFailureHandler;
import com.imooc.security.browser.authentication.ImoocAuthenticationSuccessHandler;
import com.imooc.security.core.properties.SecurityProperties;
import com.imooc.security.core.validate.code.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import javax.sql.DataSource;

// 1. 要继承spring security提供的适配器类
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ImoocAuthenticationSuccessHandler imoocAuthenticationSuccessHandler;

    @Autowired
    private ImoocAuthenctiationFailureHandler imoocAuthenctiationFailureHandler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;


    // 2. 重写configure(HttpSecurity httpSecurity)方法
    @Override
    protected  void configure(HttpSecurity httpSecurity) throws Exception{
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        // 设置我们自定义登录失败处理
        validateCodeFilter.setAuthenticationFailureHandler(imoocAuthenctiationFailureHandler);
        validateCodeFilter.setSecurityProperties(securityProperties);
        validateCodeFilter.afterPropertiesSet();

        httpSecurity
                // 在UsernamePasswordAuthenticationFilter过滤器前面添加一个过滤器
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                // .httpBasic() : 指定httpBasic登陆认证
                // formLogin()： 指定表单登陆认证
                .formLogin()
                    // 处理不同类型的请求  步骤1：自定义的登陆跳转的URL，跳转到一个自定义的controller方法上。
                    .loginPage("/authentication/require")
                    /* 自定义登录页面  步骤3： 指定表单提交的URL。
                        UsernamePasswordAuthenticationFilter拦截器默认只拦截/login post请求。此处设置该拦截器去拦截/authentication/form 请求
                    */
                    .loginProcessingUrl("/authentication/form")
                    // 配置自定义登录成功后的处理
                    .successHandler(imoocAuthenticationSuccessHandler)
                    // 配置自定义登录失败后的处理
                    .failureHandler(imoocAuthenctiationFailureHandler)
                    .and()
                .rememberMe()
                    // 设置TokenRepository
                    .tokenRepository(persistentTokenRepository())
                    // 设置记住我的时间。单位：秒
                    .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                    // 设置我们自定义的UserDetailsService。
                    .userDetailsService(userDetailsService)
                    .and()
                // 指定授权配置，此处意思：任何请求都需要身份认证
                .authorizeRequests()
                // 自定义登录页面  步骤2： 匹配一个正则表达式，匹配成功的URL不需要身份认证
                // 处理不同类型的请求  步骤2：设置自定义的controller方法，不需要身份认证
                .antMatchers("/authentication/require",
                        securityProperties.getBrowser().getLoginPage(),"/code/image").permitAll()
                // 任何请求
                .anyRequest()
                // 都需要身份认证
                .authenticated()
                .and()
                .csrf().disable() ; //关闭CSRF
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        /* BCryptPasswordEncoder是一个实现PasswordEncoder接口的实现类，采用BCrypt加密算法。
           如果你想采用md5加密方法，只需要实现PasswordEncoder接口，自己重写他的两个方法即可。
         */
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置TokenRepository
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        // 注入数据源
        tokenRepository.setDataSource(dataSource);
        /* 设置为true，会创建一张数据表，如果数据库已有该张表，程序会报错。建好表之后，这句代码就是注释掉。
           类：JdbcTokenRepositoryImpl 成员变量：CREATE_TABLE_SQL 就是具体SQL。
         */
		// tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }
}
