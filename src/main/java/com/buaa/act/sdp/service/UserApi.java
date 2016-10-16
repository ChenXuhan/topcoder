package com.buaa.act.sdp.service;

import com.buaa.act.sdp.bean.user.*;
import com.buaa.act.sdp.dao.DevelopmentDao;
import com.buaa.act.sdp.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.dao.RatingHistoryDao;
import com.buaa.act.sdp.dao.UserDao;
import com.buaa.act.sdp.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Object object = RequestUtil.request("http://api.topcoder.com/v2/users/" + userName, User.class);
        if (object != null && object instanceof User) {
            User user = (User) object;
            userDao.insert(user);
        }
    }

    public void handStatistics(String userName, Statistics statistics) {
        if (statistics != null) {
            Track track = statistics.getTrack();
            CompetitionHistory competitionHistory = statistics.getCompetitionHistory();
            if (track != null) {
                List<Development> list = track.getAllTypeDevelopments(userName);
                developmentDao.insert(list);
            }
            if (competitionHistory != null) {
                List<DevelopmentHistory> developmentHistories = competitionHistory.getAllDevelopmentHistory(userName);
                developmentHistoryDao.insert(developmentHistories);
            }
        }
    }

    public void getUserStatistics(String userName) {
        Object object = RequestUtil.request("http://api.topcoder.com/v2/users/" + userName + "/statistics/develop", Statistics.class);
        if (object != null && object instanceof Statistics) {
            Statistics statistics = (Statistics) object;
            handStatistics(userName, statistics);
        }
    }

    //"challengeType should be an element of design,development,specification,architecture,bug_hunt,test_suites,assembly,ui_prototypes,conceptualization,ria_build,ria_component,test_scenarios,copilot_posting,content_creation,reporting,marathon_match,first2finish,code,algorithm."
    public void getUserChallengeHistory(String userName, String challengeType) {
        Object object = RequestUtil.request("http://api.topcoder.com/v2/develop/statistics/" + userName + "/" + challengeType, UserRatingHistory.class);
        if (object != null && object instanceof UserRatingHistory) {
            UserRatingHistory userRatingHistory = (UserRatingHistory) object;
            handUserRatingHistory(userName, challengeType, userRatingHistory);
        }
    }

    public void handUserRatingHistory(String userName, String challengeType, UserRatingHistory userRatingHistory) {
        if (userRatingHistory != null) {
            RatingHistory[] histories = userRatingHistory.getHistory();
            if (histories == null) {
                return;
            }
            for (int i = 0; i < histories.length; i++) {
                histories[i].setHandle(userName);
                histories[i].setDevelopType(challengeType);
            }
            ratingHistoryDao.insert(histories);
        }
    }

}
