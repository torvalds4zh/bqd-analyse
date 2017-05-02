package com.haizhi.bqd.service.repo;

import com.haizhi.bqd.service.model.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Date;

/**
 * Created by chenbo on 17/4/6.
 */
@Slf4j
@Repository
public class UserRepo {

    @Setter
    private JdbcTemplate template;

    private String TABLE_USERS = "`user`";

    private final RowMapper<User> USER_ROW_MAPPER = new BeanPropertyRowMapper<>(User.class);

    public User findById(Long id) {
        try {
            String sql = "select * from " + TABLE_USERS + " where id = ? and status = 0";
            return template.queryForObject(sql, USER_ROW_MAPPER, id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    public User findByUsername(String username) {
        try {
            String sql = "select * from " + TABLE_USERS + " where username = ? and status = 0";
            return template.queryForObject(sql, USER_ROW_MAPPER, username);
        } catch (DataAccessException ex) {
            if (!(ex instanceof EmptyResultDataAccessException)) {
                log.error("{}", ex);
            }
            return null;
        }
    }

    public Long countAll() {
        try {
            String sql = "select count(1) from " + TABLE_USERS + " where status = 0";
            return template.queryForObject(sql, Long.class);
        } catch (DataAccessException ex) {
            log.error("{}", ex);
        }
        return 0l;
    }

    public User create(User user) {
        try {
            final String sql = "insert into " + TABLE_USERS +
                    "(username, `password`, salt, mobile, permission, create_time, update_time) " +
                    "select ?, ?, ?, ?, ?, now(), now() from DUAL where not exists(select id from " + TABLE_USERS + " where username=?)";
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getSalt());
                if (user.getMobile() == null) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, user.getMobile());
                }

                if (user.getPermission() == null) {
                    ps.setNull(5, Types.VARCHAR);
                } else {
                    ps.setString(5, user.getPermission());
                }
                ps.setString(6, user.getUsername());

                return ps;
            }, holder);
            if (holder.getKey() != null) {
                user.setId(holder.getKey().longValue());
            } else {
                log.warn(TABLE_USERS + ": username={} 已存在", user.getUsername());
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return user;
    }

    public void update(User user) {
        try {
            String sql = "update " + TABLE_USERS + " " +
                    "set mobile = ?, permission = ?" +
                    "where id = ?";

            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (user.getMobile() == null) {
                    ps.setString(1, "");
                } else {
                    ps.setString(1, user.getMobile());
                }
                if (user.getPermission() == null) {
                    ps.setString(2, "");
                } else {
                    ps.setString(2, user.getPermission());
                }
                ps.setLong(3, user.getId());

                return ps;
            });
        } catch (DataAccessException ex) {
            log.error("{}", ex);
        }
    }

    public void updatePassword(User user) {
        try {
            String sql = "update " + TABLE_USERS + " set password=?, update_time=NOW() WHERE id=?";
            template.update(sql, user.getPassword(), user.getId());
        } catch (DataAccessException ex) {
            log.error("{}", ex);
        }
    }

    public void delete(Long id) {
        try {
            String sql = "update " + TABLE_USERS + " set status = 1, update_time=NOW() WHERE id=?";
            template.update(sql, id);
        } catch (DataAccessException ex) {
            log.error("{}", ex);
        }
    }

    public void updateWithDeadLine(Date deadLine, Long id) {
        try {
            String sql = "update " + TABLE_USERS + " set token_dead_time = ?, update_time=NOW() WHERE id=?";
            template.update(sql, deadLine, id);
        } catch (DataAccessException ex) {
            log.error("{}", ex);
        }
    }

}
