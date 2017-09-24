package com.buaa.act.sdp.service.recommend.result;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.cluster.Cluster;
import com.buaa.act.sdp.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.service.recommend.feature.Reliability;
import com.buaa.act.sdp.service.recommend.network.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/6/3.
 */
@Service
public class TaskResult {

    @Autowired
    private FeatureExtract featureExtract;
    @Autowired
    private Cluster cluster;
    @Autowired
    private Reliability reliability;
    @Autowired
    private Competition competition;

    public List<String> recommendWorkers(ChallengeItem item) {
        if (item == null) {
            return null;
        }
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
        List<String> worker = recommendWorker(cluster.getRecommendResult(item.getChallengeType(), features, feature, position + 1, 3, winners));
        List<Integer> index = cluster.getNeighbors();
        worker = reliability.filter(worker, index, winners, item.getChallengeType());
        worker = competition.refine(index, worker, winners, position+1,item.getChallengeType());
        return worker;
    }

    //分类结果排序
    public List<String> recommendWorker(Map<String, Double> map) {
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

}
