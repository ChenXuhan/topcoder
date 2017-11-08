package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.stereotype.Service;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by yang on 2017/3/9.
 */
@Service
public class TcBayes extends NaiveBayes {

    private Instances instances;

    /**
     * 按概率对分类结果排序
     * @param path arff文件路径
     * @param features 特征
     * @param position
     * @param winners
     * @return
     */
    public Map<String, Double> getRecommendResult(String path, double[][] features, int position, List<String> winners) {
        Map<Double, String> winnerIndex = WekaArffUtil.<String>getWinnerIndex(winners, position);
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
                throw new Exception("Null distribution predicted");
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

    /**
     * 找出最大可能性的开发者及其概率：DCW-DS
     * @param path
     * @param features
     * @param position
     * @param workers
     * @return
     */
    public List<Double> getRecommendResult(String path, List<double[]> features, int position, List<String> workers) {
        List<Double> result = new ArrayList<>(features.size()-position);
        try {
            Map<Integer, Double> winnerIndex = new HashMap<>();
            instances = WekaArffUtil.getInstances(path, features, workers, winnerIndex);
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
            for (int i = 0; i < features.size()-position; i++) {
                double[] dist = distributionForInstance(instances.instance(position + i));
                if (dist == null) {
                    throw new Exception("Null distribution predicted");
                }
                for(int j=0;j<dist.length;j++) {
                    if (winnerIndex.get(j)==Constant.WINNER) {
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
