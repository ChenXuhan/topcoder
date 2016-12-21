package com.buaa.act.sdp.service;

/**
 * Created by YLT on 2016/10/18.
 */

import com.buaa.act.sdp.bean.challenge.*;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengePhaseDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.util.JsonUtil;
import com.buaa.act.sdp.util.RequestUtil;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ChallengeItem getChallengeById(int challengeId) {
        for(int i=0;i<10;i++) {
            String str = RequestUtil.request("http://api.topcoder.com/v2/challenges/" + challengeId);
            if (str != null) {
                return JsonUtil.fromJson(str, ChallengeItem.class);
            }
        }
        return null;
    }

    public ChallengeRegistrant[] getChallengeRegistrantsById(int challengeId) {
        for(int i=0;i<10;i++) {
            String str = RequestUtil.request("http://api.topcoder.com/v2/challenges/registrants/" + challengeId);
            if (str != null) {
                return JsonUtil.fromJson(str, ChallengeRegistrant[].class);
            }
        }
        return null;
    }

    public ChallengePhase[] getChallengePhasesById(int challengeId) {
        for(int i=0;i<10;i++) {
            String str = RequestUtil.request("http://api.topcoder.com/v2/challenges/phases/" + challengeId);
            if (str != null) {
                JsonElement jsonElement = JsonUtil.getJsonElement(str, "phases");
                if (jsonElement != null) {
                    return JsonUtil.fromJson(jsonElement, ChallengePhase[].class);
                }
            }
        }
        return null;
    }

    public ChallengeSubmission[] getChallengeSubmissionsById(int challengeId) {
        for(int i=0;i<10;i++) {
            String str = RequestUtil.request("http://api.topcoder.com/v2/develop/challenges/result/" + challengeId);
            if (str != null) {
                JsonElement jsonElement = JsonUtil.getJsonElement(str, "results");
                if (jsonElement != null) {
                    return JsonUtil.fromJson(jsonElement, ChallengeSubmission[].class);
                }
            }
        }
        return null;
    }

    public int getCompleteChallengeCount() {
        for(int i=0;i<10;i++) {
            String str = RequestUtil.request("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=1&pageSize=50");
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "total");
            if (jsonElement.isJsonPrimitive()) {
                System.out.print(jsonElement.getAsInt());
                return jsonElement.getAsInt();
            }
        }
        return 0;
    }

    public PastChallenge[] getPastChallenges(int pageIndex, int pageSize) {
        for(int i=0;i<10;i++) {
            String str = RequestUtil.request("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=" + pageIndex + "&pageSize=" + pageSize);
            if (str != null) {
                JsonElement jsonElement = JsonUtil.getJsonElement(str, "data");
                if (jsonElement != null) {
                    PastChallenge[] pastChallenges = JsonUtil.fromJson(jsonElement, PastChallenge[].class);
                    return pastChallenges;
                }
            }
        }
        return null;
    }

    public boolean challengeExistOrNot(int challengeId) {
        ChallengeItem item = challengeItemDao.getChallengeItemById(challengeId);
        if (item!=null) {
            return true;
        }
        return false;
    }

    public void savePastChallenge() {
        int count = getCompleteChallengeCount();
        int pages = count / 50;
        if (count % 50 != 0) {
            pages++;
        }
        PastChallenge[] pastChallenges;
        ChallengeItem challengeItem;
        ChallengeRegistrant[] challengeRegistrant;
        ChallengeSubmission[] challengeSubmissions;
        ChallengePhase[] challengePhases;
        int challengeId;
        for (int i = 1; i <= pages; i++) {
            System.out.println("page " + i + "");
            pastChallenges = getPastChallenges(i, 50);
            if (pastChallenges == null) {
                continue;
            }
            for (int j = 0; j < pastChallenges.length; j++) {
                challengeId = pastChallenges[j].getChallengeId();
                challengeItem=getChallengeById(challengeId);
                if(challengeItem!=null&&(!challengeExistOrNot(challengeId))) {
                    challengeItemGenerate(challengeItem, pastChallenges[j]);
                    handChallenge(challengeItem);
                }
            }
        }
    }

    public void handChallenge(ChallengeItem challengeItem) {
        int challengeId = challengeItem.getChallengeId();
        challengeItemDao.insert(challengeItem);
        System.out.println(challengeId+" finished");
        ChallengeRegistrant[] challengeRegistrant = getChallengeRegistrantsById(challengeId);
        if (challengeRegistrant != null && challengeRegistrant.length != 0) {
            challengeRegistrantGenerate(challengeId, challengeRegistrant);
            challengeRegistrantDao.insert(challengeRegistrant);
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

    public void getMissedChallenges(int startId) {
        int min = 30000000;
        ChallengeItem challengeItem;
        while (startId >= min) {
            challengeItem=getChallengeById(startId);
            if(challengeItem!=null){
                handChallenge(challengeItem);
            }
            startId--;
        }
    }
}
