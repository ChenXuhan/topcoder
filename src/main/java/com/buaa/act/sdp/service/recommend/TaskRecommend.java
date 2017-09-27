package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.recommend.classification.*;
import com.buaa.act.sdp.service.recommend.cluster.Cluster;
import com.buaa.act.sdp.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.service.recommend.feature.Reliability;
import com.buaa.act.sdp.service.recommend.feature.WordCount;
import com.buaa.act.sdp.service.recommend.network.Competition;
import com.buaa.act.sdp.service.recommend.result.TaskResult;
import com.buaa.act.sdp.util.Maths;
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
    private TaskResult taskResult;

    private int[] testData;

    // 测试集如何选取？
    public int[] testDataSet(int n) {
        int k = n - (int) (0.5 * n);
        testData = new int[k];
        for (int i = 0; i < k; i++) {
            testData[i] = n - k + i;
        }
        return testData;
    }

    //  寻找k个邻居，局部的分类器
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
            Map<String, Double> tcResult = localClassifier.getRecommendResult(challengeType, features, num[i], winners);
            worker = taskResult.recommendWorker(tcResult);
            calculateResult(winners.get(num[i]), worker, counts, mpp);
            List<Integer> index = localClassifier.getNeighbors();
            worker = reliability.filter(worker, index, winners, challengeType);
            worker = competition.refine(index, worker, winners, num[i], challengeType);
            calculateResult(winners.get(num[i]), worker, count, mpps);
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println((i + 1) + "\t" + 1.0 * counts[i] / num.length + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length + "\t" + mpps[i] / num.length);
        }
    }

    // 先kmeans聚类在某一聚类中分类
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
                Map<String, Double> result = cluster.getRecommendResult(challengeType, features, features[num[i]], num[i], n, winners);
                worker = taskResult.recommendWorker(result);
                calculateResult(winners.get(num[i]), worker, counts, mpp);
                List<Integer> index = cluster.getNeighbors();
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

    // 原始的分类
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
            worker = taskResult.recommendWorker(tcResult);
            calculateResult(winners.get(num[i]), worker, counts, mpp);
            worker = reliability.filter(worker, index, winners, challengeType);
            List<Integer> indexs = new ArrayList<>();
            for (int j = 0; j < num[i]; j++) {
                indexs.add(j);
            }
            worker = competition.refine(indexs, worker, winners, num[i], challengeType);
//            worker = competition.uclRank(indexs, worker, winners, num[i], challengeType);
            calculateResult(winners.get(num[i]), worker, count, mpps);
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println((i + 1) + "\t" + 1.0 * counts[i] / num.length + "\t" + 1.0 * count[i] / num.length + "\t" + mpp[i] / num.length + "\t" + mpps[i] / num.length);
        }
    }

    // 协同过滤
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
            worker = taskResult.recommendWorker(cbmResult);
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

    // 考虑tf-idf后的分类推荐结果
    public void getRecommendBayesUcl(String challengeType) {
        double[][] features = featureExtract.getTimesAndAward(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
        int start = (int) (0.9 * winners.size());
        int[] count = new int[]{0, 0, 0, 0};
        List<String> worker;
        for (int i = start; i < winners.size(); i++) {
            WordCount[] wordCounts = featureExtract.getWordCount(i, challengeType);
            Map<String, Double> result = bayes.getRecommendResultUcl(wordCounts, features, winners, i);
            worker = taskResult.recommendWorker(result);
            calculateResult(winners.get(i), worker, count, null);
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println(1.0 * count[i] / (winners.size() - start));
        }
    }

    public List<String> recommendWorker(List<Map<String, Double>> list) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            List<String> worker = taskResult.recommendWorker(list.get(i));
            for (int j = 0; j < worker.size(); j++) {
                double k = result.getOrDefault(worker.get(j), -1.0);
                if (k >= 0) {
                    result.put(worker.get(j), k + j);
                } else {
                    result.put(worker.get(j), 1.0 * j);
                }
                int m = count.getOrDefault(worker.get(j), -1);
                if (m >= 0) {
                    count.put(worker.get(j), m + 1);
                } else {
                    count.put(worker.get(j), 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            result.put(entry.getKey(), 1.0 * result.get(entry.getKey()) / entry.getValue());
        }
        List<Map.Entry<String, Double>> workers = new ArrayList<>(result.entrySet());
        Collections.sort(workers, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        List<String> worker = new ArrayList<>();
        for (int i = 0; i < workers.size(); i++) {
            worker.add(workers.get(i).getKey());
        }
        return worker;
    }

    public List<String> recommendKnnWorker(Map<String, Integer> map) {
        List<String> workers = new ArrayList<>();
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < list.size() && i < 20; i++) {
            workers.add(list.get(i).getKey());
        }
        return workers;
    }

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