package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.stereotype.Service;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import java.util.*;

/**
 * Created by yang on 2017/3/9.
 */
@Service
public class TcBayes extends NaiveBayes {

    private Instances instances;

    // 获取训练数据
    public Instances getInstances(String path, double[][] features, List<String> winners) {
        WekaArffUtil.writeToArffClassfiler(path, features, winners);
        instances = WekaArffUtil.getInstances(path);
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    // weka分类器类别对应的下标
    public Map<Double, String> getWinnerIndex(List<String> winner, int len) {
        Map<Double, String> map = new HashMap<>();
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < len; i++) {
            set.add(winner.get(i));
        }
        int k = 0;
        double index;
        for (String s : set) {
            index = k++;
            map.put(index, s);
        }
        return map;
    }

    // 按概率对分类结果排序
    public Map<String, Double> getRecommendResult(String path, double[][] features, int position, List<String> winners) {

        Map<Double, String> winnerIndex = getWinnerIndex(winners, position);
        Map<String, Double> map = new HashMap<>();
        double index = 0;
        if(winnerIndex.size()==0){
            return map;
        }
        if (winnerIndex.size() == 1) {
            map.put(winnerIndex.get(index), 1.0);
            return map;
        }
        try {
            instances = getInstances(path, features, winners);
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

}
