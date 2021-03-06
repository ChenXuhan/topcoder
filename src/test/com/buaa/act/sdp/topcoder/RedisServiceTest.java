package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.service.redis.UpdateRedisCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yang on 2017/12/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class RedisServiceTest {

    @Autowired
    private UpdateRedisCache updateRedisCache;

    @Test
    public void update() {
        updateRedisCache.updateCache();
    }
}
