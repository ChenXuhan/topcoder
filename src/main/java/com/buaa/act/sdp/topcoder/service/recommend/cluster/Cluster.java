package com.buaa.act.sdp.topcoder.service.recommend.cluster;

import com.buaa.act.sdp.topcoder.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.topcoder.util.Maths;
import com.buaa.act.sdp.topcoder.util.WekaArffUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/13.
 */
@Component
public class Cluster {

    private static final Logger logger = LoggerFactory.getLogger(Cluster.class);

    @Autowired
    private TcBayes tcBayes;

    public SimpleKMeans buildCluster(Instances instances, int position, int clusterNum, Map<Integer, List<Integer>> map) {
        logger.info("building cluster among test data set...");
        SimpleKMeans kMeans = null;
        try {
            kMeans = new SimpleKMeans();
            kMeans.setDistanceFunction(new Distince());
            kMeans.setNumClusters(clusterNum);
            kMeans.buildClusterer(new Instances(instances, 0, position));
            int k;
            for (int i = 0; i < position; i++) {
                k = kMeans.clusterInstance(instances.instance(i));
                if (map.containsKey(k)) {
                    map.get(k).add(i);
                } else {
                    List<Integer> temp = new ArrayList<>();
                    temp.add(i);
                    map.put(k, temp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kMeans;
    }

    /**
     * 先聚类后，使用bayes进行分类
     *
     * @param list
     * @param user
     * @param feature
     * @return
     */
    public Map<String, Double> getClassifierResult(List<Integer> list, List<String> user, double[][] feature) {
        double[][] data = new double[list.size()][feature[0].length];
        List<String> winner = new ArrayList<>(list.size());
        Maths.copy(feature, data, user, winner, list);
        Maths.normalization(data, 5);
        return tcBayes.getRecommendResult(data, list.size() - 1, winner);
    }

    public Map<String, Double> getRecommendResult(double[][] features, int num, List<String> winners, List<Integer> neighbor) {
        logger.info("recommend developers for new a tasks using cluster based classifier");
        int len = features.length;
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<String> user = new ArrayList<>(len);
        for (int i = 0; i < len-1; i++) {
            user.add(winners.get(i));
        }
        user.add("?");
        Instances instances = WekaArffUtil.getClusterInstances(features);
        SimpleKMeans kMeans = buildCluster(instances, len - 1, num, map);
        int k = 0;
        try {
            k = kMeans.clusterInstance(instances.instance(len - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Integer> list = new ArrayList<>();
        list.addAll(map.get(k));
        neighbor.addAll(map.get(k));
        list.add(len - 1);
        return getClassifierResult(list, user, features);
    }

}
