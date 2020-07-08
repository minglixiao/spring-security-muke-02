package com.imooc.web.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonView;
import com.imooc.dto.User;
import com.imooc.exception.UserNotExistException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
/**
 * 在类上面加上@RequestMapping的作用： 本类所有接口的URL都会自动加上这个前缀。
 * 我个人不喜欢这种，因为你把URL拆开后，当前端问我一个接口时，我搜索不到URL对应在代码的哪个位置。
 * 如果你说URL拆分的很规范，很好找，那你就当我这句话没说。
 */
@RequestMapping("/user")
public class UserController {

    /* */
    @GetMapping("/me/1")
    public Object getCurrentUser1(){
        // 从SecurityContextHolder获取SecurityContext，再获取Authentication
        // 只要是同一个session（会话），即便是多次不同的请求，我们都能通过SecurityContextHolder获取到当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    @GetMapping("/me/2")
    public Object getCurrentUser2(Authentication authentication){
        return authentication;
    }

    @GetMapping("/me/3")
    // 我们通过这种注解的方式，可以直接获取到我们返回的UserDetails对象
    public Object getCurrentUser3(@AuthenticationPrincipal UserDetails user){
        return user;
    }

    /**
     * @GetMapping:  等同 @RequestMapping(value = "", method = RequestMethod.GET)
     * 类似的还有@PostMapping、@PutMapping、@DeleteMapping。
     */
    @GetMapping
    @JsonView(User.UserSimpleView.class)
    public  ArrayList<User> query(@RequestParam(name="nickName", required=false, defaultValue="狗蛋")String userName ,
                                  @PageableDefault(page=1, size=20, sort="username desc") Pageable pageable) {
        System.out.println("------------/user ----get---------------");
        System.out.println(pageable.getPageSize()+"---"+pageable.getPageNumber()+"---"+pageable.getSort());
        ArrayList<User>  arr=new ArrayList<User>();
        User user0 = new User();
        user0.setUserName("zhangsan");
        user0.setPassword("21151");
        User user1 = new User();
        user1.setUserName("lisi");
        user1.setPassword("58441");
        User user2 = new User();
        user2.setUserName("wangwu");
        user2.setPassword("6984156");
        arr.add(user0);
        arr.add(user1);
        arr.add(user2);
        return arr;
    }

    @GetMapping("/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    @ResponseBody
    public  User getInfo(@PathVariable(name = "id", required = false)   Integer idxxx){
        System.out.println("------------/user/{id} ----get---------------");
        System.out.println("进入getInfo服务");
        User user = new User();
        user.setId(idxxx);
        user.setUserName("tom");
        user.setPassword("233");
        return user;
    }

    /**
     * 创建用户。
     */
    @PostMapping
    @ResponseBody
    public  User create(@RequestBody User user){
        user.setId(1);
        return user;
    }

    /**
     * 修改用户
     */
    @PutMapping("/{id:\\d+}")
    public  User update(@Valid  @RequestBody User user, BindingResult errors){
        if(errors.hasErrors()){
            errors.getAllErrors().stream().forEach(error->{
                // error实际上是FieldError这个类。
                FieldError fieldError= (FieldError)error;
                // 可以从fieldError对象中获取校验的字段和校验后的消息
                System.out.println("校验的字段："+fieldError.getField() +"; 校验后的消息："+fieldError.getDefaultMessage());
            });
        }
        System.out.println(user);
        user.setId(1);
        return user;
    }

    /**
     * 删除用户。
     * 注意：按照restful风格，接口没有返回值也可以，根据http状态码就能判断是否执行成功。
     */
    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable Integer id){
        System.out.println(id);
    }

    /**
     * 演示服务异常处理
     */
    @GetMapping("/error")
    public  User createError(@Valid  @RequestBody User user){
        System.out.println(user);
        user.setId(1);
        return user;
    }

    /**
     * 演示服务异常处理。
     */
    @GetMapping("/error/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public  User getInfoError(@PathVariable(name = "id", required = false)   Integer idxxx){
        throw new UserNotExistException("202005081806");
        // 自定义浏览器和非浏览器请求异常时的返回，适用于浏览器和非浏览器请求异常时的情况。
        // throw new RuntimeException("程序运行错误");
    }

    /**
     * 1. 前端写假数据，多个前端
     * 2. 后端写死数据，接口会随时变化
     * 3. wiremock  改变接口的功能  是一个独立的服务器  连wiremock  连  正式服务器
     */

}

