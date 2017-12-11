package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.service.api.DeveloperApi;
import com.buaa.act.sdp.topcoder.service.api.statistics.DeveloperStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yang on 2016/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class DeveloperTest {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperTest.class);
    @Autowired
    private DeveloperApi developerApi;

    @Autowired
    private DeveloperStatistics developerStatistics;

    @Test
    public void testInsertUser() {
        developerApi.getDeveloperByName("iRabbit");
    }

}
