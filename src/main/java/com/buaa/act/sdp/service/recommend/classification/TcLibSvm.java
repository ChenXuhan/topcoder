package com.buaa.act.sdp.service.recommend.classification;

import weka.classifiers.functions.LibSVM;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Created by yang on 2017/3/9.
 */
public class TcLibSvm extends LibSVM {

    public List<Map<String, Double>> getRecommendResult(Instances instances, int start, Map<Double, String> winners) {
        List<Map<String, Double>> result = new ArrayList<>();
        double index;
        try {
            buildClassifier(new Instances(instances, 0, start));
            for (int i = start; i < instances.numInstances(); i++) {
                Map<String, Double> map = new HashMap<>();
                double[] dist = distributionForInstance(instances.instance(i));
                for (int j = 0; j < dist.length; j++) {
                    index=j;
                    map.put(winners.get(index), dist[j]);
                }
                result.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
