package com.xibin.core.security;

import com.xibin.core.security.pojo.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class WeixinAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsServiceImpl userService;
    /** 这个类可以接收 自定义异常，目前还没有找到好的办法，处理自己的异常*/
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    public WeixinAuthenticationProvider(UserDetailsServiceImpl userService){
        this.userService = userService;
    }
    public WeixinAuthenticationProvider(){
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        MyUserDetails details =  (MyUserDetails)authentication.getPrincipal();
        String openId = details==null?"NONE_PROVIDED":details.getOpenId();
        UserDetails user = userService.loadUserByOpenId(openId);
        if (null == user) {
            throw new BadCredentialsException(this.messages.getMessage(HttpStatus.UNAUTHORIZED.value()+"","账户不存在"));
        }
        WeixinAuthenticationToken result = new WeixinAuthenticationToken(user,null);
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        boolean assignableFrom = WeixinAuthenticationToken.class.isAssignableFrom(authentication);
        System.out.println(assignableFrom?this.getClass().getName() + "---supports":this.getClass().getName() + "---not-supports");
        return assignableFrom;
    }

    private Set<GrantedAuthority> listUserGrantedAuthorities(Long uid) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        if (StringUtils.isEmpty(uid)) {
            return authorities;
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

}

