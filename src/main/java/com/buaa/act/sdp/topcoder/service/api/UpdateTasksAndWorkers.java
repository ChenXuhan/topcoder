package com.buaa.act.sdp.topcoder.service.api;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.TimeOutDao;
import com.buaa.act.sdp.topcoder.dao.UserDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.challenge.PastChallenge;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yang on 2017/11/10.
 */
@Service
public class UpdateTasksAndWorkers {

    @Autowired
    private ChallengeApi challengeApi;

    @Autowired
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TimeOutDao timeOutDao;

    /**
     * 获取当前已经完成的任务数量
     * @return
     */
    public int getCompletedChallengeCount() {
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

    /**
     * 增量保存所有完成的task
     */
    public void updateFinishedChallenges() {
        int count =getCompletedChallengeCount();
        int pageSize= Constant.PAGESIZE;
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
                        challengeApi.challengeItemGenerate(challengeItem, pastChallenges[j]);
                        challengeApi.saveFinishedChallenge(challengeItem, userSet);
                    }
                }
            }
        }
    }

}
