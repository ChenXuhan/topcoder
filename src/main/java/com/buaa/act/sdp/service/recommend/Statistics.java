package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.network.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/4/18.
 */
@Service
public class Statistics {

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private Competition competition;

    public List<String> rank(List<String> worker, List<Integer> neighbors, List<String> winners, String str) {
//        int[] num = reliabilityRank(worker, winners, neighbors, str);
//        int[][] index = new int[worker.size()][2];
//        for (int i = 0; i < num.length; i++) {
//            index[i][0] = num[i];
//            index[i][1] = num[i] + i;
//        }
//        Arrays.sort(index, new Comparator<int[]>() {
//            @Override
//            public int compare(int[] o1, int[] o2) {
//                return o1[1] - o2[1];
//            }
//        });
//        List<String> result = new ArrayList<>(num.length);
//        for (int i = 0; i < num.length; i++) {
//            result.add(worker.get(index[i][0]));
//        }
        return reliabilityRank(worker, winners, neighbors, str);
    }

    public List<String> reliabilityRank(List<String> worker, List<String> winners, List<Integer> neighbors, String str) {
        List<String> winner = new ArrayList<>();
        List<Map<String, Double>> score = competition.getSameTypeWorkers(neighbors, winners, winner);
        Map<String, Integer> total = new HashMap<>();
        Map<String, Integer> submissionCount = new HashMap<>();
        Map<String, Integer> winCount = new HashMap<>();
        int count;
        Set<String> set = new HashSet<>(worker);
        for (int i = 0; i < winner.size(); i++) {
            if (!set.contains(winner.get(i))) {
                continue;
            }
            count = winCount.getOrDefault(winner.get(i), -1);
            if (count > 0) {
                winCount.put(winner.get(i), count + 1);
            } else {
                winCount.put(winner.get(i), 1);
            }
        }
        for (int i = 0; i < score.size(); i++) {
            for (Map.Entry<String, Double> entry : score.get(i).entrySet()) {
                count = total.getOrDefault(entry.getKey(), -1);
                if (count > 0) {
                    total.put(entry.getKey(), count + 1);
                } else {
                    total.put(entry.getKey(), 1);
                }
                if (entry.getValue() > 0) {
                    count = submissionCount.getOrDefault(entry.getKey(), -1);
                    if (count > 0) {
                        submissionCount.put(entry.getKey(), count + 1);
                    } else {
                        submissionCount.put(entry.getKey(), 1);
                    }
                }
            }
        }
        Map<Integer, Double> winRate = new HashMap<>();
        Map<Integer, Double> subRate = new HashMap<>();
        Set<Integer> filter = new HashSet<>();
        Set<Integer> filters = new HashSet<>();
        double avgSub = 0, avgWin = 0;
        int subTotal = 0, winTotal = 0, regTotal = 0;
        for (int i = 0; i < worker.size(); i++) {
//            if (i < 20) {
                winTotal += winCount.get(worker.get(i));
                subTotal += submissionCount.get(worker.get(i));
                regTotal += total.get(worker.get(i));
//            }
            winRate.put(i, 1.0 * winCount.get(worker.get(i)) / submissionCount.get(worker.get(i)));
            subRate.put(i, 1.0 * submissionCount.get(worker.get(i)) / total.get(worker.get(i)));
            avgWin += 1.0 * winCount.get(worker.get(i)) / submissionCount.get(worker.get(i));
            avgSub += 1.0 * submissionCount.get(worker.get(i)) / total.get(worker.get(i));
        }
        avgSub /= worker.size();
        avgWin /= worker.size();
//        subTotal /= worker.size();
//        winTotal /= worker.size();
//        regTotal /= worker.size();
        List<String> result = new ArrayList<>();
        List<Map.Entry<Integer, Double>> winList = new ArrayList<>(winRate.entrySet());
        List<Map.Entry<Integer, Double>> subList = new ArrayList<>(subRate.entrySet());
        Collections.sort(winList, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Collections.sort(subList, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int k = 0;
        for (int i = 0; i < winList.size(); i++) {
            if (str.equals(worker.get(winList.get(i).getKey()))) {
                k = winList.get(i).getKey();
                break;
            }
        }
        int a = 0, b = 0, c = 0, d = 0;
        for (int i = 0; i < winList.size(); i++) {
            if (winList.get(i).getKey() < k && winList.get(i).getValue() < avgWin) {
                a++;
            }
            if (winList.get(i).getKey() < k && winList.get(i).getValue() < 1.0 * winTotal / subTotal && subList.get(i).getValue() < 1.0 * subTotal / regTotal && subList.get(i).getKey() < k) {
                c++;
            }
            if (subList.get(i).getKey() < k && subList.get(i).getValue() < avgSub) {
                b++;
            }
            if (subList.get(i).getKey() < k && subList.get(i).getValue() < 1.0 * subTotal / regTotal) {
                d++;
            }
        }
        for (int i = 0; i < worker.size(); i++) {
//            if (0.15 + winRate.get(i) < 1.0 * winTotal / subTotal) {
//                filter.add(i);
//            }
            if (0.3 + subRate.get(i) < 1.0 * subTotal / regTotal) {
                filter.add(i);
            }
//            if (0.3 + 1.0 * subRate.get(i) < 1.0 * subTotal / regTotal && 0.15 + 1.0 * winRate.get(i) < 1.0 * winTotal / subTotal) {
//                filter.add(i);
//            }
        }
        for (int i = 0; i < worker.size(); i++) {
            if (filter.contains(i)) {
                continue;
            }
            result.add(worker.get(i));
        }
        return result;
    }

    public void timeInterval(String challengeType) {
        featureExtract.init(challengeType);
        List<ChallengeItem> items = featureExtract.getItems();
        List<String> winners = featureExtract.getWinners();
        Map<String, List<String>> map = new HashMap<>();
        String time;
        for (int i = 0; i < items.size(); i++) {
            time = items.get(i).getPostingDate().substring(0, 10);
            if (map.containsKey(winners.get(i))) {
                map.get(winners.get(i)).add(time);
            } else {
                List<String> list = new ArrayList<>();
                list.add(time);
                map.put(winners.get(i), list);
            }
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> temp = entry.getValue();
            System.out.println(entry.getKey() + ":" + temp.get(0) + "_" + temp.get(temp.size() - 1));
        }
    }
}
