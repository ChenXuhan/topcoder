package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/13.
 */
@Service
public class Cluster {
    private Instances instances;
    private Map<Integer, List<Integer>> map;
    @Autowired
    private TcBayes tcBayes;

    public Instances getInstances(String path, double[][] features) {
        if (instances == null) {
            WekaArffUtil.writeToArffCluster(path, features);
            instances = WekaArffUtil.getInstances(path);
        }
        return instances;
    }

    public SimpleKMeans buildCluster(int position, int clusterNum) {
        SimpleKMeans kMeans = null;
        map = new HashMap<>();
        try {
            kMeans = new SimpleKMeans();
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

    public Map<String, Double> getRecommendResult(String challengeType, double[][] features, int position, int num, List<String> winners) {
        instances = getInstances(Constant.CLUSTER_DIRECTORY+challengeType, features);
        SimpleKMeans kMeans = buildCluster(position, num);
//        kMeans.setDistanceFunction();
        int k=0;
        try {
            k = kMeans.clusterInstance(instances.instance(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Integer> list = map.get(k);
        double[][] feature = new double[list.size() + 1][features[0].length];
        List<String> winner = new ArrayList<>(list.size() + 1);
        for (int j = 0; j < list.size(); j++) {
            feature[j] = features[list.get(j)];
            winner.add(winners.get(list.get(j)));
        }
        feature[list.size()] = features[position];
        winner.add(winners.get(position));
        return tcBayes.getRecommendResult(Constant.CLUSTER_DIRECTORY+challengeType+"/"+position, feature, list.size(), winner);
    }
}
