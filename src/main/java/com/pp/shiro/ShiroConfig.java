package com.pp.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pp.entity.Permission;
import com.pp.service.PermissionService;
import com.pp.util.JsonUtils;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ShiroConfig {
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private String port;

	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	@Bean("redisManager")
	public RedisManager redisManager() {
		RedisManager redisManager = new RedisManager();
		//System.out.println("redisManager.setHost(" + host + ":" + port + ")");
		redisManager.setHost(host + ":" + port);
		return redisManager;
	}

	@Bean("redisCacheManager")
	public RedisCacheManager cacheManager(RedisManager redisManager) {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager);
		// 必须要设置主键名称，shiro-redis 插件用过这个缓存用户信息
		redisCacheManager.setPrincipalIdFieldName("account");
		redisCacheManager.setExpire(1800); //单位秒
		return redisCacheManager;
	}

	@Bean("sessionDAO")
	public RedisSessionDAO redisSessionDAO(RedisManager redisManager) {
		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		redisSessionDAO.setRedisManager(redisManager);
		return redisSessionDAO;
	}

	@Bean("sessionManager")
	public DefaultWebSessionManager sessionManager(RedisSessionDAO sessionDAO) {
		DefaultWebSessionManager sessionManager = new SessionConfig();
		sessionManager.setSessionDAO(sessionDAO);
		sessionManager.setGlobalSessionTimeout(1800000);
		return sessionManager;
	}

	@Bean("hashedCredentialsMatcher")
	public HashedCredentialsMatcher hashedCredentialsMatcher(RedisCacheManager redisCacheManager) {
		HashedCredentialsMatcher hashedCredentialsMatcher = new RetryLimitCredentialsMatcher(redisCacheManager);
		hashedCredentialsMatcher.setHashAlgorithmName(ShiroUtils.ALGORITHMNAME);
		// 散列的次数
		hashedCredentialsMatcher.setHashIterations(ShiroUtils.ITERATIONS);
		return hashedCredentialsMatcher;
	}

	@Bean("simpleCookie")
	public SimpleCookie simpleCookie() {
		// 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
		// 记住我cookie生效时间30天 ,单位秒
		simpleCookie.setMaxAge(259200);
		return simpleCookie;
	}

	@Bean("rememberMeManager")
	public CookieRememberMeManager cookieRememberMeManager(SimpleCookie simpleCookie) {

		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(simpleCookie);
		// rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
		cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
		return cookieRememberMeManager;
	}

	@Bean("shiroRealm")
	public ShiroRealm shiroRealm(HashedCredentialsMatcher matcher, RedisCacheManager redisCacheManager) {
		ShiroRealm shiroRealm = new ShiroRealm();
		shiroRealm.setCredentialsMatcher(matcher);
		shiroRealm.setCacheManager(redisCacheManager);
		shiroRealm.setCachingEnabled(true);// 启用身份验证缓存，默认false
		
		//启用认证缓存
		shiroRealm.setAuthenticationCachingEnabled(true); 
		//缓存AuthenticationInfo信息的缓存名称
		shiroRealm.setAuthenticationCacheName("authenticationCache"); 
		//启用授权缓存，默认false 
		shiroRealm.setAuthorizationCachingEnabled(true); 
		//缓存AuthorizationInfo信息的缓存名称
		shiroRealm.setAuthorizationCacheName("authorizationCache");

		return shiroRealm;
	}

	@Bean("securityManager")
	public SecurityManager securityManager(ShiroRealm shiroRealm, RedisCacheManager redisCacheManager,
			DefaultWebSessionManager sessionManager, CookieRememberMeManager rememberMeManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(shiroRealm);
		securityManager.setCacheManager(redisCacheManager);
		securityManager.setSessionManager(sessionManager);
		securityManager.setRememberMeManager(rememberMeManager);
		return securityManager;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, PermissionService permService) {

		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		// 设置SecurityManager
		shiroFilter.setSecurityManager(securityManager);

		// 设置拦截器
		Map<String, String> filterMap = new LinkedHashMap<>();

		List<Permission> perms = permService.selectAll(null);
		for (Permission perm : perms) {
			if (JsonUtils.isNotEmpty(perm.getUrlMapping())) {
				String[] urls = perm.getUrlMapping().split(";");
				for (String url : urls) {
					filterMap.put(url, "authc,perms[" + perm.getPermname() + "]");
				}
			}
		}
		// 配置不会被拦截的链接 顺序判断
		filterMap.put("/common/**", "anon"); // 可以匿名访问，公共静态资源
		filterMap.put("/", "anon");
		filterMap.put("/index", "anon");
		filterMap.put("/403", "anon");
		filterMap.put("/user/login", "anon");
		filterMap.put("/user/signup", "anon");
		// filterMap.put("/user/logout", "logout");
		// 这句话的意思是除了上面配置的路径，剩下的全部路径都需要认证通过才能访问，所以这句话要放在最后
		filterMap.put("/**", "authc");
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilter.setLoginUrl("/user/login");
		// 登录成功后要跳转的链接
		shiroFilter.setSuccessUrl("/index");
		// 未授权界面
		shiroFilter.setUnauthorizedUrl("/403");
		shiroFilter.setFilterChainDefinitionMap(filterMap);
		return shiroFilter;
	}

	/**
	 * 配置SecurityManager的生命周期处理器
	 */
	@Bean("lifecycleBeanPostProcessor")
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
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
