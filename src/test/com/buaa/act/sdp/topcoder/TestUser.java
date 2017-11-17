package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.controller.UserController;
import com.buaa.act.sdp.topcoder.service.api.UserApi;
import com.buaa.act.sdp.topcoder.service.api.statistics.UserStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yang on 2016/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestUser {

    private static final Logger logger= LoggerFactory.getLogger(TestUser.class);
    @Autowired
    private UserApi userApi;

    @Autowired
    private UserStatistics userStatistics;

    @Autowired
    private UserController userController;

    @Test
    public void testInsertUser() {
        userApi.getUserByName("iRabbit");
    }

    @Test
    public void updateUsers() {
        userStatistics.updateUsers();
    }
}
