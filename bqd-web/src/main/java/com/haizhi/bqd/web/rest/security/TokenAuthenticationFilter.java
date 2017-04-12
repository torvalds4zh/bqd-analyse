package com.haizhi.bqd.web.rest.security;

import com.haizhi.bqd.common.DeviceUtil;
import com.haizhi.bqd.common.JsonUtil;
import com.haizhi.bqd.common.TokenUtil;
import com.haizhi.bqd.service.model.Session;
import com.haizhi.bqd.service.service.UserService;
import com.haizhi.bqd.web.rest.util.ConfUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenbo on 17/4/6.
 */
@Slf4j
@Controller
public class TokenAuthenticationFilter extends GenericFilterBean {

    @Setter
    private UserService userService;

    private static List<String> ignorePaths = ConfUtil.getConfList("ignore_path");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String token = TokenUtil.getToken(req);
        int channel = DeviceUtil.getChannel(req);
        //log.info("channel: {}, token: {}", channel, token);

        if (!(req.getPathInfo() == null || ignorePaths.contains(req.getPathInfo()))) {
            //登录和登出不用判断

            if (StringUtils.isEmpty(token) || "null".equalsIgnoreCase(token)) {//token是null,应当重新登录
                Map<String, Object> obj = new HashMap<>();
                obj.put("status", 0);
                obj.put("msg", "请登陆");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json;UTF-8");
                PrintWriter writer = resp.getWriter();
                writer.write(JsonUtil.formatJSON(obj));
                writer.flush();
                return;
            } else {
                try {

                    //除了login和logout外,其他接口都需要验证token
                    Map<String, Object> param = TokenUtil.decodeToken(token);

                    if (param.get("userId") == null) {//没有包含用户id, 需要重新登录
                        log.info("Invalid token need to re-login, not get userId from token");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }

                    Long userId;
                    if (param.get("userId") instanceof Long) {
                        userId = (Long) param.get("userId");
                    } else {
                        userId = Long.parseLong(param.get("userId").toString());
                    }

                    Session session = userService.loadSession(userId);

                    if (session == null) {//用户获取失败,需要重新登录
                        log.info("No Session found, need to re-login, channel:{}, userId:{}", channel, userId);

                        Map<String, Object> obj = new HashMap<>();
                        obj.put("status", 1);
                        obj.put("msg", "您长时间未操作或账户信息已修改，请重新登陆");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.setContentType("application/json;UTF-8");
                        PrintWriter writer = resp.getWriter();
                        writer.write(JsonUtil.formatJSON(obj));
                        writer.flush();

                        return;
                    }


                    if (param.get("password") == null || (!session.getPassword().equals(param.get("password")))) {//用户获取失败,需要重新登录
                        log.info("password has change, need to re-login, channel:{}, userId:{}", channel, userId);

                        Map<String, Object> obj = new HashMap<>();
                        obj.put("status", 1);
                        obj.put("msg", "您的密码已被修改，请重新登陆");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.setContentType("application/json;UTF-8");
                        PrintWriter writer = resp.getWriter();
                        writer.write(JsonUtil.formatJSON(obj));
                        writer.flush();

                        return;
                    }


                    if (channel == 1) { //手机渠道请求

                        String deviceId = DeviceUtil.getDeviceId(req);

                        if (!session.getPermission().contains("ROLE_SUPER") && (deviceId == null || !deviceId.equals(session.getMobileDeviceId()))) { //deviceId为空或者和登陆时session存储的deviceId不同时，重新登陆
                            log.info("Current device is different from login device, need to re-login");
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("status", 1);
                            obj.put("msg", "您的帐号已在其他手机登录，请注意帐号安全");

                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resp.setContentType("application/json;UTF-8");
                            PrintWriter writer = resp.getWriter();
                            writer.write(JsonUtil.formatJSON(obj));
                            writer.flush();
                            return;
                        }

                    } else { //web渠道

                        Long tokenTime = (Long) param.get("createTime");

                        if (tokenTime == null) {
                            log.info("Invalid token, need to re-login, not get createTime form token, channel:{}, userId:{}", channel, userId);
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("status", 1);
                            obj.put("msg", "无效的安全Token，请重新登陆");
                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resp.setContentType("application/json;UTF-8");
                            PrintWriter writer = resp.getWriter();
                            writer.write(JsonUtil.formatJSON(obj));
                            writer.flush();
                            return;
                        }

                        if (!session.getPermission().contains("ROLE_SUPER") && session.getWebLastLoginTime() != null
                                && session.getWebLastLoginTime() > tokenTime) {//如果判断token的生成时间与用户关键信息修改时间不符,剔除用户
                            log.info("Invalid token, need to re-login, another port has login, channel:{}, userId:{}", channel, userId);
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("status", 1);
                            obj.put("msg", "您的帐号已在其他电脑登录，请注意帐号安全");
                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resp.setContentType("application/json;UTF-8");
                            PrintWriter writer = resp.getWriter();
                            writer.write(JsonUtil.formatJSON(obj));
                            writer.flush();
                            return;
                        }

                    }

                    req.setAttribute("UserSession", session);

                    userService.saveSession(userId, session);

                } catch (Exception e) {//需要重新登录
                    log.warn("User token validation error, channel:{}, token:{}", channel, token, e);
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("status", 1);
                    obj.put("msg", "无效的安全Token，请重新登陆");
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.setContentType("application/json;UTF-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(JsonUtil.formatJSON(obj));
                    writer.flush();
                    return;
                }
            }

        }

        chain.doFilter(request, response);

    }

}

