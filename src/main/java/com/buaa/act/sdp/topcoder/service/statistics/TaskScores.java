package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.TaskRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.TaskSubmissionDao;
import com.buaa.act.sdp.topcoder.model.task.TaskRegistrant;
import com.buaa.act.sdp.topcoder.model.task.TaskSubmission;
import com.buaa.act.sdp.topcoder.service.redis.RedisService;
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
    private static final String TASKS_SCORES = "tasks_scores";
    private static final String TASKS_WINNERS = "tasks_winners";
    private static final String TASK_REGISTER_DATE = "task_register_date";
    private static final String TASK_SUBMIT_DATE = "task_submit_date";

    @Autowired
    private TaskRegistrantDao taskRegistrantDao;
    @Autowired
    private TaskSubmissionDao taskSubmissionDao;
    @Autowired
    private RedisService redisService;

    public Map<Integer, String> getWinners() {
        Map<Integer, String> winners = redisService.getMapCaches(TASKS_WINNERS);
        if (!winners.isEmpty()) {
            return winners;
        }
        getDevelopersScores();
        return redisService.getMapCaches(TASKS_WINNERS);
    }

    public Map<Integer, Map<String, String>> getRegisterDate() {
        Map<Integer, Map<String, String>> registerDate = redisService.getMapCaches(TASK_REGISTER_DATE);
        if (!registerDate.isEmpty()) {
            return registerDate;
        }
        getDevelopersScores();
        return redisService.getMapCaches(TASK_REGISTER_DATE);
    }

    public Map<Integer, Map<String, String>> getSubmitDate() {
        Map<Integer, Map<String, String>> submitDate = redisService.getMapCaches(TASK_SUBMIT_DATE);
        if (!submitDate.isEmpty()) {
            return submitDate;
        }
        getDevelopersScores();
        return redisService.getMapCaches(TASK_SUBMIT_DATE);
    }

    /**
     * 每一个任务的开发者得分
     *
     * @return
     */
    public Map<Integer, Map<String, Double>> getDevelopersScores() {
        Map<Integer, Map<String, Double>> scores = redisService.getMapCaches(TASKS_SCORES);
        if (scores.isEmpty()) {
            synchronized (TASKS_SCORES) {
                scores = redisService.getMapCaches(TASKS_SCORES);
                if (scores.isEmpty()) {
                    logger.info("get all developers' scores on all tasks and save into redis");
                    scores = new HashMap<>();
                    Map<Integer, Map<String, String>> submitDate = new HashMap<>();
                    Map<Integer, Map<String, String>> registerDate = new HashMap<>();
                    Map<Integer, String> winners = new HashMap<>();
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
                    updateDeveloperScores(scores, registerDate, submitDate, winners);
                    redisService.setMapCaches(TASKS_SCORES, scores);
                    redisService.setMapCaches(TASKS_WINNERS, winners);
                    redisService.setMapCaches(TASK_REGISTER_DATE, registerDate);
                    redisService.setMapCaches(TASK_SUBMIT_DATE, submitDate);
                }
            }
        }
        return scores;
    }

    /**
     * 依据submission表更新developer的得分
     */
    private void updateDeveloperScores(Map<Integer, Map<String, Double>> scores, Map<Integer, Map<String, String>> registerDate, Map<Integer, Map<String, String>> submitDate, Map<Integer, String> winners) {
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

    public Map<String, Double> getTaskScore(int taskId) {
        return getDevelopersScores().get(taskId);
    }

    public String getWinner(int taskId) {
        return getWinners().get(taskId);
    }

    public Map<String, String> getRegisterDate(int taskId) {
        return getRegisterDate().get(taskId);
    }

    public Map<String, String> getSubmitDate(int taskId) {
        return getSubmitDate().get(taskId);
    }

    public synchronized void update() {
        logger.info("update cache, get developers scores, every week");
        redisService.delete(TASKS_SCORES);
        redisService.delete(TASKS_WINNERS);
        redisService.delete(TASK_REGISTER_DATE);
        redisService.delete(TASK_SUBMIT_DATE);
        getDevelopersScores();
    }
}
