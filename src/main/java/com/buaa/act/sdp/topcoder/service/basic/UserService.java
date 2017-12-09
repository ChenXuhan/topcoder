package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.topcoder.dao.UserDao;
import com.buaa.act.sdp.topcoder.model.user.*;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/11/18.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private DevelopmentDao developmentDao;
    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private TaskScores taskScores;

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
        logger.info("get developer's info from db,userName=" + userName);
        User user = getUserByName(userName);
        if (user == null) {
            return null;
        }
        List<Development> developments = getDevelopments(userName);
        List<DevelopmentHistory> developmentHistories = getDevelopmentHistory(userName);
        return new UserInfo(user, developments, developmentHistories);
    }

    public List<Integer> getUserRegistrantTasks(String userName) {
        logger.info("get developer's registered tasks from db,userName=" + userName);
        return challengeRegistrantDao.getUserRegistrantTasks(userName);
    }

    public List<Competitor> getUserCompetitors(String userName) {
        logger.info("get developer's most attractive competitors from db, userName=" + userName);
        List<Integer> tasks = getUserRegistrantTasks(userName);
        Map<Integer, Map<String, Double>> scores = taskScores.getAllWorkerScores();
        Map<String, Double> score;
        Map<String, Integer> win = new HashMap<>();
        Map<String, Integer> lose = new HashMap<>();
        Map<String, Integer> total = new HashMap<>();
        double a, b;
        int count;
        for (int taskId : tasks) {
            score = scores.get(taskId);
            if (score != null && score.containsKey(userName)) {
                a = score.get(userName);
                for (Map.Entry<String, Double> entry : score.entrySet()) {
                    if (entry.getKey().equals(userName)) {
                        continue;
                    }
                    b = entry.getValue();
                    if (a >= b) {
                        count = win.getOrDefault(entry.getKey(), 0);
                        win.put(entry.getKey(), count + 1);
                    } else {
                        count = lose.getOrDefault(entry.getKey(), 0);
                        lose.put(entry.getKey(), count + 1);
                    }
                    count = total.get(entry.getKey());
                    total.put(entry.getKey(), count + 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(total.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        List<Competitor> result = new ArrayList<>(10);
        for (int i = 0; i < list.size() && i < 10; i++) {
            Competitor competitor = new Competitor();
            competitor.setName(list.get(i).getKey());
            competitor.setWin(win.get(list.get(i).getKey()));
            competitor.setWin(lose.get(list.get(i).getKey()));
            result.add(competitor);
        }
        return result;
    }
}
