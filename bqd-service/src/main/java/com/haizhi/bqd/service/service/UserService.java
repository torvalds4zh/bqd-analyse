package com.haizhi.bqd.service.service;

import com.haizhi.bqd.service.model.Session;
import com.haizhi.bqd.service.model.User;

/**
 * Created by chenbo on 17/4/6.
 */
public interface UserService {

    User createUser(User user);

    void updateUser(User user);

    void delete(Long userId);

    User getUserById(Long userId);

    User findByUsername(String username);

    void saveSession(Long userId, Session session);

    Session loadSession(Long userId);

    void clearSession(Long userId);
}
