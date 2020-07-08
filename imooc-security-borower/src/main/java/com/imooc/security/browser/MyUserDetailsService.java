package com.imooc.security.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

// 实现接口UserDetailsService，加注解@Component，交由security管理
@Component
public class MyUserDetailsService implements UserDetailsService {

    private Logger logger =  LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 参数userName：用户输入的用户名
     * 返回值UserDetails：是一个接口，可以使用security提供的user对象，该对象已实现UserDetails接口。
     *      我们也可以自己实现这个接口。
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        logger.info("登陆用户名："+userName);
        /* List<GrantedAuthority>是该用户拥有的权限集合。commaSeparatedStringToAuthorityList方法是
            将一个字符串转换为List<GrantedAuthority>，字符串格式要求权限名称集合用,隔离。
         */
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("admin,superAdmin");
        /* User是security提供的，已实现UserDetails接口。
           正常的业务逻辑应该：我们根据用户名去数据库查找该用户的信息，能够查找到，
           将用户信息将其封装到UserDetails实现类里。此处我们不演示这个逻辑，写死用户信息。
         */
        // User user = new User(userName, "123456", grantedAuthorities);
        /* User的另一个构造函数：
        public User(String username, String password, boolean enabled, boolean accountNonExpired,
                    boolean credentialsNonExpired, boolean accountNonLocked,
                    Collection<? extends GrantedAuthority> authorities) {}
         */
        /* 为防止密码泄露，我们一般是在插入用户信息，给密码加密，插入数据库的是加密后的密码。
            此处我们不演示读取数据库，直接写死了。
         */
        String encode = passwordEncoder.encode("123456");
        System.out.println("数据库密码是："+encode);
        User user1 = new User(userName, encode, true, true,true,
                true, grantedAuthorities);
        return user1;
    }
}
