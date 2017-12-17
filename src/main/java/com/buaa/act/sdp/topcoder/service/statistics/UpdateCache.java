package com.buaa.act.sdp.topcoder.service.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by yang on 2017/11/12.
 */
@Component
public class UpdateCache {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCache.class);

    @Autowired
    private ProjectMsg projectMsg;
    @Autowired
    protected TaskMsg taskMsg;
    @Autowired
    private TaskScores taskScores;

    /**
     * 更新缓存数据
     */
    public void updateData() {
        logger.info("update all data cache, every week...");
        projectMsg.update();
        taskScores.update();
        taskMsg.update();
    }
}
