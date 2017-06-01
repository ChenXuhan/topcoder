package com.buaa.act.sdp.service.recommend.feature;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.service.statistics.TaskMsg;
import com.buaa.act.sdp.service.statistics.TaskScores;
import com.buaa.act.sdp.util.Maths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/2/13.
 */
@Component
public class FeatureExtract {

    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private TaskScores taskScores;

    private int requirementWordSize;
    private int titleWordSize;
    private String type;

    public FeatureExtract() {
        requirementWordSize = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getWinners() {
        return taskMsg.getWinners(type);
    }

    public List<ChallengeItem> getItems() {
        return taskMsg.getItems(type);
    }

//    public Map<Integer, Map<String, Double>> getScores() {
//        return taskMsg.getScores(type);
//    }

    public int getChallengeRequirementSize() {
        return requirementWordSize;
    }

    public int getTitleWordSize() {
        return titleWordSize;
    }

    public List<Map<String, Double>> getUserScore() {
        return taskMsg.getUserScore(type);
    }

    public Map<Integer, String> getAllWinners() {
        return taskMsg.getAllWinners(type);
    }

    public Map<Integer, Map<String, Double>> getAllWorkerScores() {
        List<String> types = new ArrayList<>();
        if (type != null) {
            types.add(type);
        }
        return taskScores.getAllWorkerScores(types);
    }

    // 文本分词统计
    public WordCount[] getWordCount(int start) {
        List<ChallengeItem> items = getItems();
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
    public double[][] getTimesAndAward(String challengeType) {
        setType(challengeType);
        List<ChallengeItem> items = getItems();
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
        List<ChallengeItem> items = getItems();
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
        List<String> winners = getWinners();
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
        List<ChallengeItem> items = getItems();
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
        setType(challengeType);
//        return generateVectorUcl();
        return generateVector();
    }

}
