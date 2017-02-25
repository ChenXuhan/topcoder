package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.recommend.classification.Bayes;
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
    private ContentBase contentBase;
    @Autowired
    private FeatureExtract featureExtract;

    public void getRecommendResult() {
        double[][] features = featureExtract.getFeatures();
        List<String> winners = featureExtract.getWinners();
        Map<Integer, Map<String, Double>> score = featureExtract.getScores();
        List<ChallengeItem> items = featureExtract.getItems();
        int start = (int) (0.9 * winners.size());
        List<Map<String, Double>> scores = new ArrayList<>(start);
        for (int i = 0; i < start; i++) {
            scores.add(score.get(items.get(i).getChallengeId()));
        }
        List<Map<String, Double>> bayesResult = bayes.getRecommendResult(features, winners, start);
        List<Map<String, Double>> cbmResult = contentBase.getRecommendResult(features, start, scores);
        List<String> worker;
        int[] num = new int[]{1, 5, 10, 20};
        int[] count = new int[]{0, 0, 0, 0};
        for (int i = start; i < winners.size(); i++) {
//            worker = recommendWorker(bayesResult.get(i-start));
//            worker = recommendWorker(cbmResult.get(i));
            worker = recommendWorker(bayesResult.get(i - start), cbmResult.get(i - start));
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

    public void getRecommendUcl() {
        featureExtract.getWinnersAndScores();
        double[][] features = featureExtract.getTimesAndAward();
        List<String> winners = featureExtract.getWinners();
        int start = (int) (0.8 * winners.size());
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

    public List<String> recommendWorker(Map<String, Double> bayesMap, Map<String, Double> cbmMap) {
        List<String> workers = new ArrayList<>();
        for (Map.Entry<String, Double> entry : bayesMap.entrySet()) {
            entry.setValue(entry.getValue() / 2);
            if (cbmMap.containsKey(entry.getKey())) {
                cbmMap.put(entry.getKey(), cbmMap.get(entry.getKey()) / 2);
            }
        }
        for (Map.Entry<String, Double> entry : cbmMap.entrySet()) {
            if (bayesMap.containsKey(entry.getKey())) {
                bayesMap.put(entry.getKey(), entry.getValue() + bayesMap.get(entry.getKey()));
            } else {
                bayesMap.put(entry.getKey(), entry.getValue() / 2);
            }
        }
        List<Map.Entry<String, Double>> list = new ArrayList<>();
        list.addAll(bayesMap.entrySet());
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

}
