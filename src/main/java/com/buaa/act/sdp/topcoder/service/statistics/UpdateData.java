package com.buaa.act.sdp.topcoder.service.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by yang on 2017/11/12.
 */
@Component
public class UpdateData {
    @Autowired
    private ProjectMsg projectMsg;
    @Autowired
    protected TaskMsg taskMsg;
    @Autowired
    private TaskScores taskScores;

    public void update(){
        projectMsg.update();
        taskScores.update();
        taskMsg.update();
    }
}
