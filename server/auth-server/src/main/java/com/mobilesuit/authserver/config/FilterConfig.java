package com.mobilesuit.authserver.config;

import com.mobilesuit.authserver.auth.filter.OauthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<OauthFilter> customFilter() {
        FilterRegistrationBean<OauthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OauthFilter());
        registrationBean.addUrlPatterns("/login/github");
        registrationBean.addUrlPatterns("/oauth2/authorization/github");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
