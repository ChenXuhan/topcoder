package com.buaa.act.sdp.topcoder.service.redis;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by yang on 2017/11/12.
 */
@Component
public class UpdateRedisCache {

    private static final Logger logger = LoggerFactory.getLogger(UpdateRedisCache.class);

    @Autowired
    private ProjectMsg projectMsg;
    @Autowired
    protected TaskMsg taskMsg;
    @Autowired
    private TaskScores taskScores;

    /**
     * 更新缓存数据
     */
    public void updateCache() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            if (!ip.equals(Constant.IP)) {
                return;
            }
        } catch (UnknownHostException e) {
            logger.error("error occurred in getting local ip address...");
        }
        logger.info("update all data cache, every week...");
        projectMsg.update();
        taskScores.update();
        taskMsg.update();
    }
}
