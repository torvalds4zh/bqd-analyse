package com.haizhi.bqd.service.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by chenbo on 17/4/6.
 */
@Repository
public class RedisRepo {
    @Setter
    private ObjectMapper objectMapper;

    @Setter
    private JedisPool jedisPool;

    public <T> T get(String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String response = jedis.get(key);
            return Strings.isNullOrEmpty(response) ? null :
                    objectMapper.readValue(response, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void push(String key, Object obj, Integer expireSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, expireSeconds, objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void delete(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
