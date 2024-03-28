package com.xibin.core.security.filter;

import com.alibaba.fastjson.JSON;
import com.xibin.core.pojo.AccessToken;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.WeixinAuthenticationToken;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.utils.WXUtil;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class WeixinAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private String usernameParameter = "code";
    private String passwordParameter = "state";
    private boolean postOnly = false;

    public WeixinAuthenticationFilter() {
        super(new AntPathRequestMatcher("/wx/getAccessTokenForLogin", "POST"));
    }

    /**
     * 重写 该方法，判断是否包含 code ,没有：重定向到微信服务器，获取code ,有：则判断 state 值 ， 接着获取openId ,对比数据库不存在就更新。
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (this.postOnly && !request.getMethod().equals("POST") && !request.getMethod().equals("GET")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            Message message = new Message();
            // code
            String username = this.obtainUsername(request);
            // state
            String password = this.obtainPassword(request);
            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }
            username = username.trim();
            System.out.println("==========================>openId:" + username);
            if (!StringUtils.hasText(username)) {
                /**重定向*/
                redirectWeixin(request, response);
                return null;
            }
            /*** 此处省略 判断state 值是否合法。*/
            AccessToken token = WXUtil.getAccessToken(username);
            if (token.getOpenid() == null) {
                response.setContentType("application/json;charset=UTF-8");
                message.setMsg("获取微信账号失败，请重新从公众号进入或点击微信登陆按钮");
                message.setCode(0);
                response.getWriter().write(JSON.toJSONString(message));
                return null;
            }
            // userService.loadUserByOpenId("omEFw1tUIEdoLPjs7ZxOcggNAZek");
            MyUserDetails myUserDetails = new MyUserDetails();
            myUserDetails.setOpenId(token.getOpenid());
            WeixinAuthenticationToken authRequest = new WeixinAuthenticationToken(myUserDetails, password);
            this.setDetails(request, authRequest);
            Authentication authenticate = this.getAuthenticationManager().authenticate(authRequest);
            return authenticate;
        }
    }

    private void redirectWeixin(HttpServletRequest request, HttpServletResponse response) {
        //String state = UUID.randomUUID().toString();//将此值存起来，回调的时候进行判断。
        //String type = request.getParameter("type");
        //String returnUrl = properties.getAuthUrl() + "?type=" + type;
        //String redirectURL = wxMpService.oauth2buildAuthorizationUrl(
//                returnUrl,
//                type.equals("1") ? WxConsts.OAuth2Scope.SNSAPI_BASE : WxConsts.OAuth2Scope.SNSAPI_USERINFO,
//                state);
        String redirectURL = "https://www.jjxbjg.com";
        try {
            response.sendRedirect("");
            System.out.println("==========================>LoginFailed and redirect to:" + redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("==========================>Redirect error:" + redirectURL);
        }
    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    protected void setDetails(HttpServletRequest request, WeixinAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.usernameParameter;
    }

    public final String getPasswordParameter() {
        return this.passwordParameter;
    }
}

