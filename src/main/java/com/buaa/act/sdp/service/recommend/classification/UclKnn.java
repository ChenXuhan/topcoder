package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.service.recommend.FeatureExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fuyang on 2017/3/1.
 */
@Service
public class UclKnn {

    @Autowired
    private FeatureExtract featureExtract;

    // 选择n个最近的排序，取前K个
    public Map<String,Integer> getRecommendWorker(double[][] features, double[] feature, int k,int start, List<String> winners) {
        Map<Integer, Double> map = new HashMap<>(start);
        for (int i = 0; i < start; i++) {
            map.put(i, similarity(features[i], feature));
        }
        List<Map.Entry<Integer, Double>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<String, Integer> sortMap = new HashMap<>();
        String winner;
        for (int i = 0; i<k&&i < start; i++) {
            winner = winners.get(list.get(i).getKey());
            if (sortMap.containsKey(winner)) {
                sortMap.put(winner, sortMap.get(winner)+1);
            } else {
                sortMap.put(winner, 1);
            }
        }
        return sortMap;
    }

    public  List<Map<String,Integer>> getRecommendResult(double[][]features,int k,int start,List<String>winners){
        List<Map<String,Integer>>result=new ArrayList<>();
        for(int i=start;i<features.length;i++){
            result.add(getRecommendWorker(features,features[i],k,start,winners));
        }
        return result;
    }

    //KNN自定义的距离公式
    public double similarity(double[] vectorOne, double[] vectorTwo) {
        BigDecimal bigDecimal = BigDecimal.valueOf(0.0);
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(Math.abs(vectorOne[0] - vectorTwo[0])));
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(Math.abs(vectorOne[1] - vectorTwo[1])));
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(Math.abs(vectorOne[2] - vectorTwo[2])));
        int count = 0, length = featureExtract.getSkills().size(), start = 3;
        for (int i = start; i < length + start; i++) {
            if (Math.abs(vectorOne[i] - vectorTwo[i]) < 0.01) {
                count++;
            }
        }
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(1.0 * count / length));
        start = start + length;
        length = featureExtract.getChallengeRequirementSize();
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(cosSimilarity(vectorOne, vectorTwo, start, start + length)));
        start = start + length;
        length = featureExtract.getTitleWordSize();
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(cosSimilarity(vectorOne, vectorTwo, start, start + length)));
        return bigDecimal.doubleValue();
    }

    //余弦相似度
    public double cosSimilarity(double[] one, double[] two, int start, int end) {
        double sum = 0, a = 0, b = 0;
        for (int i = start; i < end; i++) {
            sum = sum + one[i] * two[i];
            a = one[i] * one[i] + a;
            b = two[i] * two[i] + b;
        }
        return sum / Math.sqrt(a * b);
    }

}
