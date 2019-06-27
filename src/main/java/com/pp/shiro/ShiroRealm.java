package com.pp.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import com.pp.entity.Permission;
import com.pp.entity.Role;
import com.pp.entity.User;
import com.pp.service.PermissionService;
import com.pp.service.RoleService;
import com.pp.service.UserService;

public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PermissionService permissionService;

	/**
	 * 授权(验证权限时候调用)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 1. 从 PrincipalCollection 中来获取登录用户的信息
		String account = (String) principals.getPrimaryPrincipal();
		User user = userService.selectByAccount(account);
		// 2.添加角色和权限
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		for (Role role : roleService.getRoles(user.getId())) {
			// 2.1添加角色
			simpleAuthorizationInfo.addRole(role.getRolename());
			for (Permission permission : permissionService.getPermissions(role.getId())) {
				// 2.1.1添加权限
				simpleAuthorizationInfo.addStringPermission(permission.getUrlMapping());
			}
		}
		return simpleAuthorizationInfo;
	}

	/**
	 * 认证(登陆时候调用)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 获得登录账号
		String account = (String) token.getPrincipal();
		// 查询用户
		User user = userService.selectByAccount(account);
		// 如果用户为空抛出用户不存在异常
		if (user == null) {
			throw new UnknownAccountException("用户名不存在");
		}
		// 如果账号锁定
		if (user.getStatus() == 0) {
			throw new LockedAccountException("账号已被锁定,请联系管理员");
		}
		// 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo		
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(),
				ShiroUtils.getByteSource(user.getAccount()), this.getName());

		return info;
	}
}
