package com.buaa.act.sdp.topcoder.util;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Created by yang on 2016/11/24.
 */
public class WekaArffUtil {

    /**
     * 分类构造Instances
     *
     * @param features
     * @param winners
     * @return
     */
    public static Instances getClassifierInstances(double[][] features, List<String> winners) {
        int len = 0;
        if (features.length > 0) {
            len = features[0].length + 1;
        }
        ArrayList<Attribute> attributes = new ArrayList<>(len);
        for (int i = 0; i < len - 1; i++) {
            attributes.add(new Attribute("" + i));
        }
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < winners.size(); i++) {
            set.add(winners.get(i));
        }
        ArrayList<String> types = new ArrayList<>(set);
        attributes.add(new Attribute("class", types));
        Instances instances = new Instances("features", attributes, features.length);
        instances.setClassIndex(len - 1);
        for (int i = 0; i < features.length; i++) {
            Instance instance = new DenseInstance(len);
            instance.setDataset(instances);
            for (int j = 0; j < len - 1; j++) {
                instance.setValue(j, features[i][j]);
            }
            instance.setValue(len - 1, winners.get(i));
            instances.add(instance);
        }
        return instances;
    }

    /**
     * 聚类构造Instances
     *
     * @param features
     * @return
     */
    public static Instances getClusterInstances(double[][] features) {
        int len = 0;
        if (features.length > 0) {
            len = features[0].length;
        }
        ArrayList<Attribute> attributes = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            attributes.add(new Attribute("" + i));
        }
        Instances instances = new Instances("features", attributes, features.length);
        for (int i = 0; i < features.length; i++) {
            Instance instance = new DenseInstance(len);
            instance.setDataset(instances);
            for (int j = 0; j < len; j++) {
                instance.setValue(j, features[i][j]);
            }
            instances.add(instance);
        }
        return instances;
    }

    /**
     * weka分类器类别对应的下标
     *
     * @param winner
     * @return
     */
    public static Map<Integer, String> getWinnerIndex(List<String> winner) {
        Map<Integer, String> map = new HashMap<>();
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < winner.size()-1; i++) {
            set.add(winner.get(i));
        }
        int k = 0;
        for (String s : set) {
            map.put(k++, s);
        }
        return map;
    }

}
