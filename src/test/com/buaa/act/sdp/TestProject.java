package com.buaa.act.sdp;

import com.buaa.act.sdp.service.recommend.TeamRecommend;
import com.buaa.act.sdp.service.statistics.ProjectMsg;
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
public class TestProject {

    @Autowired
    public TeamRecommend teamRecommend;

    @Test
    public void testProjectId(){
        teamRecommend.getCollaborations(8021);
    }
}
