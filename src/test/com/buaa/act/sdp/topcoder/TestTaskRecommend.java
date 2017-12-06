package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import com.buaa.act.sdp.topcoder.service.recommend.experiment.TaskRecommendExperiment;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.recommend.feature.Reliability;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yang on 2017/3/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestTaskRecommend {

    @Autowired
    private TaskService taskService;
    @Autowired
    private DeveloperRecommend developerRecommend;
    @Autowired
    private TaskRecommendExperiment recommendResult;

    @Autowired
    private Reliability reliability;

    @Test
    public void testRecommend() {
//        Code
//        First2Finish
//        Assembly Competition
        String challengeType = "Code";
//        recommendResult.contentBased(challengeType);
//        recommendResult.classifier(challengeType);
        recommendResult.clusterClassifier(challengeType, 3);
//        recommendResult.localClassifier(challengeType);
//        recommendResult.dcw_ds(challengeType);
    }

    @Test
    public void testTimeInterval() {
        reliability.timeInterval("Code");
    }

    @Test
    public void taskMsg() {
        ChallengeItem item=taskService.getChallengeById(30036600);
        System.out.println(developerRecommend.recommendWorkers(item));
    }
}
