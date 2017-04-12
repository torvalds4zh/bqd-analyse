package com.haizhi.bqd.web.rest.security;

import com.haizhi.bqd.service.model.Session;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenbo on 17/4/6.
 */
public class SessionSecurityContextRepository implements SecurityContextRepository {

    private String sessionKeyName = "UserSession";

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {

        Object obj = requestResponseHolder.getRequest().getAttribute(sessionKeyName);

        if (obj == null) {

            return SecurityContextHolder.createEmptyContext();

        } else {

            Session session = (Session) obj;

            String permission = session.getPermission();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (permission != null && !"".equals(permission)) {
                String[] sp = permission.split(",");
                for (String authority : sp) {
                    authorities.add(new SimpleGrantedAuthority(authority));
                }

            }

            UserDetails userDetails = new UserDetails(session.getUsername(), session.getPassword(), authorities);
            userDetails.setId(session.getUserId());
            userDetails.setSalt(session.getSalt());
            userDetails.setUsername(session.getUsername());
            userDetails.setPermission(session.getPermission());


            if (session.getPermission().contains("ROLE_ADMIN") || session.getPermission().contains("ROLE_SUPER")) {
                userDetails.setAdmin(true);
            }

            UsernamePasswordAuthenticationToken up = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            up.setDetails(userDetails);

            SecurityContext context = SecurityContextHolder.getContext();
            if (context == null) {
                context = SecurityContextHolder.createEmptyContext();
            }
            context.setAuthentication(up);

            return context;
        }

    }

    @Override
    public void saveContext(final SecurityContext context, HttpServletRequest request, HttpServletResponse response) {

    }

    public void saveContext(String token, final SecurityContext context) {

    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        Object obj = request.getAttribute(sessionKeyName);
        if (obj == null) {
            return false;
        } else {
            return true;
        }
    }

}
