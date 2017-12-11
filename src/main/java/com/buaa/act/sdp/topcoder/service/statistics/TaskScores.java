package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.TaskRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.TaskSubmissionDao;
import com.buaa.act.sdp.topcoder.model.task.TaskRegistrant;
import com.buaa.act.sdp.topcoder.model.task.TaskSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/6/1.
 */
@Component
public class TaskScores {

    private static final Logger logger = LoggerFactory.getLogger(TaskScores.class);

    @Autowired
    private TaskRegistrantDao taskRegistrantDao;
    @Autowired
    private TaskSubmissionDao taskSubmissionDao;

    /**
     * 任务的开发者得分及获胜者信息
     */
    private Map<Integer, Map<String, Double>> scores;
    private Map<Integer, String> winners;
    private Map<Integer, Map<String, String>> registerDate;
    private Map<Integer, Map<String, String>> submitDate;

    public TaskScores() {
        scores = new HashMap<>();
        winners = new HashMap<>();
        registerDate = new HashMap<>();
        submitDate = new HashMap<>();
    }

    public Map<Integer, String> getWinners() {
        if (winners.isEmpty()) {
            getDevelopersScores();
        }
        return winners;
    }

    public Map<Integer, Map<String, String>> getRegisterDate() {
        if (registerDate.isEmpty()) {
            getDevelopersScores();
        }
        return registerDate;
    }

    public Map<Integer, Map<String, String>> getSubmitDate() {
        if (submitDate.isEmpty()) {
            getDevelopersScores();
        }
        return submitDate;
    }

    /**
     * 每一个任务的开发者得分
     *
     * @return
     */
    public synchronized Map<Integer, Map<String, Double>> getDevelopersScores() {
        if (scores.isEmpty()) {
            logger.info("get all developers' scores on all tasks");
            List<TaskRegistrant> taskRegistrants = taskRegistrantDao.getAllTaskRegistrants();
            Map<String, Double> score;
            Map<String, String> time;
            for (TaskRegistrant taskRegistrant : taskRegistrants) {
                score = scores.getOrDefault(taskRegistrant.getChallengeID(), null);
                if (score != null) {
                    score.put(taskRegistrant.getHandle(), 0.0);
                } else {
                    score = new HashMap<>();
                    score.put(taskRegistrant.getHandle(), 0.0);
                    scores.put(taskRegistrant.getChallengeID(), score);
                }

                /**
                 * 记录开发者的注册时间
                 */
                time = registerDate.getOrDefault(taskRegistrant.getChallengeID(), null);
                String date = null;
                if (taskRegistrant.getRegistrationDate() != null) {
                    date = taskRegistrant.getRegistrationDate().substring(0, 10);
                }
                if (time == null) {
                    time = new HashMap<>();
                    time.put(taskRegistrant.getHandle(), date);
                    registerDate.put(taskRegistrant.getChallengeID(), time);
                } else {
                    time.put(taskRegistrant.getHandle(), date);
                }
            }
            updateDeveloperScores();
        }
        return scores;
    }

    /**
     * 依据submission表更新developer的得分
     */
    private void updateDeveloperScores() {
        List<TaskSubmission> list = taskSubmissionDao.getTaskSubmissionMsg();
        Map<String, Double> score;
        Map<String, String> date;
        for (TaskSubmission taskSubmission : list) {
            if (scores.containsKey(taskSubmission.getChallengeID())) {
                score = scores.get(taskSubmission.getChallengeID());
                if (score.containsKey(taskSubmission.getHandle()) && score.get(taskSubmission.getHandle()) >= Double.parseDouble(taskSubmission.getFinalScore())) {
                    continue;
                } else {
                    score.put(taskSubmission.getHandle(), Double.parseDouble(taskSubmission.getFinalScore()));
                }
            } else {
                score = new HashMap<>();
                score.put(taskSubmission.getHandle(), Double.parseDouble(taskSubmission.getFinalScore()));
            }

            scores.put(taskSubmission.getChallengeID(), score);
            if (taskSubmission.getPlacement() != null && taskSubmission.getPlacement().equals("1") && Double.parseDouble(taskSubmission.getFinalScore()) >= 80) {
                winners.put(taskSubmission.getChallengeID(), taskSubmission.getHandle());
            }

            /**
             * 记录开发者提交时间
             */
            date = submitDate.getOrDefault(taskSubmission.getChallengeID(), null);
            String time = null;
            if (taskSubmission.getSubmissionDate() != null) {
                time = taskSubmission.getSubmissionDate().substring(0, 10);
            }
            if (date == null) {
                date = new HashMap<>();
                date.put(taskSubmission.getHandle(), time);
                submitDate.put(taskSubmission.getChallengeID(), date);
            } else {
                date.put(taskSubmission.getHandle(), time);
            }
        }
    }

    public synchronized void update() {
        logger.info("update cache, get developers' scores, every week");
        scores.clear();
        winners.clear();
        registerDate.clear();
        submitDate.clear();
        getDevelopersScores();
    }

    public Map<String, Double> getTaskScore(int taskId) {
        if (scores.isEmpty()) {
            getDevelopersScores();
        }
        return scores.get(taskId);
    }

    public String getWinner(int taskId) {
        if (scores.isEmpty()) {
            getDevelopersScores();
        }
        return winners.get(taskId);
    }

    public Map<String, String> getRegisterDate(int taskId) {
        if (scores.isEmpty()) {
            getDevelopersScores();
        }
        return registerDate.get(taskId);
    }

    public Map<String, String> getSubmitDate(int taskId) {
        if (scores.isEmpty()) {
            getDevelopersScores();
        }
        return submitDate.get(taskId);
    }
}
