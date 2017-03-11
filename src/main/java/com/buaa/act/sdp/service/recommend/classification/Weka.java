package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.service.recommend.FeatureExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.Random;

/**
 * Created by yang on 2017/3/6.
 */
@Service
public class Weka {

    @Autowired
    private FeatureExtract featureExtract;

//    public void weka() {
//        //  featureExtract.getFeatures();
////        TcJ48 classifier = new TcJ48();
////        TcLibSvm classifier = new TcLibSvm();
//        TcBayes classifier = new TcBayes();
//        File testFile = new File("F:\\arff\\Developments.arff");
//        ArffLoader loader = new ArffLoader();
//        try {
//            loader.setFile(testFile);
//            Instances instances = loader.getDataSet();
//            instances.setClassIndex(instances.numAttributes() - 1); // 分类属性行数
//            int split = (int) (instances.numInstances() * 0.9);
//            Instances train = new Instances(instances, 0, split);
//            Instances test = new Instances(instances, split, instances.numInstances() - split);
//            classifier.buildClassifier(train);
//            int[] num = new int[]{1, 5, 10, 20};
//            int[] count = new int[]{0, 0, 0, 0};
//            for (int i = 0; i < test.numInstances(); i++) {
//                double[][] result = classifier.classifyInstances(test.instance(i));
//                for (int j = 0; j < num.length; j++) {
//                    for (int k = 0; k < result.length && k < num[j]; k++) {
//                        if (result[k][1] == test.instance(i).classValue()) {
//                            count[j]++;
//                            break;
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < num.length; i++) {
//                System.out.println(num[i] + "\t" + 1.0 * count[i] / test.numInstances());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static void main(String[] args) {
//        new Weka().weka();
//    }
}
