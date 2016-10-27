package com.buaa.act.sdp;

import com.buaa.act.sdp.service.UserApi;
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
    private UserApi  userApi;
    @Test
    public void testInsertUser(){
        userApi.getUserByName("iRabbit");
    }
    @Test
    public void testInsertDevelopment(){
        userApi.getUserStatistics("lifeloner");
    }
    @Test
    public void testInsertRatingHistory(){
        userApi.getUserChallengeHistory("lifeloner","development");
    }

    @Test
    public void testSaveUser(){
        userApi.getUserStatistics("14blades");
        //userApi.saveUser("iRabbit");
    }
    @Test
    public void test1(){
        userApi.saveAllUsers();
    }

}
