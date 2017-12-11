package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.service.recommend.experiment.TeamRecommendExperiment;
import com.buaa.act.sdp.topcoder.service.recommend.result.TeamRecommend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yang on 2017/6/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TeamRecommendTest {

    @Autowired
    private TeamRecommend teamRecommend;
    @Autowired
    private TeamRecommendExperiment teamRecommendExperiment;


    @Test
    public void testProjectToTasks() {
        System.out.println(teamRecommendExperiment.getTestProjectId());
    }

    @Test
    public void testCompareTeamRecommend() {
        try {
            teamRecommendExperiment.compareTeamRecommendResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTeamRecommend() throws Exception {
        System.out.println(teamRecommend.generateBestTeamUsingHeuristic(9200));
    }
}
