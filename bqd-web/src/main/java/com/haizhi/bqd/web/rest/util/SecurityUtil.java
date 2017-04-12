package com.haizhi.bqd.web.rest.util;

import com.haizhi.bqd.web.rest.security.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by chenbo on 17/4/6.
 */
public class SecurityUtil {

    public static UserDetails getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }

}
