package com.buaa.act.sdp;

import com.buaa.act.sdp.service.recommend.FeatureExtract;
import com.buaa.act.sdp.service.recommend.RecommendResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Test
    public void testFeatureExtract() {
        featureExtract.getFeatures("Code");
    }

    @Test
    public void testRecommend() {
//        recommendResult.getRecommendResult("First2Finish");
//        recommendResult.getRecommendBayesUcl("Assembly Competition");
//        recommendResult.clusterAndClassifier("Assembly Competition", 200);
//        recommendResult.clusterClassifier("Development",3);
        recommendResult.localClassifier("Assembly Competition");
    }
}
