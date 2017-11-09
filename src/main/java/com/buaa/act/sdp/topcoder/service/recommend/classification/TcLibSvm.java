package com.buaa.act.sdp.topcoder.service.recommend.classification;

import com.buaa.act.sdp.topcoder.util.WekaArffUtil;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/9.
 */
@Service
public class TcLibSvm extends LibSVM {

    private Instances instances;

    public Map<String, Double> getRecommendResult(String path, double[][] features, int position, List<String> winners) {
        Map<Double, String> winnerIndex = WekaArffUtil.getWinnerIndex(winners, position);
        Map<String, Double> map = new HashMap<>();
        double index = 0;
        if (winnerIndex.size() == 0) {
            return map;
        }
        if (winnerIndex.size() == 1) {
            map.put(winnerIndex.get(index), 1.0);
            return map;
        }
        try {
            instances = WekaArffUtil.getInstances(path, features, winners);
            buildClassifier(new Instances(instances, 0, position));
            double[] dist = distributionForInstance(instances.instance(position));
            if (dist == null) {
                return map;
            }
            for (int j = 0; j < dist.length; j++) {
                index = j;
                if (winnerIndex.containsKey(index)) {
                    map.put(winnerIndex.get(index), dist[j]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
