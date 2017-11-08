package com.buaa.act.sdp;

import com.buaa.act.sdp.service.api.UserApi;
import com.buaa.act.sdp.service.update.UserStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yang on 2016/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestUser {

    @Autowired
    private UserApi userApi;

    @Autowired
    private UserStatistics userStatistics;

    @Test
    public void testInsertUser() {
        userApi.getUserByName("iRabbit");
    }

    @Test
    public void testInsertDevelopment() {
        userApi.getUserStatistics("lifeloner");
    }

    @Test
    public void testInsertRatingHistory() {
        userApi.getUserChallengeHistory("lifeloner", "development");
    }

    @Test
    public void testSaveUser() {
        //userApi.getUserStatistics("arthurjlp");
        // userApi.getUserByName("arthurjlp");
        //linerRegression.getDate("30055549");
        //userApi.saveUser("iRabbit");
    }

    @Test
    public void test1() {
        userApi.saveAllUsers();
    }

    @Test
    public void updateUsers() {
        userStatistics.updateUsers();
    }
}
