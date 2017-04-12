package com.haizhi.bqd.web.rest.security;

import com.google.common.base.Strings;
import com.google.common.primitives.Longs;
import com.haizhi.bqd.common.TokenUtil;
import com.haizhi.bqd.service.service.UserService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static com.haizhi.bqd.common.TokenUtil.AUTH_TOKEN_KEY;

/**
 * Created by chenbo on 17/4/7.
 */
@Slf4j
public class RestLogoutSuccessHandler implements LogoutSuccessHandler {

    @Setter
    UserService userService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        cleanSession(request, authentication);

        // set token to response header
        response.setHeader(AUTH_TOKEN_KEY, "");
        // set token to response cookie
        Cookie aCookie = new Cookie(AUTH_TOKEN_KEY, "");
        aCookie.setMaxAge(-1);
        aCookie.setPath("/");
        response.addCookie(aCookie);

        PrintWriter writer = response.getWriter();
        writer.write("{\"status\":0, \"msg\":\"Logout Success\"}");
        writer.flush();
        writer.close();

    }

    private void cleanSession(HttpServletRequest request, Authentication authentication) {
        try {
            if (authentication != null && authentication.getPrincipal() != null
                    && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                userService.clearSession(userDetails.getId());
            } else {
                String token = TokenUtil.getToken(request);
                if (!Strings.isNullOrEmpty(token)) {
                    Map<String, Object> tokenDataMap = TokenUtil.decodeToken(token);
                    Long userId = Longs.tryParse(tokenDataMap.get("userId").toString());
                    userService.clearSession(userId);
                }
            }
        } catch (Exception e) {
            log.error("清空用户session发生异常", e);
        }
    }

}

