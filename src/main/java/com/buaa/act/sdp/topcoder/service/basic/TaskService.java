package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.user.Registrant;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/11/17.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private ChallengeItemDao challengeItemDao;
    @Autowired
    private TaskScores taskScores;

    public ChallengeItem getChallengeById(int challengeId) {
        logger.info("get task's detail msg from db,taskId=" + challengeId);
        return challengeItemDao.getChallengeItemById(challengeId);
    }

    public boolean projectExist(int projectId) {
        if (projectId <= 0) {
            return false;
        }
        int num = challengeItemDao.projectExist(projectId);
        return num > 0 ? true : false;
    }

    public List<Integer> getProjectTasks(int projectId) {
        logger.info("query tasks in a project in db,projectId=" + projectId);
        if (projectId <= 0) {
            return new ArrayList<>();
        }
        return challengeItemDao.getProjectTasks(projectId);
    }

    public List<Integer> getAllTasks() {
        logger.info("get all tasks' id from db");
        return challengeItemDao.getChallengeIds();
    }

    public List<Registrant> getTaskRegistrants(int challengeId) {
        logger.info("get task's registrants from db,taskId=" + challengeId);
        Map<String, Double> score = taskScores.getTaskScore(challengeId);
        Map<String, String> registerTime = taskScores.getRegisterDate(challengeId);
        Map<String, String> submitTime = taskScores.getSubmitDate(challengeId);
        String winner = taskScores.getWinner(challengeId);
        List<Registrant> registrants = new ArrayList<>(score.size());
        for (Map.Entry<String, Double> entry : score.entrySet()) {
            Registrant registrant = new Registrant(entry.getKey(), registerTime.get(entry.getKey()), submitTime.get(entry.getKey()), entry.getValue(), entry.getKey().equals(winner));
            registrants.add(registrant);
        }
        return registrants;
    }
}
