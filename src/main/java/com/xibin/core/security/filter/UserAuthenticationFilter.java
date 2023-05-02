package com.xibin.core.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xibin.core.security.pojo.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class UserAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            MyUserDetails user = new ObjectMapper().readValue(req.getInputStream(), MyUserDetails.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
