package com.buaa.act.sdp.service.recommend.cbm;

import com.buaa.act.sdp.util.Maths;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/2/23.
 */
@Service
public class ContentBase {

    // 获取相似任务中所有的获胜者handle
    public Set<String> getWinner(List<String> winner, List<Integer> neighbors) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < neighbors.size(); i++) {
            set.add(winner.get(neighbors.get(i)));
        }
        return set;
    }

    // 计算当前任务与其相似任务之间的相似度，并按照相似度排序
    public double[][] getSimilarityTasks(double[][] features, int index, List<Integer> neighborIndex) {
        double[][] similarity = new double[neighborIndex.size()][2];
        for (int i = 0; i < neighborIndex.size(); i++) {
            similarity[i][0] = neighborIndex.get(i);
            similarity[i][1] = Maths.taskSimilariry(features[index], features[neighborIndex.get(i)]);
        }
        Arrays.sort(similarity, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                return Double.compare(o2[1], o1[1]);
            }
        });
        return similarity;
    }

    // 取前20个相似任务,item-based推荐
    public Map<String, Double> getRecommendResult(double[][] features, int index, List<Map<String, Double>> scores, List<String> winner) {
        Map<String, List<Double>> map = new HashMap<>();
        List<Integer> neighborIndex = Maths.getSimilarityChallenges(features, index);
        double[][] similarity = getSimilarityTasks(features, index, neighborIndex);
        Set<String> winnerSet = getWinner(winner, neighborIndex);
        for (int i = 0; i < 20 && i < neighborIndex.size(); i++) {
            for (Map.Entry<String, Double> entry : scores.get((int) similarity[i][0]).entrySet()) {
                if (winnerSet.contains(entry.getKey())) {
                    if (map.containsKey(entry.getKey())) {
                        map.get(entry.getKey()).add(entry.getValue());
                        map.get(entry.getKey()).add(similarity[i][1]);
                    } else {
                        List<Double> list = new ArrayList<>();
                        list.add(entry.getValue());
                        list.add(similarity[i][1]);
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
