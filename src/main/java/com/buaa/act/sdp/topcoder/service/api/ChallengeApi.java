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

import java.util.HashSet;
import java.util.List;
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
     * 获取当前已经完成的任务
     * @return
     */
    public int getCompleteChallengeCount() {
        String str = null;
        try {
            str = RequestUtil.request("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=1&pageSize=50");
        } catch (Exception e) {
            System.err.println("time out getChallengeCount");
            timeOutDao.insertTimeOutData("challengeCount", "");
        }
        JsonElement jsonElement = JsonUtil.getJsonElement(str, "total");
        if (jsonElement.isJsonPrimitive()) {
            System.out.print(jsonElement.getAsInt());
            return jsonElement.getAsInt();
        }
        return 0;
    }

    /**
     * 分页获取历史完成任务
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public PastChallenge[] getPastChallenges(int pageIndex, int pageSize) {
        String str = null;
        try {
            str = RequestUtil.request("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=" + pageIndex + "&pageSize=" + pageSize);
        } catch (Exception e) {
            System.err.println("time out getPastChallenges");
            timeOutDao.insertTimeOutData("pastChallenges", pageIndex + "_" + pageSize);
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

    public boolean challengeExistOrNot(int challengeId) {
        ChallengeItem item = challengeItemDao.getChallengeItemById(challengeId);
        if (item != null) {
            return true;
        }
        return false;
    }

    /**
     * 增量保存所有完成的task
     */
    public void savePastChallenge() {
        int count = getCompleteChallengeCount();
        int pages = count / 50;
        if (count % 50 != 0) {
            pages++;
        }
        List<String> userList = userDao.getUsers();
        Set<String> userSet = new HashSet<>();
        userSet.addAll(userList);
        List<Integer> challengeList = challengeItemDao.getChallenges();
        Set<Integer> challengeSet = new HashSet<>();
        challengeSet.addAll(challengeList);
        PastChallenge[] pastChallenges;
        ChallengeItem challengeItem;
        int challengeId;
        for (int i = 1; i <= pages; i++) {
            pastChallenges = getPastChallenges(i, 50);
            if (pastChallenges == null || pastChallenges.length == 0) {
                continue;
            }
            for (int j = 0; j < pastChallenges.length; j++) {
                challengeId = pastChallenges[j].getChallengeId();
                if (!challengeSet.contains(challengeId)) {
                    challengeItem = getChallengeById(challengeId);
                    if (challengeItem != null) {
                        challengeItemGenerate(challengeItem, pastChallenges[j]);
                        handChallenge(challengeItem, userSet);
                    }
                }
            }
        }
    }

    /**
     * 获取task及task相关的开发者信息
     * @param challengeItem
     * @param username
     */
    public void handChallenge(ChallengeItem challengeItem, Set<String> username) {
        challengeItemDao.insert(challengeItem);
        int challengeId = challengeItem.getChallengeId();
        System.out.println(challengeId);
        ChallengeRegistrant[] challengeRegistrant = getChallengeRegistrantsById(challengeId);
        if (challengeRegistrant != null && challengeRegistrant.length != 0) {
            challengeRegistrantGenerate(challengeId, challengeRegistrant);
            challengeRegistrantDao.insert(challengeRegistrant);
            for (int i = 0; i < challengeRegistrant.length; i++) {
                if (!username.contains(challengeRegistrant[i].getHandle())) {
                    username.add(challengeRegistrant[i].getHandle());
                    userApi.saveUserMessages(challengeRegistrant[i].getHandle());
                    System.out.println("\t" + challengeRegistrant[i].getHandle());
                }
            }
        }
        ChallengeSubmission[] challengeSubmissions = getChallengeSubmissionsById(challengeId);
        if (challengeSubmissions != null && challengeSubmissions.length != 0) {
            challengeSubmissionGenerate(challengeId, challengeSubmissions);
            challengeSubmissionDao.insert(challengeSubmissions);
        }
        ChallengePhase[] challengePhases = getChallengePhasesById(challengeId);
        if (challengePhases != null && challengePhases.length != 0) {
            challengePhaseGenerate(challengeId, challengePhases);
            challengePhaseDao.insert(challengePhases);
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

    /**
     * 获取失败的task
     * @param startId
     */
    public void getMissedChallenges(int startId) {
        int min = 30000000;
        List<String> userList = userDao.getUsers();
        Set<String> userSet = new HashSet<>();
        userSet.addAll(userList);
        List<Integer> challengeList = challengeItemDao.getChallenges();
        Set<Integer> challengeSet = new HashSet<>();
        challengeSet.addAll(challengeList);
        ChallengeItem challengeItem;
        while (startId >= min) {
            if (!challengeSet.contains(startId)) {
                challengeSet.add(startId);
                challengeItem = getChallengeById(startId);
                if (challengeItem != null) {
                    handChallenge(challengeItem, userSet);
                } else {
                    System.out.println(startId + " failed");
                }
            }
            startId--;
        }
    }

}
