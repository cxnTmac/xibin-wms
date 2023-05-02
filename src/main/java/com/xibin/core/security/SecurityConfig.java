package com.xibin.core.security;

import com.alibaba.fastjson.JSON;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.filter.UserAuthenticationFilter;
import com.xibin.core.security.util.SecurityUtil;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootConfiguration
public class SecurityConfig  extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserLoginAuthenticationProvider loginAuthenticationProvider;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    /**
     * 密码加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 请求拦截、映射
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new UnauthorizedEntryPoint()).and()
                .csrf().disable().authorizeRequests(auth->{
            //开放swagger、登陆页面的访问权限
            auth.antMatchers("/swagger-ui.html").permitAll()
                    .antMatchers("/swagger-resources/**").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    .antMatchers("/v2/**").permitAll()
                    .antMatchers("/api/**").permitAll()
//                    .antMatchers("/user/login","/logout").permitAll()// 登陆/登出开放
                    .antMatchers("/wx/*").permitAll()// 微信和网站接口开放
                    .anyRequest().authenticated();
        });
        http.logout().logoutUrl("/logout").deleteCookies("JSESSIONID").logoutSuccessHandler(myLogoutSuccessHandler);  ;
        //启用自定义的过滤器
        http.addFilterAt(userAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.cors();//启用跨域
        http.csrf().disable();//关闭跨站攻击
    }

    /**
     * 用户认证
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //使用自定义的Provider，进行数据校验
        auth.authenticationProvider(loginAuthenticationProvider);
    }
    /**
     * 解决无法直接注入 AuthenticationManager
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception
    {
        return super.authenticationManager();
    }

    //定义异常返回信息
    public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpStatus.SC_UNAUTHORIZED,authException.getMessage());
        }
    }
    /**
     * 自定义成功回调、失败回调、登陆url地址等
     *
     * 可以在自定义UserAuthenticationFilter里面直接重写对应方法，
     * 例 成功回调：
     *      @Override
     *     public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
     *         super.setAuthenticationSuccessHandler(successHandler);
     *     }
     * @return
     * @throws Exception
     */
    @Bean
    public UserAuthenticationFilter userAuthenticationFilter() throws Exception {
        UserAuthenticationFilter filter = new UserAuthenticationFilter();
        //设置验证成功后的回调
        filter.setAuthenticationSuccessHandler((request,response,authentication)->{
            System.out.println("用户认证成功");
            //响应成功状态码必须为200
            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            //将数据以json的形式返回给前台
            Message msg = new Message();
            msg.setCode(200);
            msg.setMsg("登陆成功");
            msg.setData(SecurityUtil.getMyUserDetails());
            response.getWriter().print(JSON.toJSONString(msg));
        });
        //设置验证失败后的回调
        filter.setAuthenticationFailureHandler((request,  response,  exception) ->{
            System.out.println("用户认证失败----{}"+exception.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            //将数据以json的形式返回给前台
            response.getWriter().print(JSON.toJSONString(exception.getMessage()));
        });
        //设置用户发起登陆请求时的url
        filter.setFilterProcessesUrl("/user/login");
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }
}
