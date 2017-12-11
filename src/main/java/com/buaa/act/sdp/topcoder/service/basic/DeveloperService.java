package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.dao.DeveloperDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.topcoder.dao.TaskRegistrantDao;
import com.buaa.act.sdp.topcoder.model.developer.*;
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
public class DeveloperService {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperService.class);

    @Autowired
    private DeveloperDao developerDao;
    @Autowired
    private DevelopmentDao developmentDao;
    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;
    @Autowired
    private TaskRegistrantDao taskRegistrantDao;
    @Autowired
    private TaskScores taskScores;

    public Developer getDeveloperByName(String userName) {
        return developerDao.getDeveloperByName(userName);
    }

    public List<Development> getDevelopments(String userName) {
        return developmentDao.getDeveloperDevelopment(userName);
    }

    public List<DevelopmentHistory> getDevelopmentHistory(String userName) {
        return developmentHistoryDao.getDevelopmentHistoryByName(userName);
    }

    public DeveloperInfo getDeveloperInfo(String userName) {
        logger.info("get developer's info from db,userName=" + userName);
        Developer developer = getDeveloperByName(userName);
        if (developer == null) {
            return null;
        }
        List<Development> developments = getDevelopments(userName);
        List<DevelopmentHistory> developmentHistories = getDevelopmentHistory(userName);
        return new DeveloperInfo(developer, developments, developmentHistories);
    }

    public List<Integer> getDeveloperRegistrantTasks(String userName) {
        logger.info("get developer's registered tasks from db,userName=" + userName);
        return taskRegistrantDao.getDeveloperRegistrantTasks(userName);
    }

    public List<Competitor> getDeveloperCompetitors(String userName) {
        logger.info("get developer's most attractive competitors from db, userName=" + userName);
        List<Integer> taskIds = getDeveloperRegistrantTasks(userName);
        Map<Integer, Map<String, Double>> scores = taskScores.getDevelopersScores();
        Map<String, Double> score;
        Map<String, Integer> win = new HashMap<>();
        Map<String, Integer> lose = new HashMap<>();
        Map<String, Integer> total = new HashMap<>();
        double a, b;
        int count;
        for (int taskId : taskIds) {
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
                    count = total.getOrDefault(entry.getKey(), 0);
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
        int competitors = 20;
        List<Competitor> result = new ArrayList<>(competitors);
        for (int i = 0; i < list.size() && i < competitors; i++) {
            Competitor competitor = new Competitor();
            competitor.setName(list.get(i).getKey());
            competitor.setWin(win.getOrDefault(list.get(i).getKey(), 0));
            competitor.setLose(lose.getOrDefault(list.get(i).getKey(), 0));
            competitor.setTotal(total.get(list.get(i).getKey()));
            result.add(competitor);
        }
        return result;
    }
}
