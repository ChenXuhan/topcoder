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

    //challengeId对应的提交人的得分
    private Map<Integer, Map<String, Double>> scores;
    private Map<Integer, String> winners;

    public TaskScores() {
        scores = new HashMap<>();
        winners = new HashMap<>();
    }

    // 获取task的获胜者
    public Map<Integer, String> getWinners() {
        if (winners.isEmpty()) {
            getAllWorkerScores();
        }
        return winners;
    }


    // 获得所有开发者的得分
    public Map<Integer, Map<String, Double>> getAllWorkerScores() {
        if (scores.isEmpty()) {
            List<ChallengeRegistrant> challengeRegistrants = challengeRegistrantDao.getAllRegistrant();
            Map<String, Double> score;
            for (ChallengeRegistrant challengeRegistrant : challengeRegistrants) {
                score = scores.getOrDefault(challengeRegistrant.getChallengeID(), null);
                if (score != null) {
                    score.put(challengeRegistrant.getHandle(), 0.0);
                } else {
                    score = new HashMap<>();
                    score.put(challengeRegistrant.getHandle(), 0.0);
                    scores.put(challengeRegistrant.getChallengeID(), score);
                }
            }
            updateWorkerScores();
        }
        return scores;
    }

    // 依据submission表更新worker的得分
    public void updateWorkerScores() {
        List<ChallengeSubmission> list = challengeSubmissionDao.getChallengeWinner();
        for (ChallengeSubmission challengeSubmission : list) {
            Map<String, Double> score;
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
        }
    }
}
