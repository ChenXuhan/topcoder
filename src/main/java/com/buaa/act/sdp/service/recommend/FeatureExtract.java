package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.util.Maths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

    //过滤掉的所有challenges
    private List<ChallengeItem> items;
    //challenge 对应的winner
    private List<String> winners;
    //challengeId对应的提交人的得分
    private Map<Integer, Map<String, Double>> scores;
    private List<Map<String, Double>> userScore;
    private int requirementWordSize;
    private int titleWordSize;

    private Map<Integer,String>allWinners;

    public FeatureExtract() {
        items = new ArrayList<>();
        winners = new ArrayList<>();
        scores = new HashMap<>();
        userScore = new ArrayList<>();
        requirementWordSize = 0;
        allWinners=new HashMap<>();
    }

    public List<String> getWinners() {
        return winners;
    }

    public List<ChallengeItem> getItems() {
        return items;
    }

    public Map<Integer, Map<String, Double>> getScores() {
        if (scores.size() == 0) {
            getWinnersAndScores(null);
        }
        return scores;
    }

    public int getChallengeRequirementSize() {
        return requirementWordSize;
    }

    public int getTitleWordSize() {
        return titleWordSize;
    }

    public List<Map<String, Double>> getUserScore() {
        return userScore;
    }

    public Map<Integer, String> getAllWinners() {
        return allWinners;
    }

    public void init(String challengeType) {
        getWinnersAndScores(challengeType);
    }

    //一个人对一个challenge提交多次，以最高分数为主
    public void getUserScores(ChallengeSubmission challengeSubmission) {
        Map<String, Double> score;
        if (Double.parseDouble(challengeSubmission.getFinalScore()) < 80) {
            return;
        }
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
                if (filterChallenge(challengeItem, challengeType)) {
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

    //对challenge进行过滤
    public boolean filterChallenge(ChallengeItem challengeItem, String challengeType) {
        if (!challengeItem.getCurrentStatus().equals("Completed")) {
            return false;
        }
        String str = challengeItem.getChallengeType();
        if (!str.equals(challengeType)) {
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
//        if(challengeItem.getTechnology().length == 1&& challengeItem.getTechnology()[0].equals("Other")){
//            return false;
//        }
        return true;
    }

    // 文本分词统计
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
        WordCount requirement = new WordCount(requirements);
        requirement.init(start);
        WordCount title = new WordCount(titles);
        title.init(start);
        WordCount skill = new WordCount(skills);
        skill.init(start);
        wordCounts[0] = requirement;
        wordCounts[1] = title;
        wordCounts[2] = skill;
        return wordCounts;
    }

    // 只获取任务的时间和奖金
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
        Maths.normalization(features, 2);
        return features;
    }

    //UCL中KNN分类器特征
    public double[][] generateVectorUcl() {
        double[][] paymentAndDuration = new double[items.size()][3];
        Set<String> skillSet = getSkills();
        double[][] skills = new double[items.size()][skillSet.size()];
        String[] temp;
        int index;
        List<double[]> requirementTfIdf, titleTfIdf;
        for (int i = 0; i < items.size(); i++) {
            temp = items.get(i).getTechnology();
            Set<String> set = new HashSet<>();
            for (String str : temp) {
                set.add(str.toLowerCase());
            }
            temp = items.get(i).getPlatforms();
            for (String str : temp) {
                set.add(str.toLowerCase());
            }
            index = 0;
            setWorkerSkills(i, index, skills, skillSet, set);
            paymentAndDuration[i][0] = Double.parseDouble(items.get(i).getPrize()[0]);
            paymentAndDuration[i][1] = items.get(i).getDuration();
            temp = items.get(i).getPostingDate().substring(0, 10).split("-");
            paymentAndDuration[i][2] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        }
        int start = (int) (0.9 * winners.size());
        WordCount[] wordCounts = getWordCount(start);
        requirementTfIdf = wordCounts[0].getTfIdf();
        requirementWordSize = wordCounts[0].getWordSize();
        titleTfIdf = wordCounts[1].getTfIdf();
        titleWordSize = wordCounts[1].getWordSize();
        int length = requirementWordSize + titleWordSize + skillSet.size() + 3;
        double[][] features = new double[items.size()][length];
        Maths.normalization(paymentAndDuration, 3);
        for (int i = 0; i < features.length; i++) {
            index = 0;
            features[i][index++] = paymentAndDuration[i][0];
            features[i][index++] = paymentAndDuration[i][1];
            features[i][index++] = paymentAndDuration[i][2];
            for (int j = 0; j < skillSet.size(); j++) {
                features[i][index++] = skills[i][j];
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

    //需求和标题使用的长度,没有处理文本
    public double[][] generateVector() {
        Set<String> set = getSkills();
        double[][] features = new double[items.size()][set.size() + 5];
        ChallengeItem item;
        int index;
        Set<String> skill = new HashSet<>();
        for (int i = 0; i < features.length; i++) {
            item = items.get(i);
            index = 0;
            features[i][index++] = item.getDetailedRequirements().length();
            features[i][index++] = item.getChallengeName().length();
            String[] temp = items.get(i).getPostingDate().substring(0, 10).split("-");
            features[i][index++] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
            features[i][index++] = item.getDuration();
            double award = 0;
            for (String str : item.getPrize()) {
                award += Double.parseDouble(str);
                break;
            }
            features[i][index++] = award;
            skill.clear();
            for (String str : item.getTechnology()) {
                skill.add(str.toLowerCase());
            }
            for (String str : item.getPlatforms()) {
                skill.add(str.toLowerCase());
            }
            setWorkerSkills(i, index, features, set, skill);
        }
        return features;
    }

    // 统计任务中的技能
    public void setWorkerSkills(int k, int index, double[][] features, Set<String> set, Set<String> skill) {
        boolean flag;
        for (String str : set) {
            flag = false;
            for (String strs : skill) {
                if (strs.startsWith(str)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                features[k][index++] = 1.0;
            } else {
                features[k][index++] = 0;
            }
        }
    }

    // 所有技能的集合
    public Set<String> getSkills() {
        Set<String> skills = new HashSet<>();
        for (String str : Constant.TECHNOLOGIES) {
            skills.add(str.toLowerCase());
        }
        for (String str : Constant.PLATFORMS) {
            skills.add(str.toLowerCase());
        }
        return skills;
    }

    //筛选一部分任务后，获取这些challenge的特征向量
    public double[][] getFeatures(String challengeType) {
        if (items.size() == 0) {
            getWinnersAndScores(challengeType);
        }
        double[][] features = generateVector();
        return features;
    }

}
