package com.buaa.act.sdp.topcoder.service.recommend.feature;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.statistics.DynamicMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yang on 2017/9/26.
 */
@Component
public class TaskWorkerAttribute {

    private static final Logger logger = LoggerFactory.getLogger(TaskWorkerAttribute.class);

    @Autowired
    private DynamicMsg dynamicMsg;

    /**
     * 某一个任务前的所有任务的特征向量
     *
     * @param features
     * @param workers
     * @param item
     * @param items
     */
    public void getFeatures(List<double[]> features, List<String> workers, ChallengeItem item, List<ChallengeItem> items) {
        logger.info("get the training tasks' feature vectors before the test task,taskId=" + item.getChallengeId());
        ChallengeItem challenge;
        for (int i = 0; i < items.size(); i++) {
            challenge = items.get(i);
            if (challenge.getChallengeId() < item.getChallengeId()) {
                List<String> worker = new ArrayList<>();
                List<double[]> feature = generateFeatures(challenge, items, worker);
                features.addAll(feature);
                workers.addAll(worker);
            } else if (item.getChallengeId() == challenge.getChallengeId()) {
                break;
            }
        }
    }

    /**
     * 提取任务的静态特征
     *
     * @param set
     * @param item
     * @param feature
     */
    public void generateStaticFeature(Set<String> set, ChallengeItem item, double[] feature) {
        int index = 0;
        feature[index++] = item.getDetailedRequirements().length();
        feature[index++] = item.getChallengeName().length();
        String[] temp;
        if (item.getRegistrationStartDate() != null) {
            temp = item.getRegistrationStartDate().substring(0, 10).split("-");
            feature[index++] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            feature[index++] = 0;
        }
        if (item.getSubmissionEndDate() != null) {
            temp = item.getSubmissionEndDate().substring(0, 10).split("-");
            feature[index++] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            feature[index++] = 0;
        }
        double award = 0;
        for (String str : item.getPrize()) {
            award += Double.parseDouble(str);
        }
        feature[index++] = award;
        feature[index++] = type(item.getChallengeType());
        Set<String> skill = new HashSet<>();
        for (String str : item.getTechnology()) {
            skill.add(str.toLowerCase());
        }
        for (String str : item.getPlatforms()) {
            skill.add(str.toLowerCase());
        }
        setWorkerSkills(index, feature, set, skill);
    }

    /**
     * 增加动态特征到特征向量
     *
     * @param index
     * @param feature 特征向量
     * @param dynamic 动态特征
     */
    public void generateDynamicFeature(int index, double[] feature, double[] dynamic) {
        System.arraycopy(dynamic, 0, feature, index, 10);
    }

    /**
     * 某一个任务所有注册者的特征向量
     *
     * @param item    当前任务
     * @param items   所有任务
     * @param workers 开发者
     * @return
     */
    public List<double[]> generateFeatures(ChallengeItem item, List<ChallengeItem> items, List<String> workers) {
        logger.info("get the new task's features and developers' dynamic feature vector,taskId=" + item.getChallengeId());
        Set<String> set = getAllSkills();
        List<double[]> dynamicFeatures = dynamicMsg.getWorkerDynamicFeature(items, item, workers);
        double[] feature = new double[set.size() + 6];
        generateStaticFeature(set, item, feature);
        List<double[]> features = new ArrayList<>(dynamicFeatures.size());
        for (int i = 0; i < dynamicFeatures.size(); i++) {
            double[] temp = new double[set.size() + 16];
            System.arraycopy(feature, 0, temp, 0, set.size() + 6);
            generateDynamicFeature(set.size() + 6, temp, dynamicFeatures.get(i));
            features.add(temp);
        }
        return features;
    }

    public Set<String> getAllSkills() {
        Set<String> skills = new HashSet<>();
        for (String str : Constant.TECHNOLOGIES) {
            skills.add(str.toLowerCase());
        }
        for (String str : Constant.PLATFORMS) {
            skills.add(str.toLowerCase());
        }
        return skills;
    }

    public void setWorkerSkills(int index, double[] feature, Set<String> skills, Set<String> skill) {
        for (String str : skills) {
            if (skill.contains(str)) {
                feature[index++] = 1.0;
            } else {
                feature[index++] = 0;
            }
        }
    }

    public int type(String type) {
        if ("Code".equals(type)) {
            return 1;
        } else if ("First2Finish".equals(type)) {
            return 2;
        }
        return 3;
    }

}
