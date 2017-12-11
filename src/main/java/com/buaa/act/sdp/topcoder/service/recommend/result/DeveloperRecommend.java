package com.buaa.act.sdp.topcoder.service.recommend.result;

import com.buaa.act.sdp.topcoder.model.task.TaskItem;
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
    public List<String> recommendDevelopers(TaskItem item) {
        List<String> winners = featureExtract.getWinners(item.getChallengeType());
        double[][] features = featureExtract.getTaskFeatures(item.getChallengeType(), item);
        if (features.length <= 1) {
            return new ArrayList<>();
        }
        int position = 0;
        List<Integer> index = new ArrayList<>();
        List<String> developer = recommendDeveloper(cluster.getRecommendResult(features, 3, winners, index));
        developer = reliability.filter(developer, index, winners, item.getChallengeType());
        developer = competition.refine(developer, winners, position + 1, item.getChallengeType());
        return developer;
    }

    /**
     * 分类结果排序
     *
     * @param map
     * @return
     */
    public List<String> recommendDeveloper(Map<String, Double> map) {
        logger.info("sort the developers according their winning probability");
        List<String> developers = new ArrayList<>();
        if (map == null || map.size() == 0) {
            return developers;
        }
        List<Map.Entry<String, Double>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < list.size(); i++) {
            developers.add(list.get(i).getKey());
        }
        return developers;
    }

    /**
     * 开发者按概率排序
     *
     * @param data
     * @param developers
     * @return
     */
    public List<String> recommendDeveloper(List<Double> data, List<String> developers) {
        Map<String, Double> map = new HashMap<>(developers.size());
        for (int i = 0; i < developers.size(); i++) {
            map.put(developers.get(i), data.get(i));
        }
        return recommendDeveloper(map);
    }
}
