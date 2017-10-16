package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.user.WorkerDynamicMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/9/26.
 */
@Component
public class DynamicMsg {

    @Autowired
    private TaskScores taskScore;

    private Map<Integer, Map<String, WorkerDynamicMsg>> msgMap;

    public DynamicMsg() {
        msgMap = new HashMap<>();
    }

    public List<double[]> getWorkerDynamicFeature(List<ChallengeItem> list, ChallengeItem challengeItem, List<String> workers) {
        Map<String, WorkerDynamicMsg> map = msgMap.get(challengeItem.getChallengeId());
        Map<Integer, Map<String, Double>> scores = taskScore.getAllWorkerScores();
        Map<String, Double> score = scores.get(challengeItem.getChallengeId());
        Map<Integer, String> winners = taskScore.getWinners();
        List<double[]> feature = new ArrayList<>();
        if (map == null) {
            map = new HashMap<>();
            WorkerDynamicMsg msg;
            for (ChallengeItem item : list) {
                if (item.getChallengeId() >= challengeItem.getChallengeId()) {
                    break;
                }
                Map<String, Double> temp = scores.get(item.getChallengeId());
                for (Map.Entry<String, Double> entry : temp.entrySet()) {
                    msg = map.getOrDefault(entry.getKey(), null);
                    if (msg == null) {
                        msg = new WorkerDynamicMsg();
                    }
                    msg.addNumRegTask();
                    if (entry.getValue() > 0) {
                        msg.addNumsSubTask();
                    }
                    if (isSimilar(challengeItem, item)) {
                        msg.addNumRegTaskSimilar();
                        if (entry.getValue() > 0) {
                            msg.addNumSubTaskSimilar();
                        }
                        msg.addScoreTotal(entry.getValue());
                    }
                    if (dataDistance(challengeItem, item) <= 30) {
                        msg.addNumRegTaskTDays();
                        if (entry.getValue() > 0) {
                            msg.addNumSubTaskTDays();
                        }
                        if (entry.getKey().equals(winners.get(item.getChallengeId()))) {
                            msg.addNumsWinTaskTDays();
                        }
                        double price = 0;
                        for (String str : item.getPrize()) {
                            price += Double.parseDouble(str);
                        }
                        msg.addPriceTotal(price);
                    }
                    map.put(entry.getKey(), msg);
                }
            }
            msgMap.put(challengeItem.getChallengeId(), map);
        }
        for (Map.Entry<String, Double> entry : score.entrySet()) {
            double[] temp = new double[9];
            if (map.containsKey(entry.getKey())) {
                generateDynamicFeature(map.get(entry.getKey()), temp);
                if (winners.get(challengeItem.getChallengeId()).equals(entry.getKey())) {
                    temp[8] = Constant.WINNER;
                } else if (score.get(entry.getKey()) > 0) {
                    temp[8] = Constant.SUBMITTER;
                } else {
                    temp[8] = Constant.QUITTER;
                }
            }
            feature.add(temp);
            workers.add(entry.getKey());
        }
        return feature;
    }

    public List<double[]> getDynamicFeatures(List<ChallengeItem> list, ChallengeItem item, List<String> worker) {
        Map<String, WorkerDynamicMsg> map = msgMap.get(item.getChallengeId());
        if (map == null) {
            getWorkerDynamicFeature(list, item, new ArrayList<>());
            map = msgMap.get(item.getChallengeId());
        }
        List<double[]> feature = new ArrayList<>(map.size());
        for (Map.Entry<String, WorkerDynamicMsg> entry : map.entrySet()) {
            double[] temp = new double[9];
            generateDynamicFeature(entry.getValue(), temp);
            temp[8] = 0;
            feature.add(temp);
            worker.add(entry.getKey());
        }
        return feature;
    }


    public void generateDynamicFeature(WorkerDynamicMsg msg, double[] feature) {
        feature[0] = msg.getNumRegTask() == 0 ? 0 : 1.0 * msg.getNumSubTask() / msg.getNumRegTask();
        feature[1] = msg.getNumRegTaskSimilar() == 0 ? 0 : 1.0 * msg.getNumSubTaskSimilar() / msg.getNumRegTaskSimilar();
        feature[2] = msg.getNumRegTaskSimilar() == 0 ? 0 : msg.getScoreTotal() / msg.getNumRegTaskSimilar();
        feature[3] = msg.getNumRegTask();
        feature[4] = msg.getNumRegTaskTDays();
        feature[5] = msg.getNumSubTaskTDays();
        feature[6] = msg.getNumWinTaskTDays();
        feature[7] = msg.getNumRegTaskTDays() == 0 ? 0 : msg.getPriceTotal() / msg.getNumRegTaskTDays();
    }

    public boolean isSimilar(ChallengeItem one, ChallengeItem two) {
        if (one.getChallengeType().equals(two.getChallengeType())) {
            return true;
        }
        int count = 0;
        Set<String> skills = new HashSet<>();
        for (String str : one.getTechnology()) {
            skills.add(str.toLowerCase());
        }
        for (String str : two.getTechnology()) {
            if (skills.contains(str.toLowerCase())) {
                count++;
            }
        }
        double similar = 1.0 * count / Math.max(one.getTechnology().length, two.getTechnology().length);
        skills.clear();
        count = 0;
        for (String str : one.getPlatforms()) {
            skills.add(str.toLowerCase());
        }
        for (String str : two.getPlatforms()) {
            if (skills.contains(str.toLowerCase())) {
                count++;
            }
        }
        similar += 1.0 * count / Math.max(one.getPlatforms().length, two.getPlatforms().length);
        String[] temp;
        int a, b;
        if (one.getRegistrationStartDate() != null) {
            temp = one.getRegistrationStartDate().substring(0, 10).split("-");
            a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            a = 0;
        }
        if (two.getRegistrationStartDate() != null) {
            temp = two.getRegistrationStartDate().substring(0, 10).split("-");
            b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            b = 0;
        }
        similar += (a - b) / 5 / 365;
        if (one.getSubmissionEndDate() != null) {
            temp = one.getSubmissionEndDate().substring(0, 10).split("-");
            a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            a = 0;
        }
        if (two.getSubmissionEndDate() != null) {
            temp = two.getSubmissionEndDate().substring(0, 10).split("-");
            b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            b = 0;
        }
        similar += (a - b) / 5 / 365;
        double c = 0, d = 0;
        for (String str : one.getPrize()) {
            c += Double.parseDouble(str);
        }
        for (String str : two.getPrize()) {
            d += Double.parseDouble(str);
        }
        similar += Math.abs(c - d) / (c + d);
        if (similar >= 0.8) {
            return true;
        }
        return false;
    }

    public int dataDistance(ChallengeItem one, ChallengeItem two) {
        String[] temp;
        int a, b;
        if (one.getPostingDate() != null) {
            temp = one.getPostingDate().substring(0, 10).split("-");
            a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            a = 0;
        }
        if (two.getPostingDate() != null) {
            temp = two.getPostingDate().substring(0, 10).split("-");
            b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            b = 0;
        }
        return Math.abs(a - b);
    }

}
