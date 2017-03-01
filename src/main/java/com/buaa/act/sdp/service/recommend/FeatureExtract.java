package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import com.buaa.act.sdp.common.Constant;
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

    @Autowired(required = false)
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired(required = false)
    private ChallengeItemDao challengeItemDao;

    //过滤掉的所有challenges
    private List<ChallengeItem> items;
    //challenge 对应的winner
    private List<String> winners;
    //challengeId对应的提交人的得分
    private Map<Integer, Map<String, Double>> scores;

    private int requirementWordSize;
    private int titleWordSize;

    public FeatureExtract() {
        items = new ArrayList<>();
        winners = new ArrayList<>();
        scores = new HashMap<>();
        requirementWordSize=0;
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

    public int getChallengeRequirementSize(){
        return requirementWordSize;
    }

    public int getTitleWordSize() {
        return titleWordSize;
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

    //对challenge进行过滤
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

    // 文本分词
    public WordCount[] getWordCount(int start) {
        String[] requirements = new String[items.size()];
        String[] skills = new String[items.size()];
        String[] titles = new String[items.size()], temp;
        WordCount[] wordCounts = new WordCount[3];
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
        WordCount requirement = new WordCount();
        requirement.init(requirements, start);
        WordCount title = new WordCount();
        title.init(titles, start);
        WordCount skill = new WordCount();
        skill.init(skills, start);
        wordCounts[0] = requirement;
        wordCounts[1] = title;
        wordCounts[2] = skill;
        return wordCounts;
    }

    public double[][] getTimesAndAward() {
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

    //UCL中KNN分类器特征
    public double[][] generateVectorUcl() {
        double[][] paymentAndDuration = new double[2][items.size()];
        String[] requirements = new String[items.size()];
        String[] title = new String[items.size()];
        double [][] skills = new double[items.size()][Constant.TECHNOLOGIES.length];
        double [][] platforms= new double[items.size()][Constant.PLATFORMS.length];
        String[] temp;
        List<double[]> requirementTfIdf, titleTfIdf;
        for (int i = 0; i < items.size(); i++) {
            requirements[i] = items.get(i).getDetailedRequirements();
            title[i] = items.get(i).getChallengeName();
            temp = items.get(i).getTechnology();
            Set<String>set=new HashSet<>();
            for(String str:temp){
                set.add(str);
            }
            for(int j=0;j<Constant.TECHNOLOGIES.length;j++){
                if(set.contains(Constant.TECHNOLOGIES[j])){
                    skills[i][j]=1.0;
                }else {
                    skills[i][j]=0;
                }
            }
            set.clear();
            temp = items.get(i).getPlatforms();
            for(String str:temp){
                set.add(str);
            }
            for(int j=0;j<Constant.PLATFORMS.length;j++){
                if(set.contains(Constant.PLATFORMS[j])){
                    platforms[i][j]=1.0;
                }else {
                    platforms[i][j]=0;
                }
            }
            paymentAndDuration[0][i] = Double.parseDouble(items.get(i).getPrize()[0]);
            paymentAndDuration[1][i] = items.get(i).getDuration();
        }
        WordCount tfIdf = new WordCount();
        requirementTfIdf = tfIdf.getTfIdf(requirements);
        requirementWordSize=tfIdf.getWordSize();
        tfIdf = new WordCount();
        titleTfIdf = tfIdf.getTfIdf(title);
        titleWordSize=tfIdf.getWordSize();
        int length = requirementWordSize + titleWordSize + Constant.TECHNOLOGIES.length+Constant.PLATFORMS.length+2;
        double[][] features = new double[items.size()][length];
        normalization(paymentAndDuration);
        int index;
        for (int i = 0; i < features.length; i++) {
            index = 0;
            features[i][index++] = paymentAndDuration[0][i];
            features[i][index++] = paymentAndDuration[1][i];
            for (int j = 0; j < Constant.TECHNOLOGIES.length; j++) {
                features[i][index++] = skills[i][j];
            }
            for (int j = 0; j < Constant.PLATFORMS.length; j++) {
                features[i][index++] = platforms[i][j];
            }
            for (int j = 0; j < requirementWordSize; j++) {
                features[i][index++] = requirementTfIdf.get(i)[j];
            }
            for (int j = 0; j < titleWordSize; j++) {
                features[i][index++] = titleTfIdf.get(i)[j];
            }
        }
        return features;
    }

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
       double[][] features = generateVectorUcl();
//        double[][] features = generateVector();
        return features;
    }

    // 向量统一处理[0-1]
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
