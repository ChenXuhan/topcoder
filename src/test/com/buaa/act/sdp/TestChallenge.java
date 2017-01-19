package com.buaa.act.sdp;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.service.api.ChallengeApi;
import com.buaa.act.sdp.service.cbm.ContentBased;
import com.buaa.act.sdp.service.statistics.ChallengeStatistics;
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

    @Autowired
    private ContentBased contentBased;

    @Autowired(required = false)
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeStatistics challengeStatistics;

    @Test
    public void test(){
//        challengeApi.savePastChallenge();
//        System.out.println(Constant.PLATFORMS.length);
//       System.out.println(challengeSubmissionDao.getChallengeAndScore().get(5).getFinalScore());
//        ChallengeItem item= challengeItemDao.getChallengeItemById(30055474);
//        System.out.println(contentBased.contentBasedRecomend(item));
          contentBased.recommendAccurary();

    }

    @Test
    public void testGetMissedChallenge(){
        challengeApi.getMissedChallenges(30012813);
    }

    @Test
    public void testPhrase(){
        System.out.println(challengeApi.getChallengePhasesById(30018229));
    }

    @Test
    public void updateChallenges(){
        challengeStatistics.updateChallenges();
    }
}
