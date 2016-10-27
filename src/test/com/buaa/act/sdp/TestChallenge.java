package com.buaa.act.sdp;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.service.ChallengeApi;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * Created by yang on 2016/10/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestChallenge {

    @Autowired
    private ChallengeApi challengeApi;
    /**
     *
     */

    @Test
    public void test(){
        challengeApi.savePastChallenge();
    }
    @Test
    public void testId(){
        challengeApi.storeTest(30047584);
    }
   /* @Test
    public void testExist(){
        challengeApi.existOrNot(30054988);
    }*/


}
