package com.xibin.core.security.util;

import com.xibin.core.security.pojo.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    // 获取当前登录用户的信息
    public static MyUserDetails getMyUserDetails(){
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        return (MyUserDetails) auth.getPrincipal();
    }
}
