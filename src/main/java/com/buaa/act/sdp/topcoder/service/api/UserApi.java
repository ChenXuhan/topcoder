package com.buaa.act.sdp.topcoder.service.api;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.*;
import com.buaa.act.sdp.topcoder.model.user.*;
import com.buaa.act.sdp.topcoder.util.HttpUtils;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
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
    @Autowired
    private TimeOutDao timeOutDao;

    /**
     * 爬取开发者信息
     *
     * @param userName
     */
    public User getUserByName(String userName) {
        String json = null;
        try {
            json = RequestUtil.request("http://api.topcoder.com/v2/users/" + userName);
        } catch (Exception e) {
            System.err.println("time out getUser " + userName);
            timeOutDao.insertTimeOutData("user ", userName);
        }
        if (json != null) {
            User user = JsonUtil.fromJson(json, User.class);
            String[] skills = getUserSkills(userName);
            if (skills != null) {
                user.setSkills(skills);
            }
            return user;
        }
        return null;
    }

    /**
     * 保存或更新开发者的基本信息
     *
     * @param userName
     */
    public void saveUserBasicInformation(String userName) {
        User user = getUserByName(userName);
        if (user != null) {
            userDao.insert(user);
        }
    }

    public void updateUserBasicInformation(String userName) {
        User user = getUserByName(userName);
        if (user != null) {
            userDao.update(user);
        }
    }

    /**
     * 获取用户的技能信息
     *
     * @param userName
     * @return
     */
    public String[] getUserSkills(String userName) {
        String json = null;
        try {
            for (int i = 0; i < Constant.RETRY_TIMES && json == null; i++) {
                json = HttpUtils.httpGet("http://api.topcoder.com/v3/members/" + userName + "/skills");
            }
        } catch (Exception e) {
            System.err.println("time out skills " + userName);
            timeOutDao.insertTimeOutData("skills ", userName);
        }
        if (json != null) {
            List<JsonElement> list = JsonUtil.getJsonElement(json, new String[]{"result", "content", "skills"});
            if (list != null && list.size() > 0) {
                JsonElement jsonElement = list.get(0);
                if (jsonElement != null && jsonElement.isJsonObject()) {
                    Map<String, Skill> map = JsonUtil.jsonToMap(jsonElement.getAsJsonObject(), Skill.class);
                    String[] str = new String[map.size()];
                    int index = 0;
                    for (Map.Entry<String, Skill> entry : map.entrySet()) {
                        str[index++] = entry.getValue().getTagName();
                    }
                    return str;
                }
            }
        }
        return null;
    }

    /**
     * 从json解析开发者的development相关信息
     *
     * @param handle
     * @param json
     * @return
     */
    public List<Development> paserDevelopmentMsg(String handle, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "Tracks");
        List<Development> lists = new ArrayList<>();
        if (jsonElement != null) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, Development> map = JsonUtil.jsonToMap(jsonObject, Development.class);
            for (Map.Entry<String, Development> entry : map.entrySet()) {
                Development development = entry.getValue();
                development.setDevelopType(entry.getKey());
                development.setHandle(handle);
                lists.add(development);
            }
        }
        return lists;
    }

    public List<DevelopmentHistory> paserDevelopmentHistoryMsg(String handle, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "CompetitionHistory");
        List<DevelopmentHistory> lists = new ArrayList<>();
        if (jsonElement != null) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, DevelopmentHistory> map = JsonUtil.jsonToMap(jsonObject, DevelopmentHistory.class);
            for (Map.Entry<String, DevelopmentHistory> entry : map.entrySet()) {
                DevelopmentHistory developmentHistory = entry.getValue();
                developmentHistory.setDevelopType(entry.getKey());
                developmentHistory.setHandle(handle);
                lists.add(developmentHistory);
            }
        }
        return lists;
    }

    /**
     * 保存或更新开发者的development相关信息
     *
     * @param userName
     */
    public void saveUserDevelopmentMsg(String userName) {
        String json = getUserDevelopmentStatistics(userName);
        if (json != null) {
            List<Development> developments = paserDevelopmentMsg(userName, json);
            if (developments.size() > 0) {
                developmentDao.insertBatch(developments);
            }
            List<DevelopmentHistory> developmentHistories = paserDevelopmentHistoryMsg(userName, json);
            if (developmentHistories.size() > 0) {
                developmentHistoryDao.insertBatch(developmentHistories);
            }
        }
    }

    public void updateUserDevelopmentMsg(String userName) {
        String json = getUserDevelopmentStatistics(userName);
        if (json != null) {
            List<Development> developments = paserDevelopmentMsg(userName, json);
            if (developments.size() > 0) {
                developmentDao.updateBatch(developments);
            }
            List<DevelopmentHistory> developmentHistories = paserDevelopmentHistoryMsg(userName, json);
            if (developmentHistories.size() > 0) {
                developmentHistoryDao.updateBatch(developmentHistories);
            }
        }
    }

    /**
     * 获取开发者development相关的信息
     *
     * @param userName
     */
    public String getUserDevelopmentStatistics(String userName) {
        String json = null;
        try {
            json = RequestUtil.request("http://api.topcoder.com/v2/users/" + userName + "/statistics/develop");
        } catch (Exception e) {
            System.err.println("time out statistics " + userName);
            timeOutDao.insertTimeOutData("statistics", userName);
        }
        if (json != null) {
            return json;
        }
        return null;
    }

    /**
     * 获取开发者某一类型任务的积分信息
     *
     * @param userName
     * @param challengeType
     */
    public void saveUserRatingHistory(String userName, String challengeType) {
        String json = null;
        try {
            json = RequestUtil.request("http://api.topcoder.com/v2/develop/statistics/" + userName + "/" + challengeType);
        } catch (Exception e) {
            System.err.println("time out history " + userName + "_" + challengeType);
            timeOutDao.insertTimeOutData("history", userName + "_" + challengeType);
        }
        if (json != null) {
            parseAndSaveUserRatingHistory(userName, challengeType, json);
        }
    }

    public void parseAndSaveUserRatingHistory(String userName, String challengeType, String json) {
        JsonElement jsonElement = JsonUtil.getJsonElement(json, "history");
        if (jsonElement != null) {
            RatingHistory[] ratingHistories = JsonUtil.fromJson(jsonElement, RatingHistory[].class);
            if (ratingHistories != null && ratingHistories.length > 0) {
                for (int i = 0; i < ratingHistories.length; i++) {
                    ratingHistories[i].setHandle(userName);
                    ratingHistories[i].setDevelopType(challengeType);
                }
                ratingHistoryDao.insertBatch(ratingHistories);
            }
        }
    }

    /**
     * 保存或更新开发者所有信息
     *
     * @param handle
     */
    public void saveUserMsg(String handle) {
        saveUserBasicInformation(handle);
        saveUserDevelopmentMsg(handle);
        saveUserRatingMsg(handle);
    }

    public void updateUserMsg(String handle) {
        updateUserBasicInformation(handle);
        updateUserDevelopmentMsg(handle);
        saveUserRatingMsg(handle);
    }

    /**
     * 开发者的积分历史信息
     *
     * @param handle
     */
    public void saveUserRatingMsg(String handle) {
        saveUserRatingHistory(handle, "design");
        saveUserRatingHistory(handle, "development");
        saveUserRatingHistory(handle, "specification");
        saveUserRatingHistory(handle, "architecture");
        saveUserRatingHistory(handle, "bug_hunt");
        saveUserRatingHistory(handle, "test_suites");
        saveUserRatingHistory(handle, "ui_prototypes");
        saveUserRatingHistory(handle, "conceptualization");
        saveUserRatingHistory(handle, "ria_build");
        saveUserRatingHistory(handle, "ria_component");
        saveUserRatingHistory(handle, "test_scenarios");
        saveUserRatingHistory(handle, "copilot_posting");
        saveUserRatingHistory(handle, "content_creation");
        saveUserRatingHistory(handle, "first2finish");
        saveUserRatingHistory(handle, "code");
    }
}
