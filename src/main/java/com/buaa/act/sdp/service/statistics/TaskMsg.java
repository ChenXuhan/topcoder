package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.challenge.ChallengeSubmission;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/5/31.
 */
@Component
public class TaskMsg {

    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private ChallengeItemDao challengeItemDao;
    @Autowired
    private TaskFilter taskFilter;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;

    // 所有任务的获胜者
    private Map<Integer, String> allWinners;
    //challengeId对应的提交人的得分
    private Map<Integer, Map<String, Double>> scores;
    //过滤掉的所有challenges
    private List<ChallengeItem> items;
    //challenge 对应的winner
    private List<String> winners;
    // 选取任务的worker得分情况
    private List<Map<String, Double>> userScore;

    private String type;

    public TaskMsg() {
        items = new ArrayList<>();
        winners = new ArrayList<>();
        scores = new HashMap<>();
        userScore = new ArrayList<>();
        allWinners = new HashMap<>();
    }

    public Map<Integer, String> getAllWinners(String type) {
        if (items.size() == 0 || !type.equals(this.type)) {
            this.type = type;
            getWinnersAndScores(type);
        }
        return allWinners;
    }

    public Map<Integer, Map<String, Double>> getScores(String type) {
        if (items.size() == 0 || !type.equals(this.type)) {
            this.type = type;
            getWinnersAndScores(type);
        }
        return scores;
    }

    public List<ChallengeItem> getItems(String type) {
        if (items.size() == 0 || !type.equals(this.type)) {
            this.type = type;
            getWinnersAndScores(type);
        }
        return items;
    }

    public List<String> getWinners(String type) {
        if (items.size() == 0 || !type.equals(this.type)) {
            this.type = type;
            getWinnersAndScores(type);
        }
        return winners;
    }

    public List<Map<String, Double>> getUserScore(String type) {
        if (items.size() == 0 ) {
            getWinnersAndScores(type);
        }
        return userScore;
    }

    //一个人对一个challenge提交多次，以最高分数为主
    public void getUserScores(ChallengeSubmission challengeSubmission) {
        Map<String, Double> score;
//        if (Double.parseDouble(challengeSubmission.getFinalScore()) < 80) {
//            return;
//        }
        if (scores.containsKey(challengeSubmission.getChallengeID())) {
            score = scores.get(challengeSubmission.getChallengeID());
            if (score.containsKey(challengeSubmission.getHandle()) && score.get(challengeSubmission.getHandle()).doubleValue() >= Double.parseDouble(challengeSubmission.getFinalScore())) {
                return;
            } else {
                score.put(challengeSubmission.getHandle(), Double.parseDouble(challengeSubmission.getFinalScore()));
            }
        } else {
            score = new HashMap<>();
            score.put(challengeSubmission.getHandle(), Double.parseDouble(challengeSubmission.getFinalScore()));
        }
        scores.put(challengeSubmission.getChallengeID(), score);
    }

    // 从所有的任务中进行筛选，过滤出一部分任务，计算winner、tasks，以及开发者所得分数
    public void getWinnersAndScores(String challengeType) {
        List<ChallengeSubmission> list = challengeSubmissionDao.getChallengeWinner();
        Map<String, Integer> map = new HashMap<>();
        Set<Integer> challengeSet = new HashSet<>();
        Map<Integer, String> user = new HashMap<>();
        Set<Integer> set = new HashSet<>();
        ChallengeItem challengeItem;
        List<ChallengeItem> challengeItems = new ArrayList<>();
        for (ChallengeSubmission challengeSubmission : list) {
            if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1") && Double.parseDouble(challengeSubmission.getFinalScore()) >= 80) {
                allWinners.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
            }
            if (set.contains(challengeSubmission.getChallengeID())) {
                continue;
            }
            if (challengeSet.contains(challengeSubmission.getChallengeID())) {
                if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1") && Double.parseDouble(challengeSubmission.getFinalScore()) >= 80) {
                    user.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
                }
                getUserScores(challengeSubmission);
            } else {
                challengeItem = challengeItemDao.getChallengeItemById(challengeSubmission.getChallengeID());
                if (taskFilter.filterChallenge(challengeItem, challengeType)) {
                    challengeSet.add(challengeItem.getChallengeId());
                    challengeItems.add(challengeItem);
                    if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1") && Double.parseDouble(challengeSubmission.getFinalScore()) >= 80) {
                        user.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
                    }
                    getUserScores(challengeSubmission);
                } else {
                    set.add(challengeSubmission.getChallengeID());
                }
            }
        }
        for (Map.Entry<Integer, String> entry : user.entrySet()) {
            if (map.containsKey(entry.getValue())) {
                map.put(entry.getValue(), map.get(entry.getValue()) + 1);
            } else {
                map.put(entry.getValue(), 1);
            }
        }
        for (int i = 0; i < challengeItems.size(); i++) {
            String win = user.get(challengeItems.get(i).getChallengeId());
            if (map.containsKey(win) && map.get(win) >= 5) {
                items.add(challengeItems.get(i));
                winners.add(win);
                userScore.add(scores.get(challengeItems.get(i).getChallengeId()));
            }
        }
        Set<String> sets = new HashSet<>(winners);
        System.out.println(winners.size() + "\t" + sets.size());
    }


}
