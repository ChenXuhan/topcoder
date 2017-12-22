package com.buaa.act.sdp.topcoder.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/12/21.
 */
@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public <T> void setObjectCache(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> T getObjectCache(String key) {
        return (T) (redisTemplate.opsForValue().get(key));
    }

    public <T> void setListCache(String key, List<T> items) {
        redisTemplate.opsForList().rightPushAll(key, items);
    }

    public <T> List<T> getListCache(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public <K, V> void setMapCache(String key, K mapKey, V mapValue) {
        redisTemplate.opsForHash().put(key, mapKey, mapValue);
    }

    public <K, V> void setMapCaches(String key, Map<K, V> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public <K, V> V getMapCache(String key, K mapKey) {
        return (V) (redisTemplate.opsForHash().get(key, mapKey));
    }

    public <K, V> Map<K, V> getMapCaches(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
