package com.pp.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import net.sf.ehcache.CacheManager;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	@Bean("ehCacheManager")
	public EhCacheManager ehCacheManager(CacheManager cacheManager) {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManager(cacheManager);
		return em;
	}
	
	@Bean("hashedCredentialsMatcher")
	public HashedCredentialsMatcher hashedCredentialsMatcher(EhCacheManager ehCacheManager) {
		HashedCredentialsMatcher hashedCredentialsMatcher = new RetryLimitCredentialsMatcher(ehCacheManager);
		hashedCredentialsMatcher.setHashAlgorithmName(ShiroUtils.ALGORITHMNAME);
		// 散列的次数
		hashedCredentialsMatcher.setHashIterations(ShiroUtils.ITERATIONS);
		return hashedCredentialsMatcher;
	}
	
	@Bean("simpleCookie")
	public SimpleCookie simpleCookie(){
	      
	      // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
	      SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
	      // 记住我cookie生效时间30天 ,单位秒
	      simpleCookie.setMaxAge(259200);
	      return simpleCookie;
	}

	@Bean("rememberMeManager")
	public CookieRememberMeManager cookieRememberMeManager(SimpleCookie simpleCookie){
	      
	      CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
	      cookieRememberMeManager.setCookie(simpleCookie);
	      //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
	      cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
	      return cookieRememberMeManager;
	}

	@Bean("shiroRealm")
	public ShiroRealm shiroRealm(HashedCredentialsMatcher matcher) {
		ShiroRealm shiroRealm = new ShiroRealm();
		shiroRealm.setAuthorizationCachingEnabled(false);
		shiroRealm.setCredentialsMatcher(matcher);
		return shiroRealm;
	}

	@Bean("securityManager")
	public SecurityManager securityManager(ShiroRealm shiroRealm, EhCacheManager ehCacheManager, 
			CookieRememberMeManager rememberMeManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 注册自己的Realm
		securityManager.setRealm(shiroRealm);
		securityManager.setCacheManager(ehCacheManager);
		securityManager.setRememberMeManager(rememberMeManager);
		return securityManager;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {

		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		// 设置SecurityManager
		shiroFilter.setSecurityManager(securityManager);
		// 设置拦截器
		Map<String, String> filterMap = new LinkedHashMap<>();
		// 配置不会被拦截的链接 顺序判断
		filterMap.put("/common/**", "anon"); // 可以匿名访问，公共静态资源
		filterMap.put("/", "anon");
		filterMap.put("/index", "anon");
		filterMap.put("/unauthorized", "anon");
		filterMap.put("/userinfo/signup", "anon");
		filterMap.put("/userinfo/logout", "logout");
		// 这句话的意思是除了上面配置的路径，剩下的全部路径都需要认证通过才能访问，所以这句话要放在最后
		filterMap.put("/**", "authc");
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilter.setLoginUrl("/userinfo/login");
		// 登录成功后要跳转的链接
		// shiroFilter.setSuccessUrl("/index");
		// 未授权界面
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
	 * 开启shiro注解功能
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

}
