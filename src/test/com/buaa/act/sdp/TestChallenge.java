package com.buaa.act.sdp;

import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.service.api.ChallengeApi;
import com.buaa.act.sdp.service.recommend.RecommendResult;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.update.ChallengeStatistics;
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

    @Autowired(required = false)
    private ChallengeSubmissionDao challengeSubmissionDao;

    @Autowired(required = false)
    private ContentBase contentBase;

    @Autowired(required = false)
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeStatistics challengeStatistics;

    @Autowired
    private RecommendResult recommendResult;

    @Test
    public void test() {
        recommendResult.getRecommendResult();
//        recommendResult.getRecommendUcl();
    }

    @Test
    public void testGetMissedChallenge() {
        challengeApi.getMissedChallenges(30012813);
    }

    @Test
    public void testPhrase() {
        System.out.println(challengeApi.getChallengePhasesById(30018229));
    }

    @Test
    public void updateChallenges() {
        challengeStatistics.updateChallenges();
    }
}
