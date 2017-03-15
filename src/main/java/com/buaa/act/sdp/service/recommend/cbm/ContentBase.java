package com.buaa.act.sdp.service.recommend.cbm;

import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.stereotype.Component;

import java.util.*;

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
        for (int i = 0; i < vectorOne.length; i++) {
            similarity += (vectorOne[i] - vectorTwo[i]) * (vectorOne[i] - vectorTwo[i]);
        }
        return similarity;
    }

    public List<Map.Entry<Integer, Double>> findNeighbor(int index, double[][] features) {
        Map<Integer, Double> similarity = new HashMap<>();
        for (int i = 0; i < index; i++) {
            similarity.put(i, taskSimilariry1(features[index], features[i]));
        }
        List<Map.Entry<Integer, Double>> lists = new ArrayList<>();
        lists.addAll(similarity.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return lists;
    }

    public List<String> getNeighbors(double[][] features, int index, List<String> winner, int k) {
        List<Map.Entry<Integer, Double>> lists = findNeighbor(index, features);
        double[][] vector = new double[k + 1][features[index].length];
        List<String> list = new ArrayList<>(k + 1);
        for (int i = 0; i < k; i++) {
            vector[i] = features[lists.get(i).getKey()];
            list.add(winner.get(lists.get(i).getKey()));
        }
        vector[k] = features[index];
        list.add(winner.get(index));
        WekaArffUtil.writeToArffClassfiler(String.valueOf(index), vector, list);
        return list;
    }

    public Map<String, Double> getRecommendResult(double[][] features, int index, List<Map<String, Double>> scores, Set<String> winner) {
        Map<String, List<Double>> map = new HashMap<>();
        List<Map.Entry<Integer, Double>> lists = findNeighbor(index, features);
        for (int i = 0; i < index; i++) {
            for (Map.Entry<String, Double> entry : scores.get(lists.get(i).getKey()).entrySet()) {
                if (winner.contains(entry.getKey())) {
                    if (map.containsKey(entry.getKey())) {
                        map.get(entry.getKey()).add(entry.getValue());
                        map.get(entry.getKey()).add(lists.get(i).getValue());
                    } else {
                        List<Double> list = new ArrayList<>();
                        list.add(entry.getValue());
                        list.add(lists.get(i).getValue());
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

}
