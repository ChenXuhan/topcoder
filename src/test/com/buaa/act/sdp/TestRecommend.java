package com.buaa.act.sdp;

import com.buaa.act.sdp.service.recommend.FeatureExtract;
import com.buaa.act.sdp.service.recommend.RecommendResult;
import com.buaa.act.sdp.service.recommend.Statistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by yang on 2017/3/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestRecommend {

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private RecommendResult recommendResult;

    @Autowired
    private Statistics statistics;

    @Test
    public void testFeatureExtract() {
        featureExtract.getFeatures("Code");
    }

    @Test
    public void testRecommend() {
//        Code
//        First2Finish
//        Assembly Competition
        String challengeType = "Assembly Competition";
        recommendResult.classifier(challengeType);
        recommendResult.contentBased(challengeType);
        recommendResult.clusterClassifier(challengeType,3);
        recommendResult.localClassifier(challengeType);
    }

    @Test
    public void testTimeInterval() {
        statistics.timeInterval("Code");
    }
}
