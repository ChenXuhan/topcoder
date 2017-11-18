package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.topcoder.dao.UserDao;
import com.buaa.act.sdp.topcoder.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yang on 2017/11/18.
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private DevelopmentDao developmentDao;
    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;

    public User getUserByName(String userName) {
        return userDao.getUserByName(userName);
    }

    public List<Development> getDevelopments(String userName) {
        return developmentDao.getDeveloperDevelopment(userName);
    }

    public List<DevelopmentHistory> getDevelopmentHistory(String userName) {
        return developmentHistoryDao.getDevelopmentHistoryByHandle(userName);
    }

    public UserInfo getDeveloperInfo(String userName) {
        User user = getUserByName(userName);
        if (user == null) {
            return null;
        }
        List<Development> developments = getDevelopments(userName);
        List<DevelopmentHistory> developmentHistories = getDevelopmentHistory(userName);
        return new UserInfo(user, developments, developmentHistories);
    }

    public List<Integer> getUserRegistrantTasks(String userName){
        return challengeRegistrantDao.getUserRegistrantTasks(userName);
    }
}
