package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.user.WorkerDynamicMsg;
import com.buaa.act.sdp.util.Maths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/9/26.
 */
@Component
public class DynamicMsg {

    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private TaskScores taskScore;

    public List<ChallengeItem> getTasks() {
        List<ChallengeItem> list = new ArrayList<>();
        list.addAll(taskMsg.getItems("Code"));
        list.addAll(taskMsg.getItems("First2Finish"));
        list.addAll(taskMsg.getItems("Assembly Competition"));
        Collections.sort(list, new Comparator<ChallengeItem>() {
            @Override
            public int compare(ChallengeItem o1, ChallengeItem o2) {
                return o1.getChallengeId() - o2.getChallengeId();
            }
        });
        return list;
    }

    public List<List<double[]>> getWorkerDynamicFeature(ChallengeItem challengeItem, List<List<String>> workers) {
        Map<String, WorkerDynamicMsg> map = new HashMap<>();
        List<ChallengeItem> list = getTasks();
        Map<Integer, Map<String, Double>> scores = taskScore.getAllWorkerScores();
        Map<Integer, String> winners = taskScore.getWinners();
        Map<String, Double> score;
        WorkerDynamicMsg msg;
        List<List<double[]>> features = new ArrayList<>();
        for (ChallengeItem item : list) {
            if (item.getChallengeId() >= challengeItem.getChallengeId()) {
                break;
            }
            score = scores.get(item.getChallengeId());
            if (score != null) {
                List<String> worker = new ArrayList<>(score.size());
                List<double[]> feature = new ArrayList<>(score.size());
                for (Map.Entry<String, Double> entry : score.entrySet()) {
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
                    worker.add(entry.getKey());
                    double[] temp = new double[8];
                    generateDynamicFeature(msg,temp);
                    feature.add(temp);
                }
                workers.add(worker);
                features.add(feature);
            }
        }
        return features;
    }

    public void generateDynamicFeature(WorkerDynamicMsg msg,double[]feature){
        feature[0]=1.0*msg.getNumSubTask()/msg.getNumRegTask();
        feature[1]=1.0*msg.getNumSubTaskSimilar()/msg.getNumRegTaskSimilar();
        feature[2]=msg.getScoreTotal()/msg.getNumRegTaskSimilar();
        feature[3]=msg.getNumRegTask();
        feature[4]=msg.getNumRegTaskTDays();
        feature[5]=msg.getNumSubTaskTDays();
        feature[6]=msg.getNumWinTaskTDays();
        feature[7]=msg.getPriceTotal()/msg.getNumRegTaskTDays();
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
        String[] temp = one.getRegistrationStartDate().substring(0, 10).split("-");
        int a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        temp = two.getRegistrationStartDate().substring(0, 10).split("-");
        int b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        similar += (a - b) / 5 / 365;
        temp = one.getSubmissionEndDate().substring(0, 10).split("-");
        a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        temp = two.getSubmissionEndDate().substring(0, 10).split("-");
        b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
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
        String[] temp = one.getPostingDate().substring(0, 10).split("-");
        int a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        temp = two.getPostingDate().substring(0, 10).split("-");
        int b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        return Math.abs(a - b);
    }
}
