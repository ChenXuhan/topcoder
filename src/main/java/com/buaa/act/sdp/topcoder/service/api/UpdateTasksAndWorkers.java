package com.buaa.act.sdp.topcoder.service.api;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.UserDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.challenge.PastChallenge;
import com.buaa.act.sdp.topcoder.service.api.statistics.ChallengeStatistics;
import com.buaa.act.sdp.topcoder.service.api.statistics.UserStatistics;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yang on 2017/11/10.
 */
@Component
public class UpdateTasksAndWorkers {

    @Autowired
    private ChallengeApi challengeApi;

    @Autowired
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStatistics userStatistics;

    @Autowired
    private ChallengeStatistics challengeStatistics;

    private static final Logger logger = LoggerFactory.getLogger(UpdateTasksAndWorkers.class);

    private static final String CHALLENGE_COUNT_URL = "http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=1&pageSize=50";
    private static final String GET_COMPELETED_TASK_URL = "http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=";

    /**
     * 获取当前已经完成的任务数量
     *
     * @return
     */
    public int getCompletedChallengeCount() {
        logger.info("get completed task count from topcoder api");
        String str = null;
        try {
            str = RequestUtil.request(CHALLENGE_COUNT_URL);
        } catch (Exception e) {
            logger.error("error occurred in getting finished task's count", e);
        }
        JsonElement jsonElement = JsonUtil.getJsonElement(str, "total");
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsInt();
        }
        return 0;
    }

    /**
     * 分页获取历史完成任务
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public PastChallenge[] getPastChallenges(int pageIndex, int pageSize) {
        logger.info("get completed task id from topcoder api");
        String str = null;
        try {
            str = RequestUtil.request(GET_COMPELETED_TASK_URL + pageIndex + "&pageSize=" + pageSize);
        } catch (Exception e) {
            logger.error("error occurred in getting finished tasks id");
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "data");
            if (jsonElement != null) {
                PastChallenge[] pastChallenges = JsonUtil.fromJson(jsonElement, PastChallenge[].class);
                return pastChallenges;
            }
        }
        return null;
    }

    /**
     * 增量保存所有完成的task,更新开发者参与任务数
     */
    public void updateFinishedChallenges() {
        logger.info("update and save completed tasks,evey one week");
        int count = getCompletedChallengeCount();
        int pageSize = Constant.PAGE_SIZE;
        int pages = count / pageSize;
        if (count % pageSize != 0) {
            pages++;
        }
        List<String> userList = userDao.getDistinctUsers();
        Set<String> userSet = new HashSet<>();
        userSet.addAll(userList);
        List<Integer> challengeList = challengeItemDao.getChallengeIds();
        Set<Integer> challengeSet = new HashSet<>();
        challengeSet.addAll(challengeList);
        PastChallenge[] pastChallenges;
        ChallengeItem challengeItem;
        int challengeId;
        count = 0;
        for (int i = 1; i <= pages; i++) {
            pastChallenges = getPastChallenges(i, pageSize);
            if (pastChallenges == null || pastChallenges.length == 0) {
                continue;
            }
            for (int j = 0; j < pastChallenges.length; j++) {
                challengeId = pastChallenges[j].getChallengeId();
                if (!challengeSet.contains(challengeId)) {
                    challengeItem = challengeApi.getChallengeById(challengeId);
                    if (challengeItem != null) {
                        count++;
                        challengeApi.challengeItemGenerate(challengeItem, pastChallenges[j]);
                        challengeApi.saveFinishedChallenge(challengeItem, userSet);
                    }
                }
            }
        }
        logger.info(count + " new tasks have been saved in db");
    }

}
