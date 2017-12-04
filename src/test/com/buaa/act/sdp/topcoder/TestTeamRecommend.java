package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.service.recommend.experiment.TeamRecommendExperiment;
import com.buaa.act.sdp.topcoder.service.recommend.result.TeamRecommend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by yang on 2017/6/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestTeamRecommend {

    @Autowired
    private TeamRecommend teamRecommend;
    @Autowired
    private TeamRecommendExperiment teamRecommendExperiment;


    @Test
    public void testProjectToTasks(){
        teamRecommendExperiment.getTestProjectId();
    }

    @Test
    public void testCompareTeamRecommend(){
        teamRecommendExperiment.compareTeamRecommendResult();
    }
}
