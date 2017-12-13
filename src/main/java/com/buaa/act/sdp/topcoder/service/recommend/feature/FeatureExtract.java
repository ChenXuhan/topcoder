package com.buaa.act.sdp.topcoder.service.recommend.feature;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import com.buaa.act.sdp.topcoder.util.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2017/2/13.
 */
@Component
public class FeatureExtract {

    private static final Logger logger = LoggerFactory.getLogger(FeatureExtract.class);

    @Autowired
    private TaskMsg taskMsg;

    private int requirementWordSize;

    public FeatureExtract() {
        requirementWordSize = 0;
    }

    public List<String> getWinners(String type) {
        return taskMsg.getWinners(type);
    }

    public List<TaskItem> getTaskItems(String type) {
        return taskMsg.getItems(type);
    }

    public int getTaskRequirementSize() {
        return requirementWordSize;
    }

    public List<Map<String, Double>> getDeveloperScore(String type) {
        return taskMsg.getDeveloperScore(type);
    }

    /**
     * 文本分词统计
     *
     * @param start
     * @param type
     * @return
     */
    public WordCount[] getWordCount(int start, String type) {
        logger.info("get the word vector using tf-idf");
        List<TaskItem> items = getTaskItems(type);
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
                stringBuilder.append(s);
                stringBuilder.append(' ');
            }
            temp = items.get(i).getPlatforms();
            for (String s : temp) {
                stringBuilder.append(s);
                stringBuilder.append(' ');
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

    /**
     * 只获取任务的时间和奖金
     *
     * @param taskType
     * @return
     */
    public double[][] getTimesAndAward(String taskType) {
        List<TaskItem> items = getTaskItems(taskType);
        double[][] features = new double[items.size()][2];
        TaskItem item;
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

    public double[] generateVector(Set<String> set, TaskItem item) {
        int index = 0;
        double[] feature = new double[set.size() + 5];
        feature[index++] = item.getDetailedRequirements().length();
        feature[index++] = item.getChallengeName().length();
        String[] temp = item.getPostingDate().substring(0, 10).split("-");
        feature[index++] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        feature[index++] = item.getDuration();
        feature[index++] = Double.parseDouble(item.getPrize()[0]);
        Set<String> skill = new HashSet<>();
        for (String str : item.getTechnology()) {
            skill.add(str.toLowerCase());
        }
        for (String str : item.getPlatforms()) {
            skill.add(str.toLowerCase());
        }
        setDeveloperSkills(index, feature, set, skill);
        return feature;
    }

    /**
     * 某一任务前的所任任务特征向量
     *
     * @param type
     * @return
     */
    public double[][] generateVectors(String type, TaskItem item) {
        List<TaskItem> items = getTaskItems(type);
        Set<String> set = getSkills();
        int k = 0;
        for (int i = 0; i < items.size(); i++) {
            if (item.getChallengeId() <= items.get(i).getChallengeId()) {
                break;
            }
            k++;
        }
        double[][] features = new double[k + 1][];
        for (int i = 0; i < k; i++) {
            features[i] = generateVector(set, items.get(i));
        }
        features[k] = generateVector(set, item);
        return features;
    }

    /**
     * 统计任务中的技能
     *
     * @param index
     * @param feature
     * @param set
     * @param skill
     */
    public void setDeveloperSkills(int index, double[] feature, Set<String> set, Set<String> skill) {
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
                feature[index++] = 1.0;
            } else {
                feature[index++] = 0;
            }
        }
    }

    /**
     * 任务技能集合
     *
     * @return
     */
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

    public double[][] getTaskFeatures(String taskType, TaskItem item) {
        logger.info("generate tasks features vector, taskId=" + item.getChallengeId());
        return generateVectors(taskType, item);
    }

}
