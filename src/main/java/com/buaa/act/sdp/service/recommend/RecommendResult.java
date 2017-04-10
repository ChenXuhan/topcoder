package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.recommend.classification.Bayes;
import com.buaa.act.sdp.service.recommend.classification.LocalClassifier;
import com.buaa.act.sdp.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.service.recommend.cluster.Cluster;
import com.buaa.act.sdp.service.recommend.network.Competition;
import com.buaa.act.sdp.util.Maths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/2/24.
 */
@Service
public class RecommendResult {

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

    //  寻找k个邻居，局部的分类器
    public void localClassifier(String challengeType) {
        System.out.println("Local");
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners();
        int start = (int) (0.9 * winners.size());
        int[] count = new int[]{0, 0, 0, 0};
        int[] counts = new int[]{0, 0, 0, 0};
        List<String> worker = null;
        for (int i = start; i < winners.size(); i++) {
            Map<String, Double> tcResult = localClassifier.getRecommendResult(challengeType, features, i, winners);
            System.out.println(winners.get(i));
            worker = recommendWorker(tcResult);
            calculateResult(winners.get(i), worker, counts);
            System.out.println(worker);
            List<Integer> index = localClassifier.getNeighbors();
            worker = competition.rank(index, worker, winners,i);
            System.out.println(worker);
            calculateResult(winners.get(i), worker, count);
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println(1.0 * counts[i] / (winners.size() - start));
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println(1.0 * count[i] / (winners.size() - start));
        }
    }

    // 先kmeans聚类在某一类别中分类
    public void clusterClassifier(String challengeType, int n) {
        System.out.println("Cluster");
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners= featureExtract.getWinners();
        int start = (int) (0.9 * winners.size());
        try {
            List<String> worker;
            int[] count = new int[]{0, 0, 0, 0};
            int[] counts = new int[]{0, 0, 0, 0};
            for (int i = start; i < winners.size(); i++) {
                Map<String, Double> result = cluster.getRecommendResult(challengeType, features, i, n, winners);
                System.out.println(winners.get(i));
                worker = recommendWorker(result);
                calculateResult(winners.get(i), worker, counts);
                System.out.println(worker);
                List<Integer> index = cluster.getNeighbors();
                worker = competition.rank(index, worker, winners,i);
                System.out.println(worker);
                calculateResult(winners.get(i), worker, count);
            }
            for (int j = 0; j < counts.length; j++) {
                System.out.println(1.0 * counts[j] / (winners.size() - start));
            }
            for (int j = 0; j < count.length; j++) {
                System.out.println(1.0 * count[j] / (winners.size() - start));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 原始的分类和协同过滤
    public void getRecommendResult(String challengeType) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners();
        int start = (int) (0.9 * winners.size());
        List<String> worker;
        int[] count = new int[]{0, 0, 0, 0};
        int[] counts = new int[]{0, 0, 0, 0};
        System.out.println("UCL");
        for (int i = start; i < winners.size(); i++) {
            double[][] data = new double[i + 1][features[0].length];
            List<String> user = new ArrayList<>(i + 1);
            List<Integer> index = new ArrayList<>(i + 1);
            for (int j = 0; j <= i; j++) {
                index.add(j);
            }
            Maths.copy(features, data, winners, user, index);
//            Maths.normalization(data, 5);
            Map<String, Double> tcResult = tcBayes.getRecommendResult(Constant.CLASSIFIER_DIRECTORY + challengeType + "/" + i, data, i, user);
//            System.out.println(winners.get(i));
            worker = recommendWorker(tcResult);
//            System.out.println(worker);
            calculateResult(winners.get(i), worker, counts);
            List<Integer>indexs=new ArrayList<>();
            for(int j=0;j<i;j++){
                indexs.add(j);
            }
            worker = competition.rank(indexs, worker, winners,i);
//            System.out.println(worker);
            calculateResult(winners.get(i), worker, count);
        }
//        for (int i = 0; i < count.length; i++) {
//            System.out.println(1.0 * count[i] / (winners.size() - start));
//        }
//        count = new int[]{0, 0, 0, 0};
//        System.out.println("CBM");
//        for (int i = start; i < winners.size(); i++) {
//            Map<String, Double> cbmResult = contentBase.getRecommendResult(features, i, scores, winners);
//            System.out.println(winners.get(i));
//            worker = recommendWorker(cbmResult);
//            System.out.println(worker);
//            calculateResult(winners.get(i), worker, counts);
//            List<Integer>index=new ArrayList<>();
//            for(int j=0;j<i;j++){
//                index.add(j);
//            }
//            worker = competition.rankWorkers(index, worker, winners,i);
//            System.out.println(worker);
//            calculateResult(winners.get(i), worker, count);
//        }
        for (int i = 0; i < counts.length; i++) {
            System.out.println(1.0 * counts[i] / (winners.size() - start));
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println(1.0 * count[i] / (winners.size() - start));
        }
    }

    public void getRecommendBayesUcl(String challengeType) {
        featureExtract.getWinnersAndScores(challengeType);
        double[][] features = featureExtract.getTimesAndAward();
        List<String> winners = featureExtract.getWinners();
        int start = (int) (0.9 * winners.size());
        int[] count = new int[]{0, 0, 0, 0};
        List<String> worker;
        for (int i = start; i < winners.size(); i++) {
            WordCount[] wordCounts = featureExtract.getWordCount(i);
            Map<String, Double> result = bayes.getRecommendResultUcl(wordCounts, features, winners, i);
            worker = recommendWorker(result);
            calculateResult(winners.get(i), worker, count);
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println(1.0 * count[i] / (winners.size() - start));
        }
    }

    // 分类结果排序
    public List<String> recommendWorker(Map<String, Double> bayesMap, Map<String, Double> cbmMap, double a, double b) {
        List<String> workers = new ArrayList<>();
        Map<String, Double> map = new HashMap<>();
        for (Map.Entry<String, Double> entry : bayesMap.entrySet()) {
            map.put(entry.getKey(), a * entry.getValue());
        }
        for (Map.Entry<String, Double> entry : cbmMap.entrySet()) {
            if (bayesMap.containsKey(entry.getKey())) {
                map.put(entry.getKey(), b * entry.getValue() + map.get(entry.getKey()));
            } else {
                map.put(entry.getKey(), b * entry.getValue());
            }
        }
        List<Map.Entry<String, Double>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < list.size() && i < 20; i++) {
            workers.add(list.get(i).getKey());
        }
        return workers;
    }

    //分类结果排序
    public List<String> recommendWorker(Map<String, Double> map) {
        List<String> workers = new ArrayList<>();
        List<Map.Entry<String, Double>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < list.size(); i++) {
            workers.add(list.get(i).getKey());
        }
        return workers;
    }

    public List<String> recommendWorker(List<Map<String, Double>> list) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            List<String> worker = recommendWorker(list.get(i));
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

    public void calculateResult(String winner, List<String> worker, int[] count) {
        int[] num = new int[]{1, 5, 10, 15};
        for (int j = 0; j < num.length; j++) {
            for (int k = 0; k < worker.size() && k < num[j]; k++) {
                if (winner.equals(worker.get(k))) {
                    count[j]++;
                    break;
                }
            }
        }
    }
}
