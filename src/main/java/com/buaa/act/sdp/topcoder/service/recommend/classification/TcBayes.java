package com.buaa.act.sdp.topcoder.service.recommend.classification;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.util.WekaArffUtil;
import org.springframework.stereotype.Component;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/9.
 */
@Component
public class TcBayes extends NaiveBayes {

    /**
     * 按概率对分类结果排序
     *
     * @param features 特征
     * @param position
     * @param winners
     * @return
     */
    public Map<String, Double> getRecommendResult(double[][] features, int position, List<String> winners) {
        Map<Integer, String> winnerIndex = WekaArffUtil.getWinnerIndex(winners);
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
            Instances instances = WekaArffUtil.getInstances(features, winners);
            buildClassifier(new Instances(instances, 0, position));
            double[] dist = distributionForInstance(instances.instance(position));
            if (dist == null) {
                throw new Exception("Null distribution predicted");
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

    /**
     * 找出最大可能性的开发者及其概率：DCW-DS
     *
     * @param features
     * @param position
     * @param workers
     * @return
     */
    public List<Double> getRecommendResultDcwds(List<double[]> features, int position, List<String> workers) {
        List<Double> result = new ArrayList<>(features.size() - position);
        try {
            Map<String, Integer> workerIndex = new HashMap<>();
            int index = 0;
            for (String worker : workers) {
                if (!workerIndex.containsKey(worker)) {
                    workerIndex.put(worker, index++);
                }
            }
            index = features.get(0).length - 2;
            List<String> winners = new ArrayList<>(workers.size());
            double[][] data = new double[features.size()][];
            for (int i = 0; i < features.size(); i++) {
                features.get(i)[index] = workerIndex.get(workers.get(i));
                winners.add("" + features.get(i)[index + 1]);
                data[i] = features.get(i);
            }
            Map<Integer, String> winnerIndex = WekaArffUtil.getWinnerIndex(winners);
            Instances instances = WekaArffUtil.getInstances(data, winners);
            if (winnerIndex.size() == 0) {
                for (int i = 0; i < result.size(); i++) {
                    result.add(0.0);
                }
                return result;
            }
            if (winnerIndex.size() == 1) {
                for (int i = 0; i < result.size(); i++) {
                    result.add(1.0);
                }
                return result;
            }
            buildClassifier(new Instances(instances, 0, position));
            for (int i = 0; i < features.size() - position; i++) {
                double[] dist = distributionForInstance(instances.instance(position + i));
                if (dist == null) {
                    throw new Exception("Null distribution predicted");
                }
                for (int j = 0; j < dist.length; j++) {
                    if (Double.parseDouble(winnerIndex.get(j)) > Constant.SUBMITTER) {
                        result.add(dist[j]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
