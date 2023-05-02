package com.xibin.core.security;

import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SysUser> users = userService.selectByUserName(username);
        if(users.size()==0){
            throw new BadCredentialsException("用户不存在");
        }
        if("N".equals(users.get(0).getIsEnable())){
            throw new BadCredentialsException("用户被禁用");
        }
        MyUserDetails userInfo = new MyUserDetails();
        BeanUtils.copyProperties(users.get(0),userInfo);
        userInfo.setUsername(users.get(0).getUserName());
        userInfo.setUserId(users.get(0).getId());
        // 暂时默认为1
        userInfo.setWarehouseId(1);
        Set authoritiesSet = new HashSet();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN"); // 模拟从数据库中获取用户角色
        authoritiesSet.add(authority);
        userInfo.setAuthorities(authoritiesSet);
        return userInfo;
    }
}
