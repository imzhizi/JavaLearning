package com.imzhizi.javalearning.spring.servlet;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 * created by zhizi
 * on 3/21/20 14:01
 */
@Configuration
public class AppConfig {
    public String test(HttpServletRequest request){
        request.getParameter("");
        return null;
    }
}
