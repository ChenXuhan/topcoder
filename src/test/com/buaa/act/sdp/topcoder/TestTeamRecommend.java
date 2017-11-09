package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.service.recommend.result.TeamResult;
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
    public TeamResult teamResult;

    @Test
    public void testProjectId(){
        List<String>bestTeam=teamResult.findBestTeamMaxLogit(7282);
        System.out.println(bestTeam.size());
        System.out.println(bestTeam);
    }
}
