package com.xibin.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserLoginAuthenticationProvider extends DaoAuthenticationProvider{
        @Autowired
        private UserDetailsServiceImpl detailsService;
        @Autowired
        private PasswordEncoder encoder;


    /**
         * 找到容器中的detailsService，并执行setUserDetailsService方法，完成赋值
         *
         * 必须要给UserDetailsService赋值，否则会出现UnsatisfiedDependencyException
         */
        @Autowired
        private void setDetailsService() {
            setUserDetailsService(detailsService);
        }

        @Override
        protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
            String presentedPassword = authentication.getCredentials().toString();
//            if (!encoder.matches(presentedPassword, userDetails.getPassword())) {
//                throw new BadCredentialsException(messages.getMessage("badCredentials", "用户密码错误"));
//            }
            if(!presentedPassword.equals(userDetails.getPassword())){
                throw new BadCredentialsException(messages.getMessage("badCredentials", "用户密码错误"));
            }
        }
}
