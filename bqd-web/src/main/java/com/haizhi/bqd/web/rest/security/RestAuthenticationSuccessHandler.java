package com.haizhi.bqd.web.rest.security;

import com.haizhi.bqd.common.DeviceUtil;
import com.haizhi.bqd.common.JsonUtil;
import com.haizhi.bqd.common.TokenUtil;
import com.haizhi.bqd.service.model.Session;
import com.haizhi.bqd.service.model.User;
import com.haizhi.bqd.service.service.UserService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.lang.JoseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.haizhi.bqd.common.TokenUtil.AUTH_TOKEN_KEY;

/**
 * Created by chenbo on 17/4/7.
 */
@Slf4j
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Setter
    UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        Long validEndTime = enterpriseService.getEnterpriseValidEndTimeFromCache(userDetails.getTenantId());
//        if (checkValidEndTime(httpServletResponse, validEndTime)) {
//            log.info("passport has expired, not can login userId:{}", userDetails.getUserId());
//
//            return;
//        }

        Date loginTime = new Date();
        String token;

        try {
            token = TokenUtil.generateToken(userDetails.getUsername(), userDetails.getPassword(), loginTime, null, String.valueOf(userDetails.getId()));
        } catch (JoseException e) {
            token = null;
            log.error("Failed to generate token for user, username=" + userDetails.getUsername(), e);
        }

        if (token != null) {
            httpServletRequest.getSession().setAttribute(AUTH_TOKEN_KEY, token);
            //set token to response header
            httpServletResponse.setHeader(AUTH_TOKEN_KEY, token);
            //set token to response cookie

//            httpServletResponse.addCookie(Environment.buildTokenCookie(token));
        }

        Session session = userService.loadSession(userDetails.getId());
        if (session == null) {
            session = new Session();
            session.setUserId(userDetails.getId());
        }
        session.setUsername(userDetails.getUsername());
        session.setPassword(userDetails.getPassword());
        session.setPermission(userDetails.getPermission());
        session.setSalt(userDetails.getSalt());
        /*if (userDetails.getDeadLineTime() != null) {
            session.setDeadLineTime(userDetails.getDeadLineTime());
        }*/

        int channel = DeviceUtil.getChannel(httpServletRequest);

        if (channel == 1) {
            String deviceId = DeviceUtil.getDeviceId(httpServletRequest);
            if (deviceId != null) {
                session.setMobileDeviceId(deviceId);
            }
            session.setMobileLastLoginTime(loginTime.getTime());
        } else {
            session.setWebLastLoginTime(loginTime.getTime());
        }

        List<SimpleGrantedAuthority> list = (List<SimpleGrantedAuthority>) authentication.getAuthorities();
        StringBuilder authorities = new StringBuilder();
        for (SimpleGrantedAuthority auth: list) {
            authorities.append(auth.getAuthority());
        }
        int sysRole = User.getRole(authorities.toString());
        session.setRole(sysRole);

        userService.saveSession(session.getUserId(), session);

//        String companyName = "";
//        if (enterprise != null && enterprise.getName() != null) {
//            companyName = enterprise.getName();
//        }

        String name = userDetails.getUsername();
        Long userId = userDetails.getId();

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", 0);
        resp.put("msg", "Login Success");
        resp.put("role", sysRole+"");
        resp.put("name", new String(name.getBytes(), "UTF-8"));
        resp.put("userId", userId);
//        resp.put("validEndTime", validEndTime);

        httpServletResponse.setContentType("application/json;UTF-8");

        PrintWriter writer = httpServletResponse.getWriter();

        writer.write(JsonUtil.formatJSON(resp));

    }

    private boolean checkValidEndTime(HttpServletResponse httpServletResponse,Long validEndTime) throws IOException {
        if(new Date(validEndTime).before(new Date())){
            Map<String, Object> obj = new HashMap<>();
            obj.put("status", 1);
            obj.put("msg", "您的账号已过期，请联系业务人员");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("application/json;UTF-8");
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write(JsonUtil.formatJSON(obj));
            writer.flush();
            return true;
        }

        return false;
    }

}
