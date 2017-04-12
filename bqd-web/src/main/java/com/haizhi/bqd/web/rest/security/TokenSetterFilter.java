package com.haizhi.bqd.web.rest.security;

import com.google.common.base.Strings;
import com.haizhi.bqd.common.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.haizhi.bqd.common.TokenUtil.AUTH_TOKEN_KEY;

/**
 * Created by chenbo on 17/4/7.
 */
@Slf4j
public class TokenSetterFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = TokenUtil.getToken(request);

        if (!Strings.isNullOrEmpty(token)) {
            // set token to response header
            response.setHeader(AUTH_TOKEN_KEY, token);

            // set token to response cookie

//            response.addCookie(Environment.buildTokenCookie(token));

//            log.debug("write token back to response, token=" + token);

        }

        doFilter(request, response, filterChain);
    }

}
