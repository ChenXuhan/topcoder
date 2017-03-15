package com.buaa.act.sdp.service.recommend.classification;

import org.springframework.stereotype.Service;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import java.util.*;

/**
 * Created by yang on 2017/3/9.
 */
@Service
public class TcBayes extends NaiveBayes {

    public Map<String, Double> getRecommendResult(Instances instances, int start, Map<Double, String> winner) {

        double index;
        Map<String, Double> map = new HashMap<>();
        try {
            buildClassifier(new Instances(instances, 0, start));
            double[] dist = distributionForInstance(instances.instance(start));
            if (dist == null) {
                throw new Exception("Null distribution predicted");
            }
            for (int j = 0; j < dist.length; j++) {
                index = j;
                if (winner.containsKey(index)) {
                    map.put(winner.get(index), dist[j]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
