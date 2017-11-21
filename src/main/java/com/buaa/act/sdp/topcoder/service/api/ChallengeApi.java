package com.buaa.act.sdp.topcoder.service.api;

/**
 * Created by YLT on 2016/10/18.
 */

import com.buaa.act.sdp.topcoder.dao.*;
import com.buaa.act.sdp.topcoder.model.challenge.*;
import com.buaa.act.sdp.topcoder.service.api.statistics.ChallengeStatistics;
import com.buaa.act.sdp.topcoder.service.api.statistics.UserStatistics;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by yang on 2016/9/30.
 */
@Service
public class ChallengeApi {

    @Autowired
    private ChallengeItemDao challengeItemDao;
    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private ChallengePhaseDao challengePhaseDao;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private UserApi userApi;
    @Autowired
    private ChallengeStatistics challengeStatistics;
    @Autowired
    private UserStatistics userStatistics;

    private static final Logger logger = LoggerFactory.getLogger(ChallengeApi.class);

    private static final String TASK_URL_PREFIX = "http://api.topcoder.com/v2/challenges/";
    private static final String REGISTRANT_URL_PREFIX = "http://api.topcoder.com/v2/challenges/registrants/";
    private static final String PHRASE_URL_PREFIX = "http://api.topcoder.com/v2/challenges/phases/";
    private static final String SUBMISSION_URL_PREFIX = "http://api.topcoder.com/v2/develop/challenges/result/";

    /**
     * 获取task基本信息
     *
     * @param challengeId
     * @return
     */
    public ChallengeItem getChallengeById(int challengeId) {
        logger.info("get task from topcoder,taskId=" + challengeId);
        String str = null;
        try {
            str = RequestUtil.request(TASK_URL_PREFIX + challengeId);
        } catch (Exception e) {
            logger.error("error occurred in getting task,taskId=" + challengeId, e);
        }
        if (str != null) {
            return JsonUtil.fromJson(str, ChallengeItem.class);
        }
        return null;
    }

    /**
     * 获取task的注册开发者信息
     *
     * @param challengeId
     * @return
     */
    public ChallengeRegistrant[] getChallengeRegistrantsById(int challengeId) {
        logger.info("get task registrants from topcoder,taskId=" + challengeId);
        String str = null;
        try {
            str = RequestUtil.request(REGISTRANT_URL_PREFIX + challengeId);
        } catch (Exception e) {
            logger.error("error occurred in getting task registrants,taskId=" + challengeId, e);
        }
        if (str != null) {
            return JsonUtil.fromJson(str, ChallengeRegistrant[].class);
        }
        return null;
    }

    /**
     * 获取task的发布时间信息
     *
     * @param challengeId
     * @return
     */
    public ChallengePhase[] getChallengePhasesById(int challengeId) {
        logger.info("get task phrase from topcoder,taskId=" + challengeId);
        String str = null;
        try {
            str = RequestUtil.request(PHRASE_URL_PREFIX + challengeId);
        } catch (Exception e) {
            logger.error("error occurred in getting task phrase,taskId=" + challengeId, e);
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "phases");
            if (jsonElement != null) {
                return JsonUtil.fromJson(jsonElement, ChallengePhase[].class);
            }
        }
        return null;
    }

    /**
     * 获取task的提交信息
     *
     * @param challengeId
     * @return
     */
    public ChallengeSubmission[] getChallengeSubmissionsById(int challengeId) {
        logger.info("get task submission from topcoder,taskId=" + challengeId);
        String str = null;
        try {
            str = RequestUtil.request(SUBMISSION_URL_PREFIX + challengeId);
        } catch (Exception e) {
            logger.error("error occurred in getting task submission,taskId=" + challengeId, e);
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "results");
            if (jsonElement != null) {
                return JsonUtil.fromJson(jsonElement, ChallengeSubmission[].class);
            }
        }
        return null;
    }

    /**
     * 获取task及task相关的开发者信息
     *
     * @param challengeItem
     * @param username
     */
    public void saveFinishedChallenge(ChallengeItem challengeItem, Set<String> username) {
        logger.info("save new task's and developer's information,taskId=" + challengeItem.getChallengeId());
        int challengeId = challengeItem.getChallengeId();
        int registerCount = 0, submissionCount = 0;
        ChallengeRegistrant[] challengeRegistrant = getChallengeRegistrantsById(challengeId);
        if (challengeRegistrant != null && challengeRegistrant.length != 0) {
            logger.info("save new task's registrants,taskId=" + challengeItem.getChallengeId());
            challengeRegistrantGenerate(challengeId, challengeRegistrant);
            challengeRegistrantDao.insertBatch(challengeRegistrant);
            for (int i = 0; i < challengeRegistrant.length; i++) {
                if (!username.contains(challengeRegistrant[i].getHandle())) {
                    username.add(challengeRegistrant[i].getHandle());
                    userApi.saveUserMsg(challengeRegistrant[i].getHandle());
                } else {
                    userApi.updateUserMsg(challengeRegistrant[i].getHandle());
                }
                userStatistics.updateTaskCount(challengeRegistrant[i].getHandle());
            }
            registerCount = challengeRegistrant.length;
        }
        ChallengeSubmission[] challengeSubmissions = getChallengeSubmissionsById(challengeId);
        if (challengeSubmissions != null && challengeSubmissions.length != 0) {
            logger.info("save new task's submission,taskId=" + challengeItem.getChallengeId());
            challengeSubmissionGenerate(challengeId, challengeSubmissions);
            challengeSubmissionDao.insertBatch(challengeSubmissions);
            submissionCount = challengeSubmissions.length;
        }
        ChallengePhase[] challengePhases = getChallengePhasesById(challengeId);
        if (challengePhases != null && challengePhases.length != 0) {
            logger.info("save new task's phrase,taskId=" + challengeItem.getChallengeId());
            challengePhaseGenerate(challengeId, challengePhases);
            challengePhaseDao.insertBatch(challengePhases);
        }
        challengeStatistics.updateChallenge(challengeItem, registerCount, submissionCount);
        challengeItemDao.insert(challengeItem);
    }

    public void challengeItemGenerate(ChallengeItem item, PastChallenge pastChallenge) {
        item.setNumRegistrants(pastChallenge.getNumRegistrants());
        item.setNumSubmissions(pastChallenge.getNumSubmissions());
        item.setRegistrationStartDate(pastChallenge.getRegistrationStartDate());
    }

    public void challengeRegistrantGenerate(int challengeId, ChallengeRegistrant[] challengeRegistrants) {
        for (int i = 0; i < challengeRegistrants.length; i++) {
            challengeRegistrants[i].setChallengeID(challengeId);
        }
    }

    public void challengeSubmissionGenerate(int challengeId, ChallengeSubmission[] challengeSubmissions) {
        for (int i = 0; i < challengeSubmissions.length; i++) {
            challengeSubmissions[i].setChallengeID(challengeId);
        }
    }

    public void challengePhaseGenerate(int challengeId, ChallengePhase[] challengePhases) {
        for (int i = 0; i < challengePhases.length; i++) {
            challengePhases[i].setChallengeID(challengeId);
        }
    }

}
