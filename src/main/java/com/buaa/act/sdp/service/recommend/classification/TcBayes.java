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

    public List<Map<String, Double>> getRecommendResult(Instances instances, int start, Map<Double, String> winner) {
        List<Map<String, Double>> result = new ArrayList<>();
        double index;
        try {
            buildClassifier(new Instances(instances, 0, start));
            for (int i = start; i < instances.numInstances(); i++) {
                Map<String, Double> map = new HashMap<>();
                double[] dist = distributionForInstance(instances.instance(i));
                if (dist == null) {
                    throw new Exception("Null distribution predicted");
                }
                for (int j = 0; j < dist.length; j++) {
                    index=j;
                    map.put(winner.get(index), dist[j]);
                }
                result.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
