package com.buaa.act.sdp.topcoder.service.recommend.classification;

import com.buaa.act.sdp.topcoder.util.WekaArffUtil;
import org.springframework.stereotype.Component;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Instance;
import weka.core.Instances;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/9.
 */
@Component
public class TcJ48 extends J48 {

    /**
     * 按概率对分类结果排序
     *
     * @param features
     * @param position
     * @param winners
     * @return
     */
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
            Class treeClassfier = ClassifierTree.class;
            Method method = treeClassfier.getDeclaredMethod("getProbs", int.class, Instance.class, double.class);
            method.setAccessible(true);
            Instances instances = WekaArffUtil.getClassifierInstances(features, winners);
            buildClassifier(new Instances(instances, 0, position));
            for (int j = 0; j < instances.numClasses(); j++) {
                if (winnerIndex.containsKey(j)) {
                    map.put(winnerIndex.get(j), (double) method.invoke(m_root, j, instances.instance(position), 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
