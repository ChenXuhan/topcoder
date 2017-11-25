package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeSubmission;
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
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;

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
            getAllWorkerScores();
        }
        return winners;
    }

    public Map<Integer, Map<String, String>> getRegisterDate() {
        if (registerDate.isEmpty()) {
            getAllWorkerScores();
        }
        return registerDate;
    }

    public Map<Integer, Map<String, String>> getSubmitDate() {
        if (submitDate.isEmpty()) {
            getAllWorkerScores();
        }
        return submitDate;
    }

    /**
     * 每一个任务的开发者得分
     *
     * @return
     */
    public synchronized Map<Integer, Map<String, Double>> getAllWorkerScores() {
        if (scores.isEmpty()) {
            logger.info("get all developers' scores on all tasks");
            List<ChallengeRegistrant> challengeRegistrants = challengeRegistrantDao.getAllChallengeRegistrants();
            Map<String, Double> score;
            Map<String, String> time;
            for (ChallengeRegistrant challengeRegistrant : challengeRegistrants) {
                score = scores.getOrDefault(challengeRegistrant.getChallengeID(), null);
                if (score != null) {
                    score.put(challengeRegistrant.getHandle(), 0.0);
                } else {
                    score = new HashMap<>();
                    score.put(challengeRegistrant.getHandle(), 0.0);
                    scores.put(challengeRegistrant.getChallengeID(), score);
                }

                /**
                 * 记录开发者的注册时间
                 */
                time = registerDate.getOrDefault(challengeRegistrant.getChallengeID(), null);
                String date = null;
                if (challengeRegistrant.getRegistrationDate() != null) {
                    date = challengeRegistrant.getRegistrationDate().substring(0, 10);
                }
                if (time == null) {
                    time = new HashMap<>();
                    time.put(challengeRegistrant.getHandle(), date);
                    registerDate.put(challengeRegistrant.getChallengeID(), time);
                } else {
                    time.put(challengeRegistrant.getHandle(), date);
                }
            }
            updateWorkerScores();
        }
        return scores;
    }

    /**
     * 依据submission表更新worker的得分
     */
    private void updateWorkerScores() {
        List<ChallengeSubmission> list = challengeSubmissionDao.getChallengeSubmissionMsg();
        Map<String, Double> score;
        Map<String, String> date;
        for (ChallengeSubmission challengeSubmission : list) {
            if (scores.containsKey(challengeSubmission.getChallengeID())) {
                score = scores.get(challengeSubmission.getChallengeID());
                if (score.containsKey(challengeSubmission.getHandle()) && score.get(challengeSubmission.getHandle()) >= Double.parseDouble(challengeSubmission.getFinalScore())) {
                    continue;
                } else {
                    score.put(challengeSubmission.getHandle(), Double.parseDouble(challengeSubmission.getFinalScore()));
                }
            } else {
                score = new HashMap<>();
                score.put(challengeSubmission.getHandle(), Double.parseDouble(challengeSubmission.getFinalScore()));
            }

            scores.put(challengeSubmission.getChallengeID(), score);
            if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1") && Double.parseDouble(challengeSubmission.getFinalScore()) >= 80) {
                winners.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
            }

            /**
             * 记录开发者提交时间
             */
            date = submitDate.getOrDefault(challengeSubmission.getChallengeID(), null);
            String time = null;
            if (challengeSubmission.getSubmissionDate() != null) {
                time = challengeSubmission.getSubmissionDate().substring(0, 10);
            }
            if (date == null) {
                date = new HashMap<>();
                date.put(challengeSubmission.getHandle(), time);
                submitDate.put(challengeSubmission.getChallengeID(), date);
            } else {
                date.put(challengeSubmission.getHandle(), time);
            }
        }
    }

    public synchronized void update() {
        logger.info("update cache, get developers' scores, every week");
        scores.clear();
        winners.clear();
        registerDate.clear();
        submitDate.clear();
        getAllWorkerScores();
    }

    public Map<String, Double> getTaskScore(int challengeId) {
        if (scores.isEmpty()) {
            getAllWorkerScores();
        }
        return scores.get(challengeId);
    }

    public String getWinner(int challengeId) {
        if (scores.isEmpty()) {
            getAllWorkerScores();
        }
        return winners.get(challengeId);
    }

    public Map<String, String> getRegisterDate(int challengeId) {
        if (scores.isEmpty()) {
            getAllWorkerScores();
        }
        return registerDate.get(challengeId);
    }

    public Map<String, String> getSubmitDate(int challengeId) {
        if (scores.isEmpty()) {
            getAllWorkerScores();
        }
        return submitDate.get(challengeId);
    }
}
