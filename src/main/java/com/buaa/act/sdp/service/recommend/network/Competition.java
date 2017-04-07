package com.buaa.act.sdp.service.recommend.network;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.service.recommend.FeatureExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/3/17.
 */
@Component
public class Competition {

    private Map<Integer, Map<String, Double>> scores;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private FeatureExtract featureExtract;

    public Competition() {
        scores = new HashMap<>();
    }

    // 注册表获取所有task所有人的得分:0
    public Map<Integer, Map<String, Double>> getAllWorkerScores() {
        if (scores.size() > 0) {
            return scores;
        }
        List<ChallengeRegistrant> challengeRegistrants = challengeRegistrantDao.getAllRegistrant();
        Map<String, Double> score;
        for (ChallengeRegistrant challengeRegistrant : challengeRegistrants) {
            score = scores.getOrDefault(challengeRegistrant.getChallengeID(), null);
            if (score != null) {
                score.put(challengeRegistrant.getHandle(), 0.0);
            } else {
                score = new HashMap<>();
                score.put(challengeRegistrant.getHandle(), 0.0);
                scores.put(challengeRegistrant.getChallengeID(), score);
            }
        }
        Map<Integer, Map<String, Double>> submissionScores = featureExtract.getScores();
        updateWorkerScores(submissionScores);
        return scores;
    }

    //依据submission表更新worker的得分
    public void updateWorkerScores(Map<Integer, Map<String, Double>> submissionScores) {
        if (submissionScores != null) {
            Map<String, Double> registrant, submission;
            for (Map.Entry<Integer, Map<String, Double>> entry : submissionScores.entrySet()) {
                if (scores.containsKey(entry.getKey())) {
                    registrant = scores.get(entry.getKey());
                    submission = entry.getValue();
                    for (Map.Entry<String, Double> temp : submission.entrySet()) {
                        registrant.put(temp.getKey(), temp.getValue());
                    }
                }else{
                    registrant=new HashMap<>();
                    submission = entry.getValue();
                    for (Map.Entry<String, Double> temp : submission.entrySet()) {
                        registrant.put(temp.getKey(), temp.getValue());
                    }
                    scores.put(entry.getKey(),registrant);
                }
            }
        }
    }

    // 获取当前任务的相似任务中worker的得分
    public List<Map<String, Double>> getWorkerScores() {
        Map<Integer, Map<String, Double>> score = getAllWorkerScores();
        List<ChallengeItem> items = featureExtract.getItems();
        List<Map<String, Double>> list = new ArrayList<>(items.size());
        for (ChallengeItem item : items) {
            list.add(score.get(item.getChallengeId()));
        }
        return list;
//        return featureExtract.getUserScore();
    }

    public Map<String, Integer> getIndex(List<String> worker) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < worker.size(); i++) {
            index.put(worker.get(i), i);
        }
        return index;
    }

    // 原始的有向边
    public int[][] getRelationEdge(Map<String, Integer> index, List<Integer> neighbors, List<Map<String, Double>> scores, List<String> winners, int n) {
        int[][] attraction = new int[index.size()][index.size()];
        String winner;
        int one, two;
        Map<String, Double> score;
        for (int i = 0; i <n; i++) {
//        for (int i = 0; i < neighbors.size(); i++) {
//            score = scores.get(neighbors.get(i));
//            winner = winners.get(neighbors.get(i));
            score = scores.get(i);
            winner = winners.get(i);
            if(!index.containsKey(winner)){
                continue;
            }
            one = index.get(winner);
            for (String user : score.keySet()) {
                if (index.containsKey(user) && !winner.equals(user)) {
                    two = index.get(user);
                    attraction[one][two]++;
                }
            }
        }
        return attraction;
    }

    // worker吸引力(worker之间边的和)
    public int[][] getWorkerAttraction(int[][] attraction) {
        for (int i = 0; i < attraction.length; i++) {
            for (int j = i + 1; j < attraction.length; j++) {
                attraction[i][j] += attraction[j][i];
                attraction[j][i] = attraction[i][j];
            }
        }
        return attraction;
    }

    // worker排斥关系计算
    public double[][] getWorkerRepulsion(int[][] attraction) {
        // worker的边数量
        int[] deg = new int[attraction.length];
        for (int i = 0; i < attraction.length; i++) {
            int one = 0;
            for (int j = 0; j < attraction.length; j++) {
                one = one + attraction[i][j];
            }
            deg[i] = one;
        }
        double[][] repulsion = new double[attraction.length][attraction.length];
        for (int i = 0; i < attraction.length; i++) {
            for (int j = i + 1; j < attraction.length; j++) {
                repulsion[i][j] = 1.0 * (deg[i] + 1) * (deg[j] + 1) / (attraction[i][j] + 1);
                repulsion[j][i] = repulsion[i][j];
            }
        }
        return repulsion;
    }

    // 综合分类推荐拍序和输赢次数排序
    public List<String> reRank(List<Integer> neighbors, List<String> worker, List<String> winners,int n) {
        List<Map<String, Double>> scores = getWorkerScores();
        Map<String, Integer> index = getIndex(worker);

        Map<Integer,String>allWinners=new HashMap<>();
       

        int[][] relation = getRelationEdge(index, neighbors, scores, winners,n);
        int[][] winTimes = new int[worker.size()][2];
        int t = 0;
        // 计算获胜次数，按照获胜次数多的排序
        for (int i = 0; i < relation.length; i++) {
            t = 0;
            for (int j = 0; j < relation.length; j++) {
                if (relation[i][j] > relation[j][i]) {
                    t++;
                }
            }
            winTimes[i][0] = i;
            winTimes[i][1] = t;
        }
        Arrays.sort(winTimes, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[1] - o1[1];
            }
        });
        // 结合原始分类顺序和输赢次数顺序
        int[][] rank = new int[worker.size()][2];
        for (int i = 0; i < worker.size(); i++) {
            t = winTimes[i][0];
            rank[t][0] = t;
            rank[t][1] = i + t;
        }
        Arrays.sort(rank, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[1] - o2[1];
            }
        });
        List<String> result = new ArrayList<>(worker.size());
        for (int i = 0; i < worker.size(); i++) {
            result.add(worker.get(rank[i][0]));
        }
        return result;
    }

    //分类的结果利用关系重新排序
    public List<String> workerRank(List<Integer> neighbors, List<String> worker, List<String> winners, String win) {
        List<Map<String, Double>> scores = getWorkerScores();
        Map<String, Integer> index = getIndex(worker);
        int[][] attraction = getRelationEdge(index, neighbors, scores, winners, worker.size());
        attraction = getWorkerAttraction(attraction);
        int one = index.getOrDefault(win, -1);
        if (one >= 0) {
            for (int i = 0; i < attraction.length; i++) {
                System.out.print(worker.get(one) + ":" + worker.get(i) + "=" + attraction[one][i] + ":" + attraction[i][one] + " ");
            }
        }
        System.out.println();
        double[][] repulsion = getWorkerRepulsion(attraction);
        int[][] attr = new int[worker.size()][];
        int[][] replu = new int[worker.size()][];
        for (int i = 0; i < worker.size(); i++) {
            attr[i] = sortAttraction(attraction[i]);
            replu[i] = sortReplusion(repulsion[i]);
        }
        List<String> result = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        // 删除worker后面排斥力大的前几名worker
        for (int i = 0; i < worker.size(); i++) {
            if (set.contains(i)) {
                continue;
            }
            int[] array = replu[i];
            for (int j = 0; j < 3 && j < array.length; j++) {
                if (i < array[j]) {
                    set.add(array[j]);
                }
            }
        }
        for (int i = 0; i < worker.size(); i++) {
            if (set.contains(i)) {
                continue;
            }
            result.add(worker.get(i));
        }
        List<String> res = new ArrayList<>();
        res.addAll(result);
        // 添加worker吸引力大的worker
        for (int i = 0; i < result.size(); i++) {
            int k = index.get(result.get(i));
            for (int j = 0; j < 3 && j < attr[k].length; j++) {
                if (!res.contains(worker.get(attr[k][j]))) {
                    res.add(worker.get(attr[k][j]));
                }
            }
        }
        return res;
    }

    // 吸引力排序
    public int[] sortAttraction(int[] num) {
        int[][] array = new int[num.length][2];
        for (int i = 0; i < num.length; i++) {
            array[i][0] = i;
            array[i][1] = num[i];
        }
        Arrays.sort(array, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[1] - o1[1];
            }
        });
        int[] res = new int[num.length];
        for (int i = 0; i < num.length; i++) {
            res[i] = array[i][0];
        }
        return res;
    }

    //排斥力排序
    public int[] sortReplusion(double[] num) {
        Map<Integer, Double> map = new HashMap<>();
        for (int i = 0; i < num.length; i++) {
            map.put(i, num[i]);
        }
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int[] res = new int[num.length];
        for (int i = 0; i < num.length; i++) {
            res[i] = list.get(i).getKey();
        }
        return res;
    }
}
