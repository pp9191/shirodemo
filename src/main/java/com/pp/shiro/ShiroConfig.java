package com.pp.shiro;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean("securityManager")
    public org.apache.shiro.mgt.SecurityManager securityManager(ShiroRealm authRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //注册自己的Realm
        securityManager.setRealm(authRealm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    /**
     *   配置ShiroFilter
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(org.apache.shiro.mgt.SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        //设置SecurityManager
        shiroFilter.setSecurityManager(securityManager);
        //设置拦截器
        Map<String, String> filterMap = new LinkedHashMap<>();
        // 配置不会被拦截的链接 顺序判断
        filterMap.put("/static/**", "anon"); //可以匿名访问
        filterMap.put("/", "anon"); 
        filterMap.put("/index", "anon");
        filterMap.put("/unauthorized", "anon");
        filterMap.put("/userinfo/signup", "anon"); 
        filterMap.put("/userinfo/logout", "logout");      
        //这句话的意思是除了上面配置的路径，剩下的全部路径都需要认证通过才能访问，所以这句话要放在最后
        filterMap.put("/**", "authc");        
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilter.setLoginUrl("/userinfo/login");
        // 登录成功后要跳转的链接
        // shiroFilter.setSuccessUrl("/index");
        //未授权界面
        shiroFilter.setUnauthorizedUrl("/403");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    /**
     * 配置SecurityManager的生命周期处理器
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
      *  开启shiro注解功能
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(org.apache.shiro.mgt.SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
