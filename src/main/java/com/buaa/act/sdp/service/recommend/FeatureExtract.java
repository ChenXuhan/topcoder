package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/2/13.
 */
@Component
public class FeatureExtract {

    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private ChallengeItemDao challengeItemDao;

    private List<ChallengeItem> items;
    private List<String> winners;
    private Map<Integer, Map<String, Double>> scores;

    public FeatureExtract() {
        items = new ArrayList<>();
        winners = new ArrayList<>();
        scores = new HashMap<>();
    }

    public List<String> getWinners() {
        return winners;
    }

    public List<ChallengeItem> getItems() {
        return items;
    }

    public Map<Integer, Map<String, Double>> getScores() {
        return scores;
    }

    //一个人对一个challenge提交多次，以最高分数为主
    public void getUserScores(ChallengeSubmission challengeSubmission) {
        Map<String, Double> score;
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

    public void getWinnersAndScores() {
        List<ChallengeSubmission> list = challengeSubmissionDao.getChallengeWinner();
        Map<String, Integer> map = new HashMap<>();
        Set<Integer> challengeSet = new HashSet<>();
        Map<Integer, String> user = new HashMap<>();
        Set<Integer> set = new HashSet<>();
        ChallengeItem challengeItem;
        List<ChallengeItem> challengeItems = new ArrayList<>();
        for (ChallengeSubmission challengeSubmission : list) {
            if (set.contains(challengeSubmission.getChallengeID())) {
                continue;
            }
            if (challengeSet.contains(challengeSubmission.getChallengeID())) {
                if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1")) {
                    user.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
                }
                getUserScores(challengeSubmission);
            } else {
                challengeItem = challengeItemDao.getChallengeItemById(challengeSubmission.getChallengeID());
                if (filterChallenge(challengeItem)) {
                    challengeSet.add(challengeItem.getChallengeId());
                    challengeItems.add(challengeItem);
                    user.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
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
        Set<String> mySet = new HashSet<>();
        for (int i = 0; i < challengeItems.size(); i++) {
            String win = user.get(challengeItems.get(i).getChallengeId());
            if (map.get(win) >= 5) {
                items.add(challengeItems.get(i));
                winners.add(win);
                mySet.add(win);
            }
        }
        System.out.println(items.size() + "\t" + mySet.size());
    }

    public boolean filterChallenge(ChallengeItem challengeItem) {
        if (!challengeItem.getCurrentStatus().equals("Completed")) {
            return false;
        }
        String str = challengeItem.getChallengeType();
        if (!str.equals("Development")) {
            return false;
        }
        if (challengeItem.getDetailedRequirements() == null || challengeItem.getDetailedRequirements().length() == 0) {
            return false;
        }
        if (challengeItem.getTechnology() == null || challengeItem.getTechnology().length == 0 || challengeItem.getTechnology()[0].isEmpty()) {
            return false;
        }
        if (challengeItem.getChallengeName() == null || challengeItem.getChallengeName().length() == 0) {
            return false;
        }
        if (challengeItem.getDuration() == 0) {
            return false;
        }
        if (challengeItem.getPrize() == null || challengeItem.getPrize().length == 0 || challengeItem.getPrize()[0].isEmpty()) {
            return false;
        }
        return true;
    }

    public WordCount[] getWordCount(int start){
        String[] requirements = new String[items.size()];
        String[] skills = new String[items.size()];
        String[] titles = new String[items.size()],temp;
        WordCount []wordCounts=new WordCount[3];
        for (int i = 0; i < items.size(); i++) {
            requirements[i] = items.get(i).getDetailedRequirements();
            titles[i] = items.get(i).getChallengeName();
            temp = items.get(i).getTechnology();
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : temp) {
                stringBuilder.append(s + ' ');
            }
            temp = items.get(i).getPlatforms();
            for (String s : temp) {
                stringBuilder.append(s + ' ');
            }
            skills[i] = stringBuilder.toString();
        }
        WordCount requirement=new WordCount();
        requirement.init(requirements,start);
        WordCount title=new WordCount();
        title.init(titles,start);
        WordCount skill=new WordCount();
        skill.init(skills,start);
        wordCounts[0]=requirement;
        wordCounts[1]=title;
        wordCounts[2]=skill;
        return wordCounts;
    }

    public double[][]getTimesAndAward(){
        double[][] features = new double[items.size()][2];
        ChallengeItem item;
        int index;
        for (int i = 0; i < features.length; i++) {
            item = items.get(i);
            index = 0;
            features[i][index++] = item.getDuration();
            features[i][index++] = Double.parseDouble(item.getPrize()[0]);
        }
        normalization(features);
        return features;
    }

//    public double[][] generateFeatures(List<ChallengeItem> challengeItems) {
//        double[] payment = new double[challengeItems.size()], duration = new double[challengeItems.size()];
//        String[] requirements = new String[challengeItems.size()];
//        String[] title = new String[challengeItems.size()];
//        String[] skills = new String[challengeItems.size()];
//        String[] temp;
//        StringBuilder stringBuilder;
//        List<double[]> requirementTfIdf, titleTfIdf, skillTfIdf;
//        for (int i = 0; i < challengeItems.size(); i++) {
//            challengeIds[i] = challengeItems.get(i).getChallengeId();
//            requirements[i] = challengeItems.get(i).getDetailedRequirements();
//            title[i] = challengeItems.get(i).getChallengeName();
//            temp = challengeItems.get(i).getTechnology();
//            stringBuilder = new StringBuilder();
//            for (String s : temp) {
//                stringBuilder.append(s + ' ');
//            }
//            skills[i] = stringBuilder.toString();
//            temp = challengeItems.get(i).getPrize();
//            if (temp != null && temp.length > 0 && !temp[0].equals("")) {
//                payment[i] = Double.parseDouble(temp[0]);
//            }
//            duration[i] = challengeItems.get(i).getDuration();
//        }
//        WordCount tfIdf = new WordCount();
//        requirementTfIdf = tfIdf.getTfIdf(challengeIds, requirements);
//        tfIdf = new WordCount();
//        titleTfIdf = tfIdf.getTfIdf(challengeIds, title);
//        tfIdf = new WordCount();
//        skillTfIdf = tfIdf.getTfIdf(challengeIds, skills);
//        int length = requirementTfIdf.get(0).length + titleTfIdf.get(0).length + skillTfIdf.get(0).length + 2, k;
//        double[][] features = new double[challengeItems.size()][length];
//        for (int i = 0; i < features.length; i++) {
//            k = 0;
//            for (int j = 0; j < requirementTfIdf.get(i).length; j++) {
//                features[i][k++] = requirementTfIdf.get(i)[j];
//            }
//            for (int j = 0; j < titleTfIdf.get(i).length; j++) {
//                features[i][k++] = titleTfIdf.get(i)[j];
//            }
//            for (int j = 0; j < skillTfIdf.get(i).length; j++) {
//                features[i][k++] = skillTfIdf.get(i)[j];
//            }
//            features[i][k++] = duration[i];
//            features[i][k++] = payment[i];
//        }
//        normalization(features);
//        return features;
//    }


    public double[][] generateVector() {
        Set<String> set = new HashSet<>();
        String[] strings;
        for (int i = 0; i < items.size(); i++) {
            strings = items.get(i).getTechnology();
            for (String str : strings) {
                set.add(str.toLowerCase());
            }
            strings = items.get(i).getPlatforms();
            for (String str : strings) {
                set.add(str.toLowerCase());
            }
        }
        double[][] features = new double[items.size()][set.size() + 4];
        ChallengeItem item;
        int index;
        Set<String> skill = new HashSet<>();
        for (int i = 0; i < features.length; i++) {
            item = items.get(i);
            index = 0;
            features[i][index++] = item.getDetailedRequirements().length();
            features[i][index++] = item.getChallengeName().length();
            features[i][index++] = item.getDuration();
            features[i][index++] = Double.parseDouble(item.getPrize()[0]);
            skill.clear();
            for (String str : item.getTechnology()) {
                skill.add(str.toLowerCase());
            }
            for (String str : item.getPlatforms()) {
                skill.add(str.toLowerCase());
            }
            for (String str : set) {
                if (skill.contains(str)) {
                    features[i][index++] = 1;
                } else {
                    features[i][index++] = 0;
                }
            }
        }
        normalization(features);
        return features;
    }

    public double[][] getFeatures() {
        if (items.size() == 0) {
            getWinnersAndScores();
        }
//       double[][] features = generateFeature(items);
        double[][] features = generateVector();
        return features;
    }

    public void normalization(double[][] features) {
        double max, min;
        for (int i = 0; i < features[0].length; i++) {
            max = 0;
            min = Integer.MAX_VALUE;
            for (int j = 0; j < features.length; j++) {
                if (features[j][i] > max) {
                    max = features[j][i];
                }
                if (features[j][i] < min) {
                    min = features[j][i];
                }
            }
            for (int j = 0; j < features.length; j++) {
                features[j][i] = (features[j][i] - min) / (max - min);
            }
        }
    }
}
