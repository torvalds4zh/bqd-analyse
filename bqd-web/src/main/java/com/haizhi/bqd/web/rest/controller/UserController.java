package com.haizhi.bqd.web.rest.controller;

import com.google.common.base.Strings;
import com.haizhi.bqd.common.Wrapper;
import com.haizhi.bqd.service.model.User;
import com.haizhi.bqd.service.service.UserService;
import com.haizhi.bqd.web.exception.AccountException;
import com.haizhi.bqd.web.rest.util.SecurityUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chenbo on 17/4/7.
 */
@Controller
@RequestMapping("/")
public class UserController {

    @Setter
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    @ResponseBody
    public Wrapper register(@RequestBody User user) {
        if (user == null) {
            return AccountException.MISS_REQUEST_BODY.get();
        }

        if (Strings.isNullOrEmpty(user.getUsername())) {
            return AccountException.MISS_USERNAME.get();
        }

        if (Strings.isNullOrEmpty(user.getPassword())) {
            return AccountException.MISS_PASSWORD.get();
        }

        if (user.getRole() == null) {
            return AccountException.MISS_ROLE.get();
        }

        User dbUser = userService.findByUsername(user.getUsername());
        if (dbUser != null && dbUser.getId() != null) {
            return AccountException.USERNAME_EXISTS.get();
        }

        userService.createUser(user);
        return Wrapper.OK;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @ResponseBody
    public Wrapper profile(){
        User user = userService.getUserById(SecurityUtil.getCurrentUser().getId());
        user.mask();
        return Wrapper.OKBuilder.data(user).build();
    }

    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    @ResponseBody
    public Wrapper modify(@RequestBody User user) {
        if (user == null) {
            return AccountException.MISS_REQUEST_BODY.get();
        }

        if (Strings.isNullOrEmpty(user.getMobile())) {
            user.setMobile("");
        }

        if (user.getRole() == null) {
            return AccountException.MISS_ROLE.get();
        }

        if (user.getId() == null) {
            return AccountException.MISS_ID.get();
        }

        userService.updateUser(user);
        return Wrapper.OK;
    }
}
