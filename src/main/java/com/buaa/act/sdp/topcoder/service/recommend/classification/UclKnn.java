package com.buaa.act.sdp.topcoder.service.recommend.classification;

import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fuyang on 2017/3/1.
 */
@Component
public class UclKnn {

    private static final Logger logger = LoggerFactory.getLogger(UclKnn.class);

    @Autowired
    private FeatureExtract featureExtract;

    /**
     * 选择n个最近的排序，取前K个
     *
     * @param features
     * @param feature
     * @param k
     * @param start
     * @param winners
     * @return
     */
    public Map<String, Integer> getRecommendWorker(double[][] features, double[] feature, int k, int start, List<String> winners) {
        logger.info("select the top k developers for new task");
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
        for (int i = 0; i < k && i < start; i++) {
            winner = winners.get(list.get(i).getKey());
            if (sortMap.containsKey(winner)) {
                sortMap.put(winner, sortMap.get(winner) + 1);
            } else {
                sortMap.put(winner, 1);
            }
        }
        return sortMap;
    }

    /**
     * 推荐的开发者获胜概率
     *
     * @param features
     * @param k
     * @param start
     * @param winners
     * @return
     */
    public List<Map<String, Integer>> getRecommendResult(double[][] features, int k, int start, List<String> winners) {
        logger.info("recommend developers for new task using knn");
        List<Map<String, Integer>> result = new ArrayList<>();
        for (int i = start; i < features.length; i++) {
            result.add(getRecommendWorker(features, features[i], k, start, winners));
        }
        return result;
    }

    /**
     * KNN自定义的距离公式
     *
     * @param vectorOne
     * @param vectorTwo
     * @return
     */
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
        length = 0;
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(cosSimilarity(vectorOne, vectorTwo, start, start + length)));
        return bigDecimal.doubleValue();
    }

    /**
     * 余弦相似度
     *
     * @param one
     * @param two
     * @param start
     * @param end
     * @return
     */
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
