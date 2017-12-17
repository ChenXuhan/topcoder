package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.service.api.UpdateTasksAndDevelopers;
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
public class TaskTest {

    @Autowired
    private UpdateTasksAndDevelopers updateTasksAndDevelopers;

    @Test
    public void testGetChallengeId() {
        updateTasksAndDevelopers.updateFinishedTasks();
    }

}
