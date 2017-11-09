package com.buaa.act.sdp.topcoder.service.recommend.result;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.network.Collaboration;
import com.buaa.act.sdp.topcoder.service.statistics.MsgFilter;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/6/5.
 */
@Service
public class TeamResult {

    @Autowired
    private ProjectMsg projectMsg;
    @Autowired
    private MsgFilter msgFilter;
    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private TaskResult taskResult;
    @Autowired
    private Collaboration collaboration;

    /**
     * 候选者下标
     *
     * @param workers    每一个任务的推荐开发者候选集
     * @param allWorkers 得到所有不重复开发者
     * @return
     */
    public Map<String, Integer> getWorkerIndex(List<List<String>> workers, List<String> allWorkers) {
        Map<String, Integer> workerIndex = new HashMap<>();
        int index = 0;
        for (List<String> list : workers) {
            for (String worker : list) {
                if (!workerIndex.containsKey(worker)) {
                    allWorkers.add(worker);
                    workerIndex.put(worker, index++);
                }
            }
        }
        return workerIndex;
    }

    /**
     * 为一个project内单个任务推荐开发者
     *
     * @param projectId
     * @return
     */
    public List<List<String>> recommendWorkers(int projectId) {
        List<Integer> ids = projectMsg.getProjectToChallenges().get(projectId);
        Set<Integer> sets = new HashSet(ids.size());
        sets.addAll(ids);
        List<List<String>> workers = new ArrayList<>(ids.size());
        List<ChallengeItem> items = taskMsg.getChallenges(sets);
        for (ChallengeItem item : items) {
            workers.add(taskResult.recommendWorkers(item));
        }
        return workers;
    }

    /**
     * 获取project待推荐worker之间的协作力
     *
     * @param taskIds     每一个项目中包含的taskId
     * @param workerIndex 开发者序号
     * @return
     */
    public double[][] getCollaborations(List<List<Integer>> taskIds, Map<String, Integer> workerIndex) {
        return collaboration.generateCollaboration(workerIndex, taskIds);
    }

    /**
     * 计算新Team替换原Team概率
     *
     * @param before 先前团队的协作值
     * @param after  当前团队协作值
     * @return
     */
    public double probability(double before, double after) {
        double previous = Math.exp(-before), current = Math.exp(-after);
        double max = Math.max(previous, current);
        return current / max;
    }

    /**
     * 选取的worker组合对应的团队协作值
     *
     * @param workerIndex   团队中每一个worker下标
     * @param collaboration worker之间协作值矩阵
     * @return
     */
    public double calTeamCollaboration(int[] workerIndex, double[][] collaboration) {
        double result = 0.0;
        for (int i = 0; i < workerIndex.length; i++) {
            for (int j = i + 1; j < workerIndex.length; j++) {
                result += collaboration[workerIndex[i]][workerIndex[j]];
            }
        }
        return result;
    }

    /**
     * 替换一个角色后的团队协作力
     *
     * @param teamStrength  当前团队协作值
     * @param index         团队内每一个开发者下标
     * @param collaboration
     * @param role          待替换的任务下标
     * @param workerId      开发者下标
     * @return
     */
    public double generateNewTeam(double teamStrength, int[] index, double[][] collaboration, int role, int workerId) {
        if (index[role] != workerId) {
            for (int i = 0; i < index.length; i++) {
                if (i == role) {
                    continue;
                }
                teamStrength += (collaboration[index[i]][workerId] - collaboration[index[i]][index[role]]);
            }
        }
        return teamStrength;
    }

    /**
     * 寻找最佳Team，迭代maxLogit算法
     * @param bestIndex 项目中每个任务的开发者
     * @param workerIndex   开发者名字-下标映射关系
     * @param workers       每一个任务的开发者候选集
     * @param collaboration 开发者协作值
     * @return
     */
    public double maxLogit(int[] bestIndex, Map<String, Integer> workerIndex, List<List<String>> workers, double[][] collaboration) {
        Random random = new Random();
        int[] index = new int[workers.size()];
        for (int i = 0; i < index.length; i++) {
            int t = random.nextInt(workers.get(i).size());
            index[i] = workerIndex.get(workers.get(i).get(t));
            bestIndex[i] = index[i];
        }
        double teamStrength = calTeamCollaboration(index, collaboration), newTeamStrength, bestTeamStrength = teamStrength;
        int role, worker;
        for (int i = 0; i < Constant.ITERATIONS; i++) {
            role = random.nextInt(index.length);
            worker = workerIndex.get(workers.get(role).get(random.nextInt(workers.get(role).size())));
            newTeamStrength = generateNewTeam(teamStrength, index, collaboration, role, worker);
            if (probability(teamStrength, newTeamStrength) >= Math.random()) {
                index[role] = worker;
                teamStrength = newTeamStrength;
                if (teamStrength > bestTeamStrength) {
                    bestTeamStrength = teamStrength;
                    for (int t = 0; t < index.length; t++) {
                        bestIndex[t] = index[t];
                    }
                }
            }
        }
        return bestTeamStrength;
    }

    /**
     * @param bestIndex 项目中每个任务的开发者
     * @param workerIndex 开发者名字-下标映射关系
     * @param workers 每一个任务的开发者候选集
     * @param collaboration 开发者协作值
     * @return
     */
    public double searchForMaxCollaboration(int[] bestIndex, Map<String, Integer> workerIndex, List<List<String>> workers, double[][] collaboration) {
        Random random = new Random();
        int[] index = new int[workers.size()];
        int t, m, position, role;
        for (int i = 0; i < index.length; i++) {
            t = random.nextInt(workers.get(i).size());
            index[i] = workerIndex.get(workers.get(i).get(t));
            bestIndex[i] = index[i];
        }
        double teamStrength = calTeamCollaboration(index, collaboration), newTeamStrength, currentScore;
        List<String> worker;
        for (int i = 0; i < Constant.ITERATIONS; i++) {
            role = 0;
            position = index[role];
            newTeamStrength = teamStrength;
            for (int j = 0; j < workers.size(); j++) {
                worker = workers.get(j);
                for (int k = 0; k < worker.size(); k++) {
                    m = workerIndex.get(worker.get(k));
                    currentScore = generateNewTeam(teamStrength, index, collaboration, j, m);
                    if (currentScore > newTeamStrength) {
                        newTeamStrength = currentScore;
                        role = j;
                        position = m;
                    }
                }
            }
            if (newTeamStrength > teamStrength) {
                teamStrength = newTeamStrength;
                index[role] = position;
            }
        }
        return teamStrength;
    }

    /**
     * 为项目寻找最佳团队开发者，ICMLA方法
     *
     * @param projectId
     * @return
     */
    public List<String> findBestTeamMaxLogit(int projectId) {
        List<List<Integer>> taskIds = msgFilter.getProjectAndChallenges(projectId);
        List<List<String>> workers = recommendWorkers(projectId);
        List<String> allWorkers = new ArrayList<>();
        Map<String, Integer> workerIndex = getWorkerIndex(workers, allWorkers);
        double[][] collaboration = getCollaborations(taskIds, workerIndex);
        int[] bestIndex = new int[workers.size()];
        double teamStrength = maxLogit(bestIndex, workerIndex, workers, collaboration);
        System.out.println(teamStrength);
        List<String> bestTeam = new ArrayList<>(bestIndex.length);
        for (int i = 0; i < bestIndex.length; i++) {
            bestTeam.add(allWorkers.get(bestIndex[i]));
        }
        return bestTeam;
    }

    /**
     * 为项目寻找最佳团队开发者：每次寻找最大的协作值，记录相应的任务和对应替换开发者
     * @param projectId
     * @return
     */
    public List<String> findBestTeamMaxCollaboration(int projectId) {
        List<List<Integer>> taskIds = msgFilter.getProjectAndChallenges(projectId);
        List<List<String>> workers = recommendWorkers(projectId);
        List<String> allWorkers = new ArrayList<>();
        Map<String, Integer> workerIndex = getWorkerIndex(workers, allWorkers);
        double[][] collaboration = getCollaborations(taskIds, workerIndex);
        int[] bestIndex = new int[workers.size()];
        double teamStrength = searchForMaxCollaboration(bestIndex, workerIndex, workers, collaboration);
        System.out.println(teamStrength);
        List<String> bestTeam = new ArrayList<>(bestIndex.length);
        for (int i = 0; i < bestIndex.length; i++) {
            bestTeam.add(allWorkers.get(bestIndex[i]));
        }
        return bestTeam;
    }
}
