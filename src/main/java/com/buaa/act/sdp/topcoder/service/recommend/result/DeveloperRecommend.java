package com.buaa.act.sdp.topcoder.service.recommend.result;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.cluster.Cluster;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.recommend.feature.Reliability;
import com.buaa.act.sdp.topcoder.service.recommend.network.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/6/3.
 */
@Service
public class DeveloperRecommend {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperRecommend.class);

    @Autowired
    private FeatureExtract featureExtract;
    @Autowired
    private Cluster cluster;
    @Autowired
    private Reliability reliability;
    @Autowired
    private Competition competition;

    /**
     * 为单个任务推荐开发者
     *
     * @param item
     * @return
     */
    public List<String> recommendWorkers(ChallengeItem item) {
        logger.info("recommend developers for a new task,taskId=" + item.getChallengeId());
        double[][] features = featureExtract.getFeatures(item.getChallengeType());
        List<ChallengeItem> items = featureExtract.getItems(item.getChallengeType());
        List<String> winners = featureExtract.getWinners(item.getChallengeType());
        int position = 0;
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).getChallengeId() < item.getChallengeId()) {
                position = i;
                break;
            }
        }
        double[] feature = featureExtract.generateVector(featureExtract.getSkills(), item);
        List<Integer> index = new ArrayList<>();
        List<String> worker = recommendWorker(cluster.getRecommendResult(features, feature, position + 1, 3, winners, index));
        worker = reliability.filter(worker, index, winners, item.getChallengeType());
        worker = competition.refine(worker, winners, position + 1, item.getChallengeType());
        return worker;
    }

    /**
     * 分类结果排序
     *
     * @param map
     * @return
     */
    public List<String> recommendWorker(Map<String, Double> map) {
        logger.info("sort the developers according winning probability");
        List<String> workers = new ArrayList<>();
        List<Map.Entry<String, Double>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < list.size(); i++) {
            workers.add(list.get(i).getKey());
        }
        return workers;
    }

    /**
     * 开发者按概率排序
     *
     * @param data
     * @param workers
     * @return
     */
    public List<String> recommendWorker(List<Double> data, List<String> workers) {
        Map<String, Double> map = new HashMap<>(workers.size());
        for (int i = 0; i < workers.size(); i++) {
            map.put(workers.get(i), data.get(i));
        }
        return recommendWorker(map);
    }
}
