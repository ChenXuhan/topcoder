package com.buaa.act.sdp.topcoder.service.recommend.classification;

import com.buaa.act.sdp.topcoder.util.WekaArffUtil;
import org.springframework.stereotype.Component;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/9.
 */
@Component
public class TcLibSvm extends LibSVM {

    public Map<String, Double> getRecommendResult(double[][] features, int position, List<String> winners) {
        Map<Integer, String> winnerIndex = WekaArffUtil.getWinnerIndex(winners);
        Map<String, Double> map = new HashMap<>();
        if (winnerIndex.size() == 0) {
            return map;
        }
        if (winnerIndex.size() == 1) {
            map.put(winnerIndex.get(0), 1.0);
            return map;
        }
        try {
            Instances instances = WekaArffUtil.getClassifierInstances(features, winners);
            buildClassifier(new Instances(instances, 0, position));
            double[] dist = distributionForInstance(instances.instance(position));
            if (dist == null) {
                return map;
            }
            for (int j = 0; j < dist.length; j++) {
                if (winnerIndex.containsKey(j)) {
                    map.put(winnerIndex.get(j), dist[j]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
