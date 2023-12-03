package com.mobilesuit.authserver.auth.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OauthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        System.out.println(req.getRequestURI());
        System.out.println(req.getSession().getId());
        if(null != req.getQueryString()&&req.getQueryString().contains("device")) {
            String deviceType = req.getQueryString().replace("device=", "");
            req.getSession().setAttribute("device", deviceType);
            System.out.println("Origin From"+ req.getRequestURL());

            Cookie cookie = new Cookie("device",deviceType);
            cookie.setPath("/");
            cookie.setMaxAge(180);
            httpServletResponse.addCookie(cookie);

            System.out.println(deviceType);

        }
        chain.doFilter(request, response);
    }

}
