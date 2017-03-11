package com.buaa.act.sdp;

import com.buaa.act.sdp.service.recommend.FeatureExtract;
import com.buaa.act.sdp.service.recommend.RecommendResult;
import com.buaa.act.sdp.service.recommend.classification.Weka;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.*;

/**
 * Created by yang on 2017/3/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestRecommend {

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private Weka weka;

    @Autowired
    private RecommendResult recommendResult;

    @Test
    public void testFeatureExtract() {
        featureExtract.getFeatures("Code");
    }

    @Test
    public void testWeka() {
        featureExtract.getFeatures("Design");
        List<String> winner = featureExtract.getWinners();
        File testFile = new File("F:\\arff\\Design.arff");
        ArffLoader loader = new ArffLoader();
        Map<Double, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        for (String s : winner) {
            set.add(s);
        }
        double index = 0;
        for (String s : set) {
            map.put(index++, s);
        }
        try {
            loader.setFile(testFile);
            Instances instances = loader.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1); // 分类属性行数
            for (int i = 0; i < instances.numInstances(); i++) {
                if (!map.get(instances.instance(i).classValue()).equals(winner.get(i))) {
                    System.out.println(instances.instance(i).classValue() + "\t" + winner.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRecommend() {
        recommendResult.getRecommendResult("Assembly Competition");
//        recommendResult.getRecommendBayesUcl("Assembly Competition");
    }
}
