package com.buaa.act.sdp.topcoder.service.recommend.network;

import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/3/17.
 */
@Component
public class Competition {

    private static final Logger logger = LoggerFactory.getLogger(Competition.class);

    @Autowired
    private FeatureExtract featureExtract;
    @Autowired
    private TaskScores taskScores;

    /**
     * 获取当前任务的相似任务中developer的得分，分数多少无限制
     *
     * @param neighbors 相似的任务
     * @param winners   获胜者
     * @param winner
     * @param type      任务类型
     * @return
     */
    public List<Map<String, Double>> getSameTypeDevelopersScores(List<Integer> neighbors, List<String> winners, List<String> winner, String type) {
        Map<Integer, Map<String, Double>> score = taskScores.getDevelopersScores();
        List<TaskItem> items = featureExtract.getTaskItems(type);
        List<Map<String, Double>> list = new ArrayList<>();
        for (int i = 0; i < neighbors.size(); i++) {
            list.add(score.get(items.get(neighbors.get(i)).getChallengeId()));
            winner.add(winners.get(neighbors.get(i)));
        }
        return list;
    }

    /**
     * 获取当前任务的相似任务中developer的得分,只考虑80分以上的
     *
     * @param neighbors
     * @param winners
     * @param winner
     * @param type
     * @return
     */
    public List<Map<String, Double>> getSameTypeDeveloperScores(List<Integer> neighbors, List<String> winners, List<String> winner, String type) {
        List<Map<String, Double>> lists = featureExtract.getDeveloperScore(type);
        List<Map<String, Double>> list = new ArrayList<>();
        for (int i = 0; i < neighbors.size(); i++) {
            list.add(lists.get(neighbors.get(i)));
            winner.add(winners.get(neighbors.get(i)));
        }
        return list;
    }

    /**
     * 获取在当前任务前的所有类型任务中参与的developer，id之前
     *
     * @param taskId 任务的id
     * @param winner 获胜者
     * @return
     */
    public List<Map<String, Double>> getAllTypeDeveloperScores(int taskId, List<String> winner) {
        logger.info("get the winners in all tasks before a new task,taskId" + taskId);
        Map<Integer, Map<String, Double>> scores = taskScores.getDevelopersScores();
        Map<Integer, String> allWinners = taskScores.getWinners();
        List<Map<String, Double>> list = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : allWinners.entrySet()) {
            if (entry.getKey() >= taskId || !scores.containsKey(entry.getKey())) {
                continue;
            }
            list.add(scores.get(entry.getKey()));
            winner.add(entry.getValue());
        }
        return list;
    }

    public Map<String, Integer> getIndex(List<String> developers) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < developers.size(); i++) {
            index.put(developers.get(i), i);
        }
        return index;
    }

    public List<Integer> getNeighbors(int n) {
        List<Integer> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(i);
        }
        return result;
    }

    /**
     * 有向边,worker和worker之间的输赢边
     *
     * @param index   开发者的下标序号
     * @param scores  开发者得分
     * @param winners 任务获胜者
     * @return
     */
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

    /**
     * 赢次数减输的次数
     *
     * @param attraction
     * @return
     */
    public double[][] getAttraction(int[][] attraction) {
        logger.info("compute the attraction between developers");
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
                if ((attraction[i][j] + attraction[j][i]) != 0) {
                    attr[i][j] = 0.5 * (attraction[j][i] - attraction[i][j]) / (attraction[i][j] + attraction[j][i]) + 0.5 * (attraction[i][j] + attraction[j][i]) / (edge[i] + edge[j]);
                }
                attr[j][i] = -attr[i][j];
            }
        }
        return attr;
    }

    /**
     * worker吸引力(worker之间边的和)
     *
     * @param attraction
     * @return
     */
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

    /**
     * worker排斥关系计算
     *
     * @param attraction
     * @return
     */
    public double[][] getDeveloperRepulsion(int[][] attraction) {
        logger.info("compute the repulsion between developers");
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

    /**
     * 综合分类推荐排序和输赢次数排序,每次只处理一名
     *
     * @param developers
     * @param winners    获胜的开发者
     * @param n          选取n个相似的任务
     * @param type       任务类型
     * @return
     */
    public List<String> refine(List<String> developers, List<String> winners, int n, String type) {
        logger.info("using attraction relationship to refine the init recommended developers");
        List<String> winner = new ArrayList<>();
        List<Integer> neighbor = getNeighbors(n);
        List<Map<String, Double>> scores = getSameTypeDeveloperScores(neighbor, winners, winner, type);
        Map<String, Integer> index = getIndex(developers);
        int[][] relation = getRelationEdge(index, scores, winner);
        double[][] attraction = getAttraction(relation);
        int[][] attr = new int[developers.size()][];
        for (int i = 0; i < developers.size(); i++) {
            attr[i] = sortRelation(attraction[i]);
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < developers.size(); i++) {
            if (!result.contains(developers.get(i))) {
                result.add(developers.get(i));
            }
            int[] num = new int[developers.size()];
            for (int j = 0; j < attr[i].length; j++) {
                num[attr[i][j]] = attr[i][j] + j;
            }
            int count = 0;
            num = sortDeveloperIndex(num);
            for (int j = 0; j < num.length; j++) {
                if (num[j] - i <= 5 && !result.contains(developers.get(num[j]))) {
                    if (count >= 5) {
                        break;
                    }
                    count++;
                    result.add(developers.get(num[j]));
                }
            }
        }
        return result;
    }

    /**
     * 分类的结果利用关系重新排序
     *
     * @param developers 开发者
     * @param n
     * @param type       类型
     * @return
     */
    public List<String> uclRank(List<String> developers, int n, String type) {
        logger.info("refine the developers using attraction and repulsion");
        List<String> winner = new ArrayList<>();
        List<Map<String, Double>> scores = getAllTypeDeveloperScores(featureExtract.getTaskItems(type).get(n).getChallengeId(), winner);
        Map<String, Integer> index = getIndex(developers);
        int[][] attraction = getRelationEdge(index, scores, winner);
        attraction = getUclAttraction(attraction);
        double[][] repulsion = getDeveloperRepulsion(attraction);
        int[][] attr = new int[developers.size()][];
        int[][] replu = new int[developers.size()][];
        for (int i = 0; i < developers.size(); i++) {
            attr[i] = sortDeveloperIndex(attraction[i]);
            replu[i] = sortRelation(repulsion[i]);
        }
        List<String> result = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        /**
         *  删除developer后面排斥力大的前几名developer
         */
        for (int i = 0; i < developers.size(); i++) {
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
        for (int i = 0; i < developers.size(); i++) {
            if (set.contains(i)) {
                continue;
            }
            result.add(developers.get(i));
        }
        List<String> res = new ArrayList<>();
        res.addAll(result);
        /**
         * 添加developer吸引力大的developer
         */
        for (int i = 0; i < result.size(); i++) {
            int k = index.get(result.get(i));
            if (!res.contains(result.get(i))) {
                res.add(result.get(i));
            }
            for (int j = attr[k].length - 1; j >= 0; j--) {
                if (!res.contains(developers.get(attr[k][j]))) {
                    res.add(developers.get(attr[k][j]));
                    break;
                }
            }
        }
        return res;
    }

    /**
     * 吸引力排序
     *
     * @param num
     * @return
     */
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

    /**
     * 最小下标排序
     *
     * @param num
     * @return
     */
    public int[] sortDeveloperIndex(int[] num) {
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
