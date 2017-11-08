package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.model.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.model.challenge.ChallengeSubmission;
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

    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;

    /**
     * 任务的开发者得分及获胜者信息
     */
    private Map<Integer, Map<String, Double>> scores;
    private Map<Integer, String> winners;
    private Map<Integer, Map<String, Integer>> registerDate;
    private Map<Integer, Map<String, Integer>> submitDate;

    public TaskScores() {
        scores = new HashMap<>();
        winners = new HashMap<>();
        registerDate = new HashMap<>();
        submitDate = new HashMap<>();
    }

    public synchronized Map<Integer, String> getWinners() {
        if (winners.isEmpty()) {
            getAllWorkerScores();
        }
        return winners;
    }

    public synchronized Map<Integer, Map<String, Integer>> getRegisterDate() {
        if (registerDate.isEmpty()) {
            getAllWorkerScores();
        }
        return registerDate;
    }

    public synchronized Map<Integer, Map<String, Integer>> getSubmitDate() {
        if (submitDate.isEmpty()) {
            getAllWorkerScores();
        }
        return submitDate;
    }

    /**
     * 每一个任务的开发者得分
     * @return
     */
    public synchronized Map<Integer, Map<String, Double>> getAllWorkerScores() {
        if (scores.isEmpty()) {
            List<ChallengeRegistrant> challengeRegistrants = challengeRegistrantDao.getAllRegistrant();
            Map<String, Double> score;
            Map<String, Integer> time;
            int date;
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
                String[] temp;
                if (challengeRegistrant.getRegistrationDate() != null && (temp = challengeRegistrant.getRegistrationDate().substring(0, 10).split("-")) != null) {
                    date = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
                } else {
                    date = 0;
                }
                if (time != null) {
                    time.put(challengeRegistrant.getHandle(), date);
                } else {
                    time = new HashMap<>();
                    time.put(challengeRegistrant.getHandle(), date);
                    registerDate.put(challengeRegistrant.getChallengeID(), time);
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
        List<ChallengeSubmission> list = challengeSubmissionDao.getChallengeWinner();
        Map<String, Double> score;
        Map<String, Integer> time;
        int date;
        for (ChallengeSubmission challengeSubmission : list) {
            if (scores.containsKey(challengeSubmission.getChallengeID())) {
                score = scores.get(challengeSubmission.getChallengeID());
                if (score.containsKey(challengeSubmission.getHandle()) && score.get(challengeSubmission.getHandle()).doubleValue() >= Double.parseDouble(challengeSubmission.getFinalScore())) {
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
            time = submitDate.getOrDefault(challengeSubmission.getChallengeID(), null);
            String[] temp;
            if (challengeSubmission.getSubmissionDate()!=null&&(temp= challengeSubmission.getSubmissionDate().substring(0, 10).split("-"))!= null) {
                date = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
            } else {
                date = 0;
            }
            if (time != null) {
                time.put(challengeSubmission.getHandle(), date);
            } else {
                time = new HashMap<>();
                time.put(challengeSubmission.getHandle(), date);
                submitDate.put(challengeSubmission.getChallengeID(), time);
            }
        }
    }
}
