package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.recommend.classification.Bayes;
import com.buaa.act.sdp.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

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
    private ContentBase contentBase;
    @Autowired
    private FeatureExtract featureExtract;


    // weka分类类别号对应的开发者handle
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

    //寻找k个邻居，局部的分类器
    public void localClassifier(String challengeType, int neighborNums) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners();
        int start = (int) (0.9 * winners.size());
        int[] num = new int[]{1, 5, 10, 20};
        int[] count = new int[]{0, 0, 0, 0};
        List<String> worker;
        for (int i = start; i < winners.size(); i++) {
            List<String> user = contentBase.getNeighbors(features, i, winners, neighborNums);
            Set<String> set = new HashSet<>(user);
            // 分类时只有一个类异常
            if (set.size() == 1) {
                worker = new ArrayList<String>() {{
                    add(user.get(0));
                }};
            } else {
                Map<Double, String> winnerIndex = getWinnerIndex(user, neighborNums);
                Instances instances = WekaArffUtil.getInstances(Constant.CLASSIFIER_DIRECTORY + String.valueOf(i));
                instances.setClassIndex(instances.numAttributes() - 1);
                Map<String, Double> tcResult = tcBayes.getRecommendResult(instances, neighborNums, winnerIndex);
                worker = recommendWorker(tcResult);
            }
            for (int j = 0; j < num.length; j++) {
                for (int k = 0; k < worker.size() && k < num[j]; k++) {
                    if (winners.get(i).equals(worker.get(k))) {
                        count[j]++;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < num.length; i++) {
            System.out.println(num[i] + "\t" + 1.0 * count[i] / (winners.size() - start));
        }
    }

    // 先kmeans聚类在某一类别中分类
    public void clusterClassifier(String challengeType, int n) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winner = featureExtract.getWinners();
        WekaArffUtil.writeToArffCluster(challengeType, features);
        Map<Integer, List<Integer>> map = new HashMap<>();
        Instances instances = WekaArffUtil.getInstances(Constant.CLUSTER_DIRECTORY + challengeType);
        int k, start = (int) (0.9 * winner.size());
        try {
            SimpleKMeans kMeans = new SimpleKMeans();
            kMeans.setNumClusters(n);
            kMeans.buildClusterer(new Instances(instances, 0, start));
            for (int i = 0; i < start; i++) {
                k = kMeans.clusterInstance(instances.instance(i));
                if (map.containsKey(k)) {
                    map.get(k).add(i);
                } else {
                    List<Integer> temp = new ArrayList<>();
                    temp.add(i);
                    map.put(k, temp);
                }
            }
            List<String> worker;
            int[] num = new int[]{1, 5, 10, 20};
            int[] count = new int[]{0, 0, 0, 0};
            for (int i = start; i < winner.size(); i++) {
                k = kMeans.clusterInstance(instances.instance(i));
                List<Integer> list = map.get(k);
                double[][] feature = new double[list.size() + 1][features[0].length];
                List<String> user = new ArrayList<>(list.size() + 1);
                for (int j = 0; j < list.size(); j++) {
                    feature[j] = features[list.get(j)];
                    user.add(winner.get(list.get(j)));
                }
                feature[list.size()] = features[i];
                user.add(winner.get(i));
                Set<String> set = new HashSet<>(user);
                if (set.size() > 1) {
                    WekaArffUtil.writeToArffClassfiler(String.valueOf(i), feature, user);
                    Map<Double, String> winnerIndex = getWinnerIndex(user, user.size() - 1);
                    Instances testInstances = WekaArffUtil.getInstances(Constant.CLASSIFIER_DIRECTORY + String.valueOf(i));
                    testInstances.setClassIndex(testInstances.numAttributes() - 1);
                    Map<String, Double> tcResult = tcBayes.getRecommendResult(testInstances, list.size(), winnerIndex);
                    worker = recommendWorker(tcResult);
                } else {
                    worker = new ArrayList<>();
                    worker.add(user.get(0));
                }
                for (int j = 0; j < num.length; j++) {
                    for (int t = 0; t < worker.size() && t < num[j]; t++) {
                        if (winner.get(i).equals(worker.get(t))) {
                            count[j]++;
                            break;
                        }
                    }
                }
                // 更新聚类结果
                if (map.containsKey(k)) {
                    map.get(k).add(i);
                } else {
                    List<Integer> temp = new ArrayList<>();
                    temp.add(i);
                    map.put(k, temp);
                }
            }
            for (int j = 0; j < num.length; j++) {
                System.out.println(num[j] + "\t" + 1.0 * count[j] / (winner.size() - start));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 原始的分类和协同过滤
    public void getRecommendResult(String challengeType) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners();
        WekaArffUtil.writeToArffClassfiler(challengeType, features, winners);
        Map<Integer, Map<String, Double>> score = featureExtract.getScores();
        List<ChallengeItem> items = featureExtract.getItems();
        int start = (int) (0.9 * winners.size());
        Instances instances = WekaArffUtil.getInstances(Constant.CLASSIFIER_DIRECTORY + challengeType);
        instances.setClassIndex(instances.numAttributes() - 1);
        Set<String> set = new HashSet<>(winners);
        List<Map<String, Double>> scores = new ArrayList<>(start);
        for (int i = 0; i < winners.size(); i++) {
            scores.add(score.get(items.get(i).getChallengeId()));
        }
        List<String> worker;
        int a;
        double b, c;
        double[][] results = new double[11][4];
        for (a = 0; a <= 10; a++) {
            b = 1.0 * a / 10;
            c = 1.0 - b;
            int[] num = new int[]{1, 5, 10, 20};
            int[] count = new int[]{0, 0, 0, 0};
            for (int i = start; i < winners.size(); i++) {
                Map<Double, String> winnerIndex = getWinnerIndex(winners, i);
//                Map<String, Double> tcResult = tcBayes.getRecommendResult(instances, i, winnerIndex);
                Map<String, Double> cbmResult = contentBase.getRecommendResult(features, i, scores, set);
//                worker = recommendWorker(tcResult);
                worker = recommendWorker(cbmResult);
//                worker = recommendWorker(tcResult, cbmResult, b, c);
                for (int j = 0; j < num.length; j++) {
                    for (int k = 0; k < worker.size() && k < num[j]; k++) {
                        if (winners.get(i).equals(worker.get(k))) {
                            count[j]++;
                            break;
                        }
                    }
                }
            }
            System.out.println(b + " : " + c);
            for (int i = 0; i < num.length; i++) {
                results[a][i] = 1.0 * count[i] / (winners.size() - start);
                System.out.println(num[i] + "\t" + results[a][i]);
            }
        }
    }

    public void getRecommendBayesUcl(String challengeType) {
        featureExtract.getWinnersAndScores(challengeType);
        double[][] features = featureExtract.getTimesAndAward();
        List<String> winners = featureExtract.getWinners();
        int start = (int) (0.9 * winners.size());
        WordCount[] wordCounts = featureExtract.getWordCount(start);
        List<Map<String, Double>> result = bayes.getRecommendResultUcl(wordCounts, features, winners, start);
        int[] num = new int[]{1, 5, 10, 20};
        int[] count = new int[]{0, 0, 0, 0};
        List<String> worker;
        for (int i = start; i < winners.size(); i++) {
            worker = recommendWorker(result.get(i - start));
            for (int j = 0; j < num.length; j++) {
                for (int k = 0; k < worker.size() && k < num[j]; k++) {
                    if (winners.get(i).equals(worker.get(k))) {
                        count[j]++;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < num.length; i++) {
            System.out.println(num[i] + "\t" + 1.0 * count[i] / (winners.size() - start));
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
        for (int i = 0; i < list.size() && i < 20; i++) {
            workers.add(list.get(i).getKey());
        }
        return workers;
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
}
