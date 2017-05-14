package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Instance;
import weka.core.Instances;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by yang on 2017/3/9.
 */
@Service
public class TcJ48 extends J48 {

    private Instances instances;

    // 按概率对分类结果排序
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
            Class treeClassfier = ClassifierTree.class;
            Method method = treeClassfier.getDeclaredMethod("getProbs", int.class, Instance.class, double.class);
            method.setAccessible(true);
            instances = WekaArffUtil.getInstances(path, features, winners);
            buildClassifier(new Instances(instances, 0, position));
            for (int j = 0; j < instances.numClasses(); j++) {
                index = j;
                if (winnerIndex.containsKey(index)) {
                    map.put(winnerIndex.get(index), (double) method.invoke(m_root, j, instances.instance(position), 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
