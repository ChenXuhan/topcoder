package com.buaa.act.sdp;

import com.buaa.act.sdp.service.ChallengeApi;
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
        //challengeApi.savePastChallenge();
    }

    @Test
    public void testGetMissedChallenge(){
        challengeApi.getMissedChallenges(30012813);
    }
    @Test
    public void testPhrase(){
        System.out.println(challengeApi.getChallengePhasesById(30018229));
    }
}
