package com.buaa.act.sdp.service.recommend.cluster;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.service.recommend.classification.TcJ48;
import com.buaa.act.sdp.service.recommend.classification.TcLibSvm;
import com.buaa.act.sdp.util.Maths;
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
    private List<Integer> neighborIndex;
    @Autowired
    private TcBayes tcBayes;
    @Autowired
    private TcJ48 tcJ48;
    @Autowired
    private TcLibSvm tcLibSvm;

    // 获取训练数据集
    public Instances getInstances(String path, double[][] features) {
        WekaArffUtil.writeToArffCluster(path, features);
        instances = WekaArffUtil.getInstances(path);
        return instances;
    }

    public List<Integer> getNeighbors() {
        return neighborIndex;
    }

    public SimpleKMeans buildCluster(int position, int clusterNum) {
        SimpleKMeans kMeans = null;
        map = new HashMap<>();
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

    public Map<String, Double> getRecommendResult(String challengeType, double[][] features, double[] feature, int position, int num, List<String> winners) {
        //选取聚类的数据集
        List<Integer> neighbors = new ArrayList<>(position + 1);
        for (int i = 0; i < position; i++) {
            neighbors.add(i);
        }
        double[][] data = new double[position + 1][features[0].length];
        List<String> user = new ArrayList<>(position + 1);
        Maths.copy(features, data, winners, user, neighbors);
        data[position] = feature;
        user.add("?");
        instances = getInstances(Constant.CLUSTER_DIRECTORY + challengeType, data);
        SimpleKMeans kMeans = buildCluster(position, num);
        int k = 0;
        try {
            k = kMeans.clusterInstance(instances.instance(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = Constant.CLUSTER_DIRECTORY + challengeType + "/" + position;
        // 在聚类中进行分类
        List<Integer> list = new ArrayList<>();
        list.addAll(map.get(k));
        neighborIndex = map.get(k);
        list.add(position);
        return getResult(list, user, data, path);
    }

    // 聚类后，使用bayes进行分类
    public Map<String, Double> getResult(List<Integer> list, List<String> user, double[][] feature, String path) {
        double[][] data = new double[list.size()][feature[0].length];
        List<String> winner = new ArrayList<>(list.size());
        Maths.copy(feature, data, user, winner, list);
//        Maths.normalization(data,5);
        return tcBayes.getRecommendResult(path, data, list.size() - 1, winner);
    }
}
