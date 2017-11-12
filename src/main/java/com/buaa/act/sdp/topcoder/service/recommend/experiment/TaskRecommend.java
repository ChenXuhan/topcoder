package com.buaa.act.sdp.topcoder.service.recommend.experiment;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.topcoder.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.topcoder.service.recommend.cluster.Cluster;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.recommend.feature.Reliability;
import com.buaa.act.sdp.topcoder.service.recommend.feature.TaskWorkerAttribute;
import com.buaa.act.sdp.topcoder.service.recommend.feature.WordCount;
import com.buaa.act.sdp.topcoder.service.recommend.network.Competition;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;
import com.buaa.act.sdp.topcoder.service.statistics.DynamicMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import com.buaa.act.sdp.topcoder.service.recommend.classification.Bayes;
import com.buaa.act.sdp.topcoder.service.recommend.classification.LocalClassifier;
import com.buaa.act.sdp.topcoder.util.Maths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/2/24.
 */
@Service
public class TaskRecommend {

    @Autowired
    private Bayes bayes;
    @Autowired
    private TcBayes tcBayes;
    @Autowired
    private LocalClassifier localClassifier;
    @Autowired
    private Cluster cluster;
    @Autowired
    private ContentBase contentBase;
    @Autowired
    private FeatureExtract featureExtract;
    @Autowired
    private Competition competition;
    @Autowired
    private Reliability reliability;
    @Autowired
    private DeveloperRecommend developerRecommend;
    @Autowired
    private DynamicMsg dynamicMsg;
    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private TaskWorkerAttribute attribute;

    private int[] testData;

    /**
     * 测试集选取
     *
     * @param n
     * @return
     */
    public int[] testDataSet(int n) {
        int k = n - (int) (0.5 * n);
        testData = new int[k];
        for (int i = 0; i < k; i++) {
            testData[i] = n - k + i;
        }
        return testData;
    }

    /**
     * 寻找k个邻居，局部的分类器
     *
     * @param challengeType
     */
    public void localClassifier(String challengeType) {
        System.out.println("Local");
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
        int[] count = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] counts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] mpp = new double[20];
        double[] mpps = new double[20];
        int[] num = testDataSet(winners.size());
        List<String> worker = null;
        for (int i = 0; i < num.length; i++) {
            List<Integer> index = localClassifier.getNeighbor(features, num[i]);
            Map<String, Double> tcResult = localClassifier.getRecommendResult(challengeType, features, num[i], winners, index);
            worker = developerRecommend.recommendWorker(tcResult);
            calculateResult(winners.get(num[i]), worker, counts, mpp);
            worker = reliability.filter(worker, index, winners, challengeType);
            worker = competition.refine(index, worker, winners, num[i], challengeType);
            calculateResult(winners.get(num[i]), worker, count, mpps);
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println((i + 1) + "\t" + 1.0 * counts[i] / num.length + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length + "\t" + mpps[i] / num.length);
        }
    }

    /**
     * ESEM中DCW-DS方法，用Bayes分类
     *
     * @param challengeType
     */
    public void dcw_ds(String challengeType) {
        System.out.println("DCW_DS");
        int[] count = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] mpp = new double[20];
        List<ChallengeItem> items = taskMsg.getTasks();
        List<String> winners = taskMsg.getWinners(challengeType);
        List<ChallengeItem> tasks = taskMsg.getItems(challengeType);
        int[] num = testDataSet(winners.size());
//        List<List<double[]>> features = new ArrayList<>(items.size());
//        List<List<String>> workers = new ArrayList<>(items.size());
//        for (int i = 0; i < items.size(); i++) {
//            System.out.println(items.get(i).getChallengeId());
//            List<String> worker = new ArrayList<>();
//            features.add(attribute.generateFeatures(items.get(i), items, worker));
//            workers.add(worker);
//        }
        Set<String> skills = attribute.getAllSkills();
        for (int i = 0; i < num.length; i++) {
//            List<List<double[]>> features = new ArrayList<>(items.size());
//            List<List<String>> workers = new ArrayList<>(items.size());
            List<double[]> feature = new ArrayList<>();
            List<String> worker = new ArrayList<>();
//            int position = attribute.getFeatures(features, feature, workers, worker, tasks.get(num[i]).getChallengeId(), items);
            attribute.getFeature(feature, worker, tasks.get(num[i]), items);
            double[] current = new double[skills.size() + 9];
            attribute.generateStaticFeature(skills, tasks.get(num[i]), current);
            List<String> testWorker = new ArrayList<>();
            List<double[]> test = dynamicMsg.getDynamicFeatures(items, tasks.get(num[i]), testWorker);
            int position = feature.size();
            for (int j = 0; j < test.size(); j++) {
                double[] temp = new double[skills.size() + 15];
                System.arraycopy(current, 0, temp, 0, skills.size() + 6);
                System.arraycopy(test.get(j), 0, temp, skills.size() + 6, 9);
                feature.add(temp);
                worker.add(testWorker.get(j));
            }
            List<Double> data = tcBayes.getRecommendResult(Constant.DCW_DS + challengeType + "/" + position, feature, position, worker);
            List<String> result = developerRecommend.recommendWorker(data, worker.subList(position, worker.size()));
            calculateResult(winners.get(num[i]), result, count, mpp);
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println((i + 1) + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length);
        }
    }

    /**
     * 先kmeans聚类在某一聚类中分类
     *
     * @param challengeType
     * @param n
     */
    public void clusterClassifier(String challengeType, int n) {
        System.out.println("Cluster");
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
        try {
            List<String> worker;
            int[] count = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int[] counts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            double[] mpp = new double[20];
            double[] mpps = new double[20];
            int[] num = testDataSet(winners.size());
            for (int i = 0; i < num.length; i++) {
                List<Integer> index = new ArrayList<>();
                Map<String, Double> result = cluster.getRecommendResult(challengeType, features, features[num[i]], num[i], n, winners, index);
                worker = developerRecommend.recommendWorker(result);
                calculateResult(winners.get(num[i]), worker, counts, mpp);
                worker = reliability.filter(worker, index, winners, challengeType);
                worker = competition.refine(index, worker, winners, num[i], challengeType);
                calculateResult(winners.get(num[i]), worker, count, mpps);
            }
            for (int i = 0; i < counts.length; i++) {
                System.out.println((i + 1) + "\t" + 1.0 * counts[i] / num.length + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length + "\t" + mpps[i] / num.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接分类
     *
     * @param challengeType
     */
    public void classifier(String challengeType) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
        List<String> worker;
        int[] count = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] counts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] mpp = new double[20];
        double[] mpps = new double[20];
        int[] num = testDataSet(winners.size());
        System.out.println("UCL");
        for (int i = 0; i < num.length; i++) {
            double[][] data = new double[num[i] + 1][features[0].length];
            List<String> user = new ArrayList<>(num[i] + 1);
            List<Integer> index = new ArrayList<>(num[i] + 1);
            for (int j = 0; j <= num[i]; j++) {
                index.add(j);
            }
            Maths.copy(features, data, winners, user, index);
            Maths.normalization(data, 5);
            Map<String, Double> tcResult = tcBayes.getRecommendResult(Constant.CLASSIFIER_DIRECTORY + challengeType + "/" + num[i], data, num[i], user);
            worker = developerRecommend.recommendWorker(tcResult);
            calculateResult(winners.get(num[i]), worker, counts, mpp);
            worker = reliability.filter(worker, index, winners, challengeType);
            List<Integer> indexs = new ArrayList<>();
            for (int j = 0; j < num[i]; j++) {
                indexs.add(j);
            }
            worker = competition.refine(indexs, worker, winners, num[i], challengeType);
            calculateResult(winners.get(num[i]), worker, count, mpps);
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println((i + 1) + "\t" + 1.0 * counts[i] / num.length + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length + "\t" + mpps[i] / num.length);
        }
    }

    /**
     * 协同过滤算法
     *
     * @param challengeType
     */
    public void contentBased(String challengeType) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
        List<Map<String, Double>> scores = featureExtract.getUserScore(challengeType);
        List<String> worker;
        int[] count = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] counts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] mpp = new double[20];
        double[] mpps = new double[20];
        int[] num = testDataSet(winners.size());
        System.out.println("CBM");
        for (int i = 0; i < num.length; i++) {
            Map<String, Double> cbmResult = contentBase.getRecommendResult(features, num[i], scores, winners);
            worker = developerRecommend.recommendWorker(cbmResult);
            calculateResult(winners.get(num[i]), worker, counts, mpp);
            List<Integer> index = new ArrayList<>();
            for (int j = 0; j < num[i]; j++) {
                index.add(j);
            }
            worker = reliability.filter(worker, index, winners, challengeType);
            worker = competition.refine(index, worker, winners, num[i], challengeType);
            calculateResult(winners.get(num[i]), worker, count, mpps);
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println((i + 1) + "\t" + 1.0 * counts[i] / num.length + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length + "\t" + mpps[i] / num.length);
        }
    }

    /**
     * 使用tf-idf计算后推荐结果
     *
     * @param challengeType
     */
    public void getRecommendBayesUcl(String challengeType) {
        double[][] features = featureExtract.getTimesAndAward(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
        int start = (int) (0.9 * winners.size());
        int[] count = new int[]{0, 0, 0, 0};
        List<String> worker;
        for (int i = start; i < winners.size(); i++) {
            WordCount[] wordCounts = featureExtract.getWordCount(i, challengeType);
            Map<String, Double> result = bayes.getRecommendResultUcl(wordCounts, features, winners, i);
            worker = developerRecommend.recommendWorker(result);
            calculateResult(winners.get(i), worker, count, null);
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println(1.0 * count[i] / (winners.size() - start));
        }
    }

    /**
     * 计算所有推荐任务的accuracy和mpp
     *
     * @param winner
     * @param worker
     * @param count
     * @param mpp
     */
    public void calculateResult(String winner, List<String> worker, int[] count, double[] mpp) {
        for (int j = 0; j < count.length; j++) {
            for (int k = 0; k < worker.size() && k <= j; k++) {
                if (winner.equals(worker.get(k))) {
                    count[j]++;
                    mpp[j] += 1.0 / (k + 1);
                    break;
                }
            }
        }
    }
}
