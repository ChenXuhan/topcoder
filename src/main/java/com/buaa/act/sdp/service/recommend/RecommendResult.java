package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.recommend.classification.Bayes;
import com.buaa.act.sdp.service.recommend.classification.TcBayes;
import com.buaa.act.sdp.service.recommend.classification.UclKnn;
import com.buaa.act.sdp.util.WekaArffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
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
    @Autowired
    private UclKnn uclKnn;

    public Map<Double, String> getWinnerIndex(List<String> winner) {
        Map<Double, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        for (String s : winner) {
            set.add(s);
        }
        int k = 0;
        double index;
        for (String s : set) {
            index = k++;
            map.put(index, s);
        }
        return map;
    }

    public void getRecommendResult(String challengeType) {
        double[][] features = featureExtract.getFeatures(challengeType);
        List<String> winners = featureExtract.getWinners();
        Map<Integer, Map<String, Double>> score = featureExtract.getScores();
        List<ChallengeItem> items = featureExtract.getItems();
        int start = (int) (0.9 * winners.size());
        Map<Double, String> winnerIndex = getWinnerIndex(winners);
        Instances instances = WekaArffUtil.getInstances(challengeType);
        Set<String> set = new HashSet<>(winners);
        List<Map<String, Double>> scores = new ArrayList<>(start);
        for (int i = 0; i < start; i++) {
            scores.add(score.get(items.get(i).getChallengeId()));
        }
        List<Map<String, Double>> tcResult = tcBayes.getRecommendResult(instances, start, winnerIndex);
        List<Map<String, Double>> cbmResult = contentBase.getRecommendResult(features, start, scores, set);
//        List<Map<String, Integer>> knnResult = knn.getRecommendResult(features, 1, start, winners);
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
//                worker = recommendWorker(tcResult.get(i - start));
//                worker = recommendWorker(cbmResult.get(i - start));
//            worker = recommendKnnWorker(knnResult.get(i - start));
                worker = recommendWorker(tcResult.get(i - start), cbmResult.get(i - start), b, c);
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

    public List<String> recommendWorker(Map<String, Double> bayesMap, Map<String, Double> cbmMap, double a, double b) {
        List<String> workers = new ArrayList<>();
        Map<String, Double> map = new HashMap<>();
        if (a > 0) {
            for (Map.Entry<String, Double> entry : bayesMap.entrySet()) {
                map.put(entry.getKey(), a * entry.getValue());
            }
        }
        if (b > 0) {
            for (Map.Entry<String, Double> entry : cbmMap.entrySet()) {
                if (map.containsKey(entry.getKey())) {
                    map.put(entry.getKey(), b * entry.getValue() + map.get(entry.getKey()));
                } else {
                    map.put(entry.getKey(), b * entry.getValue());
                }
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
