package com.buaa.act.sdp.service;

import com.buaa.act.sdp.bean.user.*;
import com.buaa.act.sdp.dao.DevelopmentDao;
import com.buaa.act.sdp.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.dao.RatingHistoryDao;
import com.buaa.act.sdp.dao.UserDao;
import com.buaa.act.sdp.util.JsonUtil;
import com.buaa.act.sdp.util.RequestUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/15.
 */
@Service
public class UserApi {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DevelopmentDao developmentDao;

    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;

    @Autowired
    private RatingHistoryDao ratingHistoryDao;

    public void getUserByName(String userName) {
        String json = RequestUtil.request("http://api.topcoder.com/v2/users/" + userName);
        if (json != null) {
            User user = JsonUtil.fromJson(json, User.class);
            userDao.insert(user);
        }
    }

    public void handUserDevelopmentInfo(String handle, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "Tracks");
        if (jsonElement != null) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, Development> map = JsonUtil.jsonToMap(jsonObject, Development.class);
            List<Development> lists = new ArrayList<>();
            for (Map.Entry<String, Development> entry : map.entrySet()) {
                Development development = entry.getValue();
                development.setDevelopType(entry.getKey());
                development.setHandle(handle);
                lists.add(development);
            }
            if (lists.size() > 0) {
                developmentDao.insert(lists);
            }
        }
        jsonElement = JsonUtil.getJsonElement(json, "CompetitionHistory");
        if (jsonElement != null) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, DevelopmentHistory> map = JsonUtil.jsonToMap(jsonObject, DevelopmentHistory.class);
            List<DevelopmentHistory> lists = new ArrayList<>();
            for (Map.Entry<String, DevelopmentHistory> entry : map.entrySet()) {
                DevelopmentHistory developmentHistory = entry.getValue();
                developmentHistory.setDevelopType(entry.getKey());
                developmentHistory.setHandle(handle);
                lists.add(developmentHistory);
            }
            if (lists.size() > 0) {
                developmentHistoryDao.insert(lists);
            }
        }
    }

    public void getUserStatistics(String userName) {
        String string = RequestUtil.request("http://api.topcoder.com/v2/users/" + userName + "/statistics/develop");
        if (string != null) {
            handUserDevelopmentInfo(userName, string);
        }
    }

    //"challengeType should be an element of design,development,specification,architecture,bug_hunt,test_suites,assembly,ui_prototypes,conceptualization,ria_build,ria_component,test_scenarios,copilot_posting,content_creation,reporting,marathon_match,first2finish,code,algorithm."
    public void getUserChallengeHistory(String userName, String challengeType) {
        String json = RequestUtil.request("http://api.topcoder.com/v2/develop/statistics/" + userName + "/" + challengeType);
        if (json != null) {
            handUserRatingHistory(userName, challengeType, json);
        }
    }

    public void handUserRatingHistory(String userName, String challengeType, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "history");
        if (jsonElement != null) {
            RatingHistory[] ratingHistories = JsonUtil.fromJson(jsonElement, RatingHistory[].class);
            if (ratingHistories != null && ratingHistories.length > 0) {
                for (int i = 0; i < ratingHistories.length; i++) {
                    ratingHistories[i].setHandle(userName);
                    ratingHistories[i].setDevelopType(challengeType);
                }
                ratingHistoryDao.insert(ratingHistories);
            }
        }
    }

    public void saveUser(String handle){
        getUserByName(handle);
        getUserStatistics(handle);
        getUserChallengeHistory(handle,"design");
        getUserChallengeHistory(handle,"development");
        getUserChallengeHistory(handle,"specification");
        getUserChallengeHistory(handle,"architecture");
        getUserChallengeHistory(handle,"bug_hunt");
        getUserChallengeHistory(handle,"test_suites");
        getUserChallengeHistory(handle,"ui_prototypes");
        getUserChallengeHistory(handle,"conceptualization");
        getUserChallengeHistory(handle,"ria_build");
        getUserChallengeHistory(handle,"ria_component");
        getUserChallengeHistory(handle,"test_scenarios");
        getUserChallengeHistory(handle,"copilot_posting");
        getUserChallengeHistory(handle,"content_creation");
        getUserChallengeHistory(handle,"first2finish");
        getUserChallengeHistory(handle,"code");
    }
}
