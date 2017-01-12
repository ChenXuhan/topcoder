package com.buaa.act.sdp.service.cbm;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/1/7.
 */
@Service
public class ContentBased {

    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private ChallengeItemDao challengeItemDao;
    private Map<Integer, Map<String, String>> userSubmissionMap;
    private Map<Integer, ChallengeItem> challengeMap;
    private List<Integer> testChallenges;
    private int maxRequireMentLength;
    private int minRequireMentLength;
    private int maxDuration;
    private int minDuration;
    private double maxReward;
    private double minReward;
    private int challengeCount;

    public List<ChallengeSubmission> init() {
        List<ChallengeSubmission> submission = challengeSubmissionDao.getChallengeAndScore();
        challengeMap = new HashMap<>();
        userSubmissionMap = new HashMap<>();
        testChallenges = new ArrayList<>();
        challengeCount = 1;
        minRequireMentLength = Integer.MAX_VALUE;
        minReward = minRequireMentLength;
        minDuration = minRequireMentLength;
        maxReward = 0;
        maxRequireMentLength = 0;
        maxDuration = maxRequireMentLength;
        for (int i = 1; i < submission.size(); i++) {
            if (submission.get(i).getChallengeID() != submission.get(i - 1).getChallengeID()) {
                challengeCount++;
            }
        }
        return submission;
    }

    public void getUserSubmissionsAndScore() {
        List<ChallengeSubmission> submission = init();
        ChallengeItem item;
        int count = 0;
        for (ChallengeSubmission challengeSubmission : submission) {
            if (!challengeMap.containsKey(challengeSubmission.getChallengeID())) {
                item = challengeItemDao.getChallengeItemById(challengeSubmission.getChallengeID());
                if(!item.getChallengeType().equals("Development")){
                    continue;
                }
                if (count >= challengeCount * 0.8) {
                    testChallenges.add(challengeSubmission.getChallengeID());
                }
                count++;
                findMaxAndMinValue(item);
                challengeMap.put(challengeSubmission.getChallengeID(), item);
            }
            if (userSubmissionMap.containsKey(challengeSubmission.getChallengeID())) {
                userSubmissionMap.get(challengeSubmission.getChallengeID()).put(challengeSubmission.getHandle(), challengeSubmission.getFinalScore());
            } else {
                Map<String, String> temp = new HashMap<>();
                temp.put(challengeSubmission.getHandle(), challengeSubmission.getFinalScore());
                userSubmissionMap.put(challengeSubmission.getChallengeID(), temp);
            }
        }
    }

    public void findMaxAndMinValue(ChallengeItem item) {
        String[] strings = item.getSubmissionEndDate().substring(0, 10).split("-");
        String[] string = item.getPostingDate().substring(0, 10).split("-");
        int num;
        if (strings != null && strings.length > 0 && string != null && string.length > 0) {
            num = (Integer.parseInt(strings[0]) - Integer.parseInt(string[0])) * 365 + (Integer.parseInt(strings[1]) - Integer.parseInt(string[1])) * 30 + (Integer.parseInt(strings[2]) - Integer.parseInt(string[2]));
            if (num > maxDuration) {
                maxDuration = num;
            }
            if (num < minDuration) {
                minDuration = num;
            }
        }
        if (item.getDetailedRequirements() != null) {
            num = item.getDetailedRequirements().length();
            if (num > maxRequireMentLength) {
                maxRequireMentLength = num;
            }
            if (num < minRequireMentLength) {
                minRequireMentLength = num;
            }
        }
        double nums = 0;
        string = item.getPrize();
        if (string != null && string.length > 0 && !string[0].equals("")) {
            nums = Double.parseDouble(string[0]);
            if (nums < minReward) {
                minReward = nums;
            }
            if (nums > maxReward) {
                maxReward = nums;
            }
        }
    }

    public double calculateSimilarity(ChallengeItem one, ChallengeItem two) {
        double[] vectorOne = generateFeatureVector(one);
        double[] vectorTwo = generateFeatureVector(two);
        double num = 0, a = 0, b = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            num += vectorOne[i] * vectorTwo[i];
            a += vectorOne[i] * vectorOne[i];
            b += vectorTwo[i] * vectorTwo[i];
        }
        return num / Math.sqrt(a * b);
    }

    public double[] generateFeatureVector(ChallengeItem item) {
        double[] vector = new double[207];
        double num = minRequireMentLength;
        int k = 0;
        if (item.getDetailedRequirements() != null) {
            num = item.getDetailedRequirements().length();
        }
        vector[k++] = (num - minRequireMentLength) / (maxRequireMentLength - minRequireMentLength);
        String[] strings = null, string = null;
        if (item.getSubmissionEndDate() != null && item.getPostingDate() != null) {
            item.getSubmissionEndDate().substring(0, 10).split("-");
            item.getPostingDate().substring(0, 10).split("-");
        }
        num = minDuration;
        if (strings != null && strings.length > 0 && string != null && string.length > 0) {
            num = (Integer.parseInt(strings[0]) - Integer.parseInt(string[0])) * 365 + (Integer.parseInt(strings[1]) - Integer.parseInt(string[1])) * 30 + (Integer.parseInt(strings[2]) - Integer.parseInt(string[2]));
        }
        vector[k++] = (num - minDuration) / (maxDuration - minDuration);
        num = minReward;
        if (item.getPrize() != null && item.getPrize().length > 0 && !item.getPrize()[0].equals("")) {
            num = Double.parseDouble(item.getPrize()[0]);
        }
        vector[k++] = (num - minReward) / (maxReward - minReward);
        Set<String> set = new HashSet<>();
        if (item.getTechnology() != null && item.getTechnology().length > 0) {
            for (String str : item.getTechnology()) {
                set.add(str);
            }
            for (String str : Constant.TECHNOLOGIES) {
                if (set.contains(str)) {
                    vector[k++] = 1;
                } else {
                    vector[k++] = 0;
                }
            }
        } else {
            for (int i = 0; i < Constant.TECHNOLOGIES.length; i++) {
                vector[k++] = 0;
            }
        }
        set.clear();
        if (item.getPlatforms() != null && item.getPlatforms().length > 0) {
            for (String str : item.getPlatforms()) {
                set.add(str);
            }
            for (String str : Constant.PLATFORMS) {
                if (set.contains(str)) {
                    vector[k++] = 1;
                } else {
                    vector[k++] = 0;
                }
            }
        } else {
            for (String str : Constant.PLATFORMS) {
                vector[k++] = 0;
            }
        }
        return vector;
    }

    public List<String> contentBasedRecomend(ChallengeItem item) {
        if (userSubmissionMap == null) {
            getUserSubmissionsAndScore();
        }
        Map<String, List<Double>> map = new HashMap<>();
        ChallengeItem temp;
        double similarity;
        for (Map.Entry<Integer, Map<String, String>> entry : userSubmissionMap.entrySet()) {
            temp = challengeMap.get(entry.getKey());
            if (temp.getChallengeId() < item.getChallengeId() && temp.getChallengeType().equals(item.getChallengeType())&&(similarity = calculateSimilarity(temp, item)) >= 0.6) {
                for (Map.Entry<String, String> entrys : entry.getValue().entrySet()) {
                    if (map.containsKey(entrys.getKey())) {
                        map.get(entrys.getKey()).add(Double.parseDouble(entrys.getValue()));
                        map.get(entrys.getKey()).add(similarity);
                    } else {
                        List<Double> list = new ArrayList<>();
                        list.add(Double.parseDouble(entrys.getValue()));
                        list.add(similarity);
                        map.put(entrys.getKey(), list);
                    }
                }
            }
        }
        List<Double> list = null;
        double score;
        Map<String, Double> userRank = new HashMap<>();
        for (Map.Entry<String, List<Double>> data : map.entrySet()) {
            list = data.getValue();
            score = 0;
            for (int i = 0; i < list.size(); i += 2) {
                score += list.get(i) * list.get(i + 1);
            }
            userRank.put(data.getKey(), score);
        }
        List<Map.Entry<String, Double>> recommendResult = new ArrayList<>();
        recommendResult.addAll(userRank.entrySet());
        Collections.sort(recommendResult, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                } else if (o2.getValue() - o1.getValue() == 0) {
                    return 0;
                }
                return 1;
            }
        });
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 20 && i < recommendResult.size(); i++) {
            users.add(recommendResult.get(i).getKey());
        }
        return users;
    }

    public void recommendAccurary() {
        if (userSubmissionMap == null) {
            getUserSubmissionsAndScore();
        }
        List<String> result = new ArrayList<>();
        Set<String> set = new HashSet<>();
        Map<String, String> map = new HashMap<>();
        double[] accurary = new double[]{0,0,0,0,0,0};
        int count, min;
        int nums[] = new int[]{1,3,5,10,15,20};
        for (int i = 0; i < testChallenges.size(); i++) {
            result = contentBasedRecomend(challengeMap.get(testChallenges.get(i)));
           set.clear();
            for (int k = 0; k < nums.length; k++) {
                min = Math.min(result.size(), nums[k]);
                for (int j = 0; j < min; j++) {
                    set.add(result.get(j));
                }
                map = userSubmissionMap.get(testChallenges.get(i));
                count = 0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (set.contains(entry.getKey())) {
                        count++;
                    }
                }
                if (result.size() > 0) {
                    accurary[k] += count / min;
                }
            }
        }
        for (int i = 0; i < nums.length; i++) {
            System.out.println(nums[i] + "\t" + accurary[i] / testChallenges.size());
        }
    }
}
