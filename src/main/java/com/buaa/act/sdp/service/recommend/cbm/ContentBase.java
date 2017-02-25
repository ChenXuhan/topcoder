package com.buaa.act.sdp.service.recommend.cbm;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/2/23.
 */
@Component
public class ContentBase {

    public double taskSimilariry(double[] vectorOne, double[] vectorTwo) {
        double num = 0, a = 0, b = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            num += vectorOne[i] * vectorTwo[i];
            a += vectorOne[i] * vectorOne[i];
            b += vectorTwo[i] * vectorTwo[i];
        }
        return num / Math.sqrt(a * b);
    }

    public double taskSimilariry1(double[] vectorOne, double[] vectorTwo) {
        double similarity = 0;
        similarity = similarity + 0.1 * Math.abs((vectorOne[0] - vectorTwo[0]));
        similarity = similarity + 0.1 * Math.abs((vectorOne[1] - vectorTwo[1]));
        similarity = similarity + 0.2 * Math.abs((vectorOne[2] - vectorTwo[2]));
        similarity = similarity + 0.2 * Math.abs((vectorOne[3] - vectorTwo[3]));
        for (int i = 4; i < vectorOne.length; i++) {
            similarity = similarity + 0.4 * Math.abs(vectorOne[i] - vectorTwo[i]);
        }
        return similarity;
    }

    public Map<String,Double> getRecommendWorker(double[] feature,double[][] features,int start,List<Map<String, Double>> scores) {
        Map<String, List<Double>> map = new HashMap<>();
        double similarity;
        for (int i = 0; i < start; i++) {
            if ((similarity = taskSimilariry(feature, features[i])) >= 0.8) {
                for (Map.Entry<String, Double> entry : scores.get(i).entrySet()) {
                    if (map.containsKey(entry.getKey())) {
                        map.get(entry.getKey()).add(entry.getValue());
                        map.get(entry.getKey()).add(similarity);
                    } else {
                        List<Double> list = new ArrayList<>();
                        list.add(entry.getValue());
                        list.add(similarity);
                        map.put(entry.getKey(), list);
                    }
                }
            }
        }
        double score, weight;
        Map<String, Double> workerMap = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : map.entrySet()) {
            List<Double> list = entry.getValue();
            score = 0;
            weight = 0;
            for (int i = 0; i < list.size(); i += 2) {
                score += list.get(i) * list.get(i + 1);
                weight += list.get(i + 1);
            }
            score = score / weight;
            workerMap.put(entry.getKey(), score);
        }
        return workerMap;
    }

    public List<Map<String,Double>>getRecommendResult(double[][] features,int start,List<Map<String, Double>> scores){
        double[]testFeature;
        List<Map<String,Double>>result=new ArrayList<>();
        for(int i=start;i<features.length;i++){
            testFeature=features[i];
            result.add(getRecommendWorker(testFeature,features,start,scores));
        }
        return result;
    }

}
