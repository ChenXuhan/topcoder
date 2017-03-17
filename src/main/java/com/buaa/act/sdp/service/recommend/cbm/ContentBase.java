package com.buaa.act.sdp.service.recommend.cbm;

import com.buaa.act.sdp.util.Maths;
import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/2/23.
 */
@Component
public class ContentBase {

    public Set<String> getWinner(List<String> winner) {
        return new HashSet<>(winner);
    }

    public Map<String, Double> getRecommendResult(double[][] features, int index, List<Map<String, Double>> scores, List<String> winner) {
        Map<String, List<Double>> map = new HashMap<>();
        List<Map.Entry<Integer, Double>> lists = Maths.findNeighbor(index, features);
        Set<String> winners = getWinner(winner);
        for (int i = 0; i < 40; i++) {
            for (Map.Entry<String, Double> entry : scores.get(lists.get(i).getKey()).entrySet()) {
                if (winners.contains(entry.getKey())) {
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
