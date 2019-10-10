package com.lhj.sso.controller;

import com.lhj.sso.model.User;
import com.lhj.sso.service.LoginService;
import com.lhj.sso.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisService redisService;

    @RequestMapping("/turnLoginPage")
    public String turnLoginPage(HttpServletRequest request, ModelMap modelMap){
        String currentPage = request.getParameter("currentPage");
        if(null != currentPage && !"".equals(currentPage)) {
            modelMap.addAttribute("currentPage", currentPage);
            return "login";
        }
        return "404";
    }

    /**
     *      执行登录操作
     **/
    @RequestMapping("/doLogin")
    @ResponseBody
    public String doLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        return loginService.doLogin(user, redisService, request, response);
    }

    /**
     *      检测用户是否处于登录状态
     *          callback：是检测该方法是否为跨域方法的唯一标识
     *              接收的参数只能叫callback(严格区分大小写)
     **/
    @RequestMapping("/token/{cookieValue}")
    @ResponseBody
    public Object checkLogin(@PathVariable("cookieValue") String cookieValue, String callback) {
        // cookieValue：redis的key
        // 方法的目的是什么？检测是否处于登录状态
        // 如何检测？通过redis的key从redis缓存库中查询是否有数据
        String userString = loginService.checkLogin(redisService, cookieValue);
        // 判断userString是否为null，如果为null则说明用户未登录，如果不为null说明用户已经处于登录状态
        if(null != userString && !"".equals(userString)) {
            // 说明用户已经处于登录状态
            if(null != callback && !"".equals(callback)) {
                // 说明该方法就是跨域方法
                MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userString);
                mappingJacksonValue.setJsonpFunction(callback);
                return mappingJacksonValue;
            } else {
                // 说明该方法为常规方法
                return userString;
            }

        }
        return null;
    }


    @RequestMapping("/add")
    public String add(){
        return redisService.set("mh","mh");
    }
}
