package com.buaa.act.sdp.topcoder.service.api;

/**
 * Created by YLT on 2016/10/18.
 */

import com.buaa.act.sdp.topcoder.dao.*;
import com.buaa.act.sdp.topcoder.model.challenge.*;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
import com.google.gson.JsonElement;
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
    private UserDao userDao;
    @Autowired
    private UserApi userApi;
    @Autowired
    private TimeOutDao timeOutDao;

    /**
     * 获取task基本信息
     * @param challengeId
     * @return
     */
    public ChallengeItem getChallengeById(int challengeId) {
        String str = null;
        try {
            str = RequestUtil.request("http://api.topcoder.com/v2/challenges/" + challengeId);
        } catch (Exception e) {
            System.err.println("time out getChallenge " + challengeId);
            timeOutDao.insertTimeOutData("challenge", "" + challengeId);
        }
        if (str != null) {
            return JsonUtil.fromJson(str, ChallengeItem.class);
        }
        return null;
    }

    /**
     * 获取task的注册开发者信息
     * @param challengeId
     * @return
     */
    public ChallengeRegistrant[] getChallengeRegistrantsById(int challengeId) {
        String str = null;
        try {
            str = RequestUtil.request("http://api.topcoder.com/v2/challenges/registrants/" + challengeId);
        } catch (Exception e) {
            System.err.println("time out getRegistrants " + challengeId);
            timeOutDao.insertTimeOutData("registrant", "" + challengeId);
        }
        if (str != null) {
            return JsonUtil.fromJson(str, ChallengeRegistrant[].class);
        }
        return null;
    }

    /**
     * 获取task的发布时间信息
     * @param challengeId
     * @return
     */
    public ChallengePhase[] getChallengePhasesById(int challengeId) {
        String str = null;
        try {
            str = RequestUtil.request("http://api.topcoder.com/v2/challenges/phases/" + challengeId);
        } catch (Exception e) {
            System.err.println("time out getPhases " + challengeId);
            timeOutDao.insertTimeOutData("phase", "" + challengeId);
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
     * @param challengeId
     * @return
     */
    public ChallengeSubmission[] getChallengeSubmissionsById(int challengeId) {
        String str = null;
        try {
            str = RequestUtil.request("http://api.topcoder.com/v2/develop/challenges/result/" + challengeId);
        } catch (Exception e) {
            System.err.println("time out getSubmissions " + challengeId);
            timeOutDao.insertTimeOutData("submission", "" + challengeId);
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
     * @param challengeItem
     * @param username
     */
    public void saveFinishedChallenge(ChallengeItem challengeItem, Set<String> username) {
        challengeItemDao.insert(challengeItem);
        int challengeId = challengeItem.getChallengeId();
        System.out.println(challengeId);
        ChallengeRegistrant[] challengeRegistrant = getChallengeRegistrantsById(challengeId);
        if (challengeRegistrant != null && challengeRegistrant.length != 0) {
            challengeRegistrantGenerate(challengeId, challengeRegistrant);
            challengeRegistrantDao.insertBatch(challengeRegistrant);
            for (int i = 0; i < challengeRegistrant.length; i++) {
                if (!username.contains(challengeRegistrant[i].getHandle())) {
                    username.add(challengeRegistrant[i].getHandle());
                    userApi.saveUserMsg(challengeRegistrant[i].getHandle());
                }else {
                    userApi.updateUserMsg(challengeRegistrant[i].getHandle());
                }
            }
        }
        ChallengeSubmission[] challengeSubmissions = getChallengeSubmissionsById(challengeId);
        if (challengeSubmissions != null && challengeSubmissions.length != 0) {
            challengeSubmissionGenerate(challengeId, challengeSubmissions);
            challengeSubmissionDao.insertBatch(challengeSubmissions);
        }
        ChallengePhase[] challengePhases = getChallengePhasesById(challengeId);
        if (challengePhases != null && challengePhases.length != 0) {
            challengePhaseGenerate(challengeId, challengePhases);
            challengePhaseDao.insertBatch(challengePhases);
        }
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
