package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.user.WorkerDynamicMsg;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/11/12.
 */
@Component
public class DeveloperMsg {

    private Map<Integer, Map<String, WorkerDynamicMsg>> msgMap;

    public DeveloperMsg() {
        msgMap = new HashMap<>();
    }

    /**
     * 计算并保存开发者的动态特征
     *
     * @param scores
     * @param winners
     * @param list
     * @param challengeItem
     * @return
     */
    public Map<String, WorkerDynamicMsg> getDeveloperDynamicMsg(Map<Integer, Map<String, Double>> scores, Map<Integer, String> winners, List<ChallengeItem> list, ChallengeItem challengeItem) {
        Map<String, WorkerDynamicMsg> map = msgMap.get(challengeItem.getChallengeId());
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
        return map;
    }

    public Map<String, WorkerDynamicMsg> getDeveloperDynamicMsg(int challengeId) {
        return msgMap.get(challengeId);
    }

    /**
     * 判断任务是否相似
     *
     * @param one
     * @param two
     * @return
     */
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

    /**
     * 任务的发布时间距离
     *
     * @param one
     * @param two
     * @return
     */
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
