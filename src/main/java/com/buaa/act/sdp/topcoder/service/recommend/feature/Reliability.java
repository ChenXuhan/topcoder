package com.buaa.act.sdp.topcoder.service.recommend.feature;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.network.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/4/18.
 */
@Component
public class Reliability {

    private static final Logger logger = LoggerFactory.getLogger(Reliability.class);

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private Competition competition;

    /**
     * 计算初步推荐开发者的可靠性
     *
     * @param worker    候选集
     * @param neighbors 相似任务
     * @param winners   获胜者
     * @param type      任务类型
     * @return
     */
    public List<String> filter(List<String> worker, List<Integer> neighbors, List<String> winners, String type) {
        logger.info("compute and filter developers with lower reliability");
        List<String> winner = new ArrayList<>();
        List<Map<String, Double>> score = competition.getSameTypeWorkers(neighbors, winners, winner, type);
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
        double avgWinRate = 0, avgSubRate = 0;
        String developer;
        int a, b, c;
        double num;
        for (int i = 0; i < worker.size(); i++) {
            developer = worker.get(i);
            a = winCount.getOrDefault(developer, 0);
            b = submissionCount.getOrDefault(developer, 0);
            c = total.getOrDefault(developer, 0);
            num = b > 0 ? 1.0 * a / b : 0;
            winRate.put(i, num);
            avgWinRate += num;
            num = c > 0 ? 1.0 * b / c : 0;
            subRate.put(i, num);
            avgSubRate += num;
        }
        avgWinRate /= worker.size();
        avgSubRate /= worker.size();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < worker.size(); i++) {
            if (winRate.get(i) < avgWinRate && subRate.get(i) < avgSubRate && i > 6) {
                continue;
            }
            result.add(worker.get(i));
        }
        return result;
    }

    /**
     * 任务的时间间隔
     *
     * @param challengeType
     */
    public void timeInterval(String challengeType) {
        List<ChallengeItem> items = featureExtract.getItems(challengeType);
        List<String> winners = featureExtract.getWinners(challengeType);
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
