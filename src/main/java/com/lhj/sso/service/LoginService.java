package com.lhj.sso.service;

import com.lhj.sso.mapper.UserMapper;
import com.lhj.sso.model.User;
import com.lhj.sso.utils.CookieUtil;
import com.lhj.sso.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.lhj.sso.utils.UUIDUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginService {

    @Value("${cookie_key}")
    private String cookieName;

    @Autowired
    private UserMapper userMapper;

    public String doLogin(User user,RedisService redisService, HttpServletRequest request, HttpServletResponse response){
        //获取从数据库中查询结果
        User u = userMapper.selectUserByUsernameAndPassword(user);
        if (null != u ){
            //说明数据中有该用户，登录成功
            // 常规项目的情况下，需要把user存入到session中
            // 单点登录项目需要把User对象存入到redis中
            //把密码清空，以防被盗取
            u.setPassword(null);
            //redis的key为u.getId + UUID
            String rediskey = u.getId() + UUIDUtil.getUUID();
            String setResult = redisService.set(rediskey, JSONUtil.toJsonString(u));
            //判断是否添加成功
            //setResult.toUpperCase()将结果转化为大写
            if ("OK".equals(setResult.toUpperCase())){
                //redis存入成功，把Redis存入cookie中
                CookieUtil.setCookie(request, response, cookieName, rediskey,20);
                return "success";
            }
        }
        return null;
    }

    //获取到Redis中的value中，Redis中的key是cookie中的value
    public String checkLogin(RedisService redisService, String cookieValue){
       return redisService.get(cookieValue);
    }

}
