package com.haizhi.bqd.service.service.impl;

import com.haizhi.bqd.common.security.SimpleHashPasswordEncoder;
import com.haizhi.bqd.service.model.Session;
import com.haizhi.bqd.service.model.User;
import com.haizhi.bqd.service.repo.RedisRepo;
import com.haizhi.bqd.service.repo.UserRepo;
import com.haizhi.bqd.service.service.UserService;
import com.haizhi.bqd.service.support.TxManagerConstant;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by chenbo on 17/4/6.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Setter
    private UserRepo userRepo;

    @Setter
    private RedisRepo redisRepo;

    @Setter
    private SimpleHashPasswordEncoder passwordEncoder;

    private Integer sessionExpireTime = 60 * 60 * 12;

    @Override
    public User createUser(User user) {
        //填充权限
        user.setPermission(user.getRole());
        String salt = passwordEncoder.genSalt(4);
        user.setSalt(salt);
        user.setPassword(passwordEncoder.encodePassword(user.getPassword(), salt));
        return userRepo.create(user);
    }

    @Transactional(TxManagerConstant.TxManager)
    @Override
    public void updateUser(User user) {
        //填充权限
        user.setPermission(user.getRole());
        userRepo.update(user);
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepo.findByUsername(username);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId);
    }

    public void updateUserWithDeadLine(Date deadLine, Long id) {
        userRepo.updateWithDeadLine(deadLine, id);
    }

    @Transactional(TxManagerConstant.TxManager)
    @Override
    public void delete(Long userId) {
        userRepo.delete(userId);
    }

    protected String getSessionKey(Long userId) {
        return "u:" + userId;
    }

    @Override
    public void saveSession(Long userId, Session session) {
        redisRepo.push(getSessionKey(userId), session, sessionExpireTime);
    }

    @Override
    public Session loadSession(Long userId) {
        return redisRepo.get(getSessionKey(userId), Session.class);
    }

    @Override
    public void clearSession(Long userId) {
        redisRepo.delete(getSessionKey(userId));
    }

}
