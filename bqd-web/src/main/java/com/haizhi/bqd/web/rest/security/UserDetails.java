package com.haizhi.bqd.web.rest.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by chenbo on 17/4/6.
 */
@Data
public class UserDetails extends User {

    private Long id;
    private String username;
    private boolean admin = false;
    private String password;
    private String salt;
    private String token;
    private String deviceId;
    private Long deadLineTime;
    private String permission;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.username = username;
        this.password = password;
    }

}
