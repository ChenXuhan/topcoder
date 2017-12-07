package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import com.buaa.act.sdp.topcoder.service.recommend.experiment.TaskRecommendExperiment;
import com.buaa.act.sdp.topcoder.service.recommend.feature.Reliability;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;
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
        String challengeType = "Assembly Competition";
//        String challengeType = "First2Finish";
//        String challengeType = "Code";
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
    public void taskMsgRecommend() {
        ChallengeItem item = taskService.getChallengeById(30036613);
        System.out.println(developerRecommend.recommendWorkers(item));
    }
}
