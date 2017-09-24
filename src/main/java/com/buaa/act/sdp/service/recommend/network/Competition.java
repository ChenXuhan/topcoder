package com.buaa.act.sdp.service.recommend.network;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.service.statistics.TaskScores;
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
    private FeatureExtract featureExtract;
    @Autowired
    private TaskScores taskScores;


    // 获取当前任务的相似任务中worker的得分，分数多少无限制
    public List<Map<String, Double>> getSameTypeWorkers(List<Integer> neighbors, List<String> winners, List<String> winner, String type) {
        Map<Integer, Map<String, Double>> score = taskScores.getAllWorkerScores();
        List<ChallengeItem> items = featureExtract.getItems(type);
        List<Map<String, Double>> list = new ArrayList<>();
        for (int i = 0; i < neighbors.size(); i++) {
            list.add(score.get(items.get(neighbors.get(i)).getChallengeId()));
            winner.add(winners.get(neighbors.get(i)));
        }
        return list;
    }

    // 获取当前任务的相似任务中worker的得分,只考虑80分以上的
    public List<Map<String, Double>> getSameTypeWorker(List<Integer> neighbors, List<String> winners, List<String> winner, String type) {
        List<Map<String, Double>> lists = featureExtract.getUserScore(type);
        List<Map<String, Double>> list = new ArrayList<>();
        for (int i = 0; i < neighbors.size(); i++) {
            list.add(lists.get(neighbors.get(i)));
            winner.add(winners.get(neighbors.get(i)));
        }
        return list;
    }

    // 获取在当前任务前的所有类型任务中参与的worker，id之前
    public List<Map<String, Double>> getAllTypeWorkers(int challengeId, List<String> winner) {
        Map<Integer, Map<String, Double>> scores = taskScores.getAllWorkerScores();
        Map<Integer, String> allWinners = taskScores.getWinners();
        List<Map<String, Double>> list = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : allWinners.entrySet()) {
            if (entry.getKey() >= challengeId || !scores.containsKey(entry.getKey())) {
                continue;
            }
            list.add(scores.get(entry.getKey()));
            winner.add(entry.getValue());
        }
        return list;
    }

    public Map<String, Integer> getIndex(List<String> worker) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < worker.size(); i++) {
            index.put(worker.get(i), i);
        }
        return index;
    }

    public List<Integer> getNeighbors(List<Integer> list, int n) {
//        return list;
        List<Integer> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(i);
        }
        return result;
    }

    // 有向边,worker和worker之间的输赢边
    public int[][] getRelationEdge(Map<String, Integer> index, List<Map<String, Double>> scores, List<String> winners) {
        int[][] attraction = new int[index.size()][index.size()];
        int one, two;
        String winner;
        Map<String, Double> score;
        for (int i = 0; i < scores.size(); i++) {
            score = scores.get(i);
            winner = winners.get(i);
            if (!index.containsKey(winner)) {
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

    // 赢次数减输的次数
    public double[][] getAttraction(int[][] attraction) {
        double[][] attr = new double[attraction.length][attraction.length];
        int[] edge = new int[attraction.length];
        for (int i = 0; i < attraction.length; i++) {
            for (int j = i + 1; j < attraction.length; j++) {
                edge[i] += attraction[i][j] + attraction[j][i];
                edge[j] += attraction[i][j] + attraction[j][i];
            }
        }
        for (int i = 0; i < attraction.length; i++) {
            for (int j = i + 1; j < attraction.length; j++) {
                attr[i][j] = 0;
                //attr[i][j] = 0.9 * (attraction[j][i] - attraction[i][j]) / (attraction[i][j] + attraction[j][i]) + 0.1 * (attraction[i][j] + attraction[j][i]) / (edge[i] + edge[j]);
                if ((attraction[i][j] + attraction[j][i]) != 0) {
                    attr[i][j] = 0.5 * (attraction[j][i] - attraction[i][j]) / (attraction[i][j] + attraction[j][i]) + 0.5 * (attraction[i][j] + attraction[j][i]) / (edge[i] + edge[j]);
                }
                attr[j][i] = -attr[i][j];
            }
        }
        return attr;
    }

    // worker吸引力(worker之间边的和)
    public int[][] getUclAttraction(int[][] attraction) {
        int[][] attr = new int[attraction.length][attraction.length];
        for (int i = 0; i < attraction.length; i++) {
            for (int j = i + 1; j < attraction.length; j++) {
                attr[i][j] = attraction[j][i] + attraction[i][j];
                attr[j][i] = attr[i][j];
            }
        }
        return attr;
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

    // 综合分类推荐拍序和输赢次数排序(下标之和越小)
    public List<String> indexRank(List<Integer> neighbors, List<String> worker, int n, String type) {
        Map<String, Integer> index = getIndex(worker);
        List<String> winner = new ArrayList<>();
        List<Integer> neighbor = getNeighbors(neighbors, n);
//        List<Map<String, Double>> scores = getSameTypeWorker(neighbor, winners, winner);
        List<Map<String, Double>> scores = getAllTypeWorkers(featureExtract.getItems(type).get(n).getChallengeId(), winner);
        int[][] relation = getRelationEdge(index, scores, winner);
        int[][] winTimes = new int[worker.size()][2];
        int t;
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

    // 综合分类推荐排序和输赢次数排序
    public List<String> winTimesRank(List<Integer> neighbors, List<String> worker, List<String> winners, int n, String type) {
        List<String> winner = new ArrayList<>();
        List<Integer> neighbor = getNeighbors(neighbors, n);
//        List<Map<String, Double>> scores = getSameTypeWorker(neighbor, winners, winner);
        List<Map<String, Double>> scores = getAllTypeWorkers(featureExtract.getItems(type).get(n).getChallengeId(), winner);
        Map<String, Integer> index = getIndex(worker);
        int[][] relation = getRelationEdge(index, scores, winner);
        List<String> result = new ArrayList<>();
        // 计算获胜次数，按照获胜次数多的排序
        for (int i = 0; i < relation.length; i++) {
            if (result.contains(worker.get(i))) {
                continue;
            }
            for (int j = i + 1; j < relation.length; j++) {
                if (relation[j][i] - relation[i][j] >= j - i && i != j) {
                    if (!result.contains(worker.get(j))) {
                        result.add(worker.get(j));
                    }
                }
            }
            result.add(worker.get(i));
        }
        return result;
    }

    // 综合分类推荐排序和输赢次数排序,每次只处理一名
    public List<String> refine(List<Integer> neighbors, List<String> worker, List<String> winners, int n, String type) {
        List<String> winner = new ArrayList<>();
        List<Integer> neighbor = getNeighbors(neighbors, n);
        List<Map<String, Double>> scores = getSameTypeWorker(neighbor, winners, winner, type);
//        List<Map<String, Double>> scores=getAllTypeWorkers(featureExtract.getItems().get(n).getChallengeId(),winner);
        Map<String, Integer> index = getIndex(worker);
        int[][] relation = getRelationEdge(index, scores, winner);
        double[][] attraction = getAttraction(relation);
        int[][] attr = new int[worker.size()][];
        for (int i = 0; i < worker.size(); i++) {
            attr[i] = sortRelation(attraction[i]);
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < worker.size(); i++) {
            if (!result.contains(worker.get(i))) {
                result.add(worker.get(i));
            }
            int[] num = new int[worker.size()];
            for (int j = 0; j < attr[i].length; j++) {
                num[attr[i][j]] = attr[i][j] + j;
            }
            int count = 0;
            num = sortWorkerIndex(num);
            for (int j = 0; j < num.length; j++) {
                if (num[j] - i <= 5 && !result.contains(worker.get(num[j]))) {
                    if (count >= 5) {
                        break;
                    }
                    count++;
                    result.add(worker.get(num[j]));
                }
            }
        }
        return result;
    }

    //分类的结果利用关系重新排序
    public List<String> uclRank(List<Integer> neighbors, List<String> worker, List<String> winners, int n, String type) {
        List<String> winner = new ArrayList<>();
        List<Map<String, Double>> scores = getAllTypeWorkers(featureExtract.getItems(type).get(n).getChallengeId(), winner);
        Map<String, Integer> index = getIndex(worker);
        int[][] attraction = getRelationEdge(index, scores, winner);
        attraction = getUclAttraction(attraction);
        double[][] repulsion = getWorkerRepulsion(attraction);
        int[][] attr = new int[worker.size()][];
        int[][] replu = new int[worker.size()][];
        for (int i = 0; i < worker.size(); i++) {
            attr[i] = sortWorkerIndex(attraction[i]);
            replu[i] = sortRelation(repulsion[i]);
        }
        List<String> result = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        // 删除worker后面排斥力大的前几名worker
        for (int i = 0; i < worker.size(); i++) {
            if (set.contains(i)) {
                continue;
            }
            int[] array = replu[i];
            int count = 0;
            for (int j = 0; j < array.length; j++) {
                if (i < array[j] && !set.contains(array[j])) {
                    set.add(array[j]);
                    count++;
                }
                if (count >= 1) {
                    break;
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
            if (!res.contains(result.get(i))) {
                res.add(result.get(i));
            }
            for (int j = attr[k].length - 1; j >= 0; j--) {
                if (!res.contains(worker.get(attr[k][j]))) {
                    res.add(worker.get(attr[k][j]));
                    break;
                }
            }
        }
        return res;
    }

    // 吸引力排序
    public int[] sortRelation(double[] num) {
        Map<Integer, Double> map = new HashMap<>();
        for (int i = 0; i < num.length; i++) {
            map.put(i, num[i]);
        }
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return Double.compare(o2.getValue(), o1.getValue());
            }
        });
        int[] res = new int[num.length];
        for (int i = 0; i < num.length; i++) {
            res[i] = list.get(i).getKey();
        }
        return res;
    }

    //最小下标排序
    public int[] sortWorkerIndex(int[] num) {
        int[][] nums = new int[num.length][2];
        for (int i = 0; i < num.length; i++) {
            nums[i][0] = i;
            nums[i][1] = num[i];
        }
        Arrays.sort(nums, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[1] - o2[1];
            }
        });
        int[] res = new int[num.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = nums[i][0];
        }
        return res;
    }

}
