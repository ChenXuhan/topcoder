package com.buaa.act.sdp.service.recommend.classification;

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

    public List<Map<String, Double>> getRecommendResult(Instances instances, int start, Map<Double, String> winners) {
        List<Map<String, Double>> result = new ArrayList<>();
        double index;
        try {
            Class treeClassfier = ClassifierTree.class;
            Method method = treeClassfier.getDeclaredMethod("getProbs", int.class, Instance.class, double.class);
            method.setAccessible(true);
            buildClassifier(new Instances(instances, 0, start));
            for (int i = start; i < instances.numInstances(); i++) {
                Map<String, Double> map = new HashMap<>();
                for (int j = 0; j < instances.numClasses(); j++) {
                    index = j;
                    map.put(winners.get(index), (double)method.invoke(m_root, j, instances.instance(i), 1));
                }
                result.add(map);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
