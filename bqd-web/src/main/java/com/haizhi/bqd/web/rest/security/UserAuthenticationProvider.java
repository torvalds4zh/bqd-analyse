package com.haizhi.bqd.web.rest.security;

import com.google.common.base.Strings;
import com.haizhi.bqd.service.model.User;
import com.haizhi.bqd.service.repo.UserRepo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenbo on 17/4/7.
 */
@Slf4j
public class UserAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Setter
    private PasswordEncoder passwordEncoder;

    @Setter
    private UserRepo userRepo;

    @Override
    protected void additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails bqdUserDetails = (UserDetails) userDetails;
        authenticate(bqdUserDetails, authentication, passwordEncoder);
    }

    private void authenticate(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication, PasswordEncoder passwordEncoder) {

        Object salt = userDetails.getSalt();

        if (authentication.getCredentials() == null) {
            log.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.isPasswordValid(userDetails.getPassword(),
                presentedPassword, salt)) {
            log.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        userDetails.setPassword(presentedPassword);

    }

    @Override
    protected UserDetails retrieveUser(String username,
                                       UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        if (Strings.isNullOrEmpty(username)) {
            throw new UsernameNotFoundException("登录用户名不能为空");
        }

        User user = userRepo.findByUsername(username);
        if (user == null) {
            //TODO 接入其他用户系统
            throw new UsernameNotFoundException("未找到指定用户 用户名:" + username);
        }
        UserDetails userDetails = new UserDetails(username, user.getPassword(), getAuthorities(user.getAuthorities()));
        userDetails.setId(user.getId());
        userDetails.setSalt(user.getSalt());
        userDetails.setUsername(user.getUsername());
        userDetails.setPermission(user.getPermission());
        /*if (user.getTokenDeadTime() != null) {
            userDetails.setDeadLineTime(user.getTokenDeadTime());
        }*/


        List<String> list = user.getAuthorities();
        for (String auth : list) {
            if (auth.equals("ROLE_ADMIN") || auth.equals("ROLE_SUPER")) {
                userDetails.setAdmin(true);
                break;
            }
        }

        return userDetails;
    }

    private List<SimpleGrantedAuthority> getAuthorities(List<String> list) {
        List<SimpleGrantedAuthority> a = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            a.add(new SimpleGrantedAuthority(list.get(i)));
        }
        return a;
    }

}
