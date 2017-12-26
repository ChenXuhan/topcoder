package com.buaa.act.sdp.topcoder.service.recommend.result;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.common.TaskRecommendThread;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.recommend.network.Collaboration;
import com.buaa.act.sdp.topcoder.service.statistics.MsgFilter;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by yang on 2017/6/5.
 */
@Service
public class TeamRecommend {

    private static final Logger logger = LoggerFactory.getLogger(TeamRecommend.class);

    @Autowired
    private ProjectMsg projectMsg;
    @Autowired
    private MsgFilter msgFilter;
    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private DeveloperRecommend developerRecommend;
    @Autowired
    private Collaboration collaboration;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 候选者下标
     *
     * @param developers    每一个任务的推荐开发者候选集
     * @param allDevelopers 得到所有不重复开发者
     * @return
     */
    public Map<String, Integer> getDeveloperIndex(List<List<String>> developers, List<String> allDevelopers) {
        Map<String, Integer> developerIndex = new HashMap<>();
        int index = 0;
        for (List<String> list : developers) {
            for (String worker : list) {
                if (!developerIndex.containsKey(worker)) {
                    allDevelopers.add(worker);
                    developerIndex.put(worker, index++);
                }
            }
        }
        return developerIndex;
    }


    /**
     * 获取一个项目内的任务（三种类型）
     *
     * @param projectId
     * @return
     */
    public List<TaskItem> getRecommendTasksInProject(int projectId) {
        logger.info("get recommended tasks in a project,projectId=" + projectId);
        List<Integer> ids = projectMsg.getProjectToTasksMapping().get(projectId);
        if (ids == null) {
            return null;
        }
        Set<Integer> sets = new HashSet(ids.size());
        sets.addAll(ids);
        return taskMsg.getTasks(sets);
    }

    /**
     * 为一个project内单个任务推荐开发者
     *
     * @param items
     * @return
     */
    public List<List<String>> recommendDevelopersForTasksInProject(List<TaskItem> items) throws Exception {
        logger.info("recommend developers for each task in a project");
        List<List<String>> developers = new ArrayList<>(items.size());
        List<Future<List<String>>> futureList = new ArrayList<>(items.size());
        for (TaskItem item : items) {
            futureList.add(recommendDevelopersForTask(item));
        }
        for (Future<List<String>> future : futureList) {
            developers.add(future.get());
        }
        return developers;
    }

    /**
     * 利用线程池为项目内每个任务推荐
     *
     * @param item
     * @return
     */
    public Future<List<String>> recommendDevelopersForTask(TaskItem item) {
        TaskRecommendThread taskRecommendThread = new TaskRecommendThread(item, developerRecommend);
        return threadPoolTaskExecutor.submit(taskRecommendThread);
    }

    /**
     * 获取project待推荐worker之间的协作力
     *
     * @param taskIds        每一个项目中包含的taskId
     * @param developerIndex 开发者序号
     * @return
     */
    public double[][] getCollaborations(List<List<Integer>> taskIds, Map<String, Integer> developerIndex) {
        return collaboration.generateCollaboration(developerIndex, taskIds);
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
     * @param developerIndex 团队中每一个worker下标
     * @param collaboration  worker之间协作值矩阵
     * @return
     */
    public double calculateTeamCollaboration(int[] developerIndex, double[][] collaboration) {
        double result = 0.0;
        for (int i = 0; i < developerIndex.length; i++) {
            for (int j = i + 1; j < developerIndex.length; j++) {
                result += collaboration[developerIndex[i]][developerIndex[j]];
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
     * @param developerId   开发者下标
     * @return
     */
    public double generateNewTeam(double teamStrength, int[] index, double[][] collaboration, int role, int developerId) {
        if (index[role] != developerId) {
            for (int i = 0; i < index.length; i++) {
                if (i == role) {
                    continue;
                }
                teamStrength += (collaboration[index[i]][developerId] - collaboration[index[i]][index[role]]);
            }
        }
        return teamStrength;
    }

    /**
     * 寻找最佳Team，迭代maxLogit算法
     *
     * @param bestIndex      项目中每个任务的开发者
     * @param developerIndex 开发者名字-下标映射关系
     * @param developers     每一个任务的开发者候选集
     * @param collaboration  开发者协作值
     * @return
     */
    public double maxLogit(int[] bestIndex, Map<String, Integer> developerIndex, List<List<String>> developers, double[][] collaboration) {
        Random random = new Random(System.currentTimeMillis());
        int[] index = new int[developers.size()];
        int t;
        for (int i = 0; i < index.length; i++) {
            t = random.nextInt(developers.get(i).size());
            index[i] = developerIndex.get(developers.get(i).get(t));
            bestIndex[i] = index[i];
        }
        double teamStrength = calculateTeamCollaboration(index, collaboration), newTeamStrength, bestTeamStrength = teamStrength;
        int role, worker;
        for (int i = 0; i < Constant.ITERATIONS; i++) {
            role = random.nextInt(index.length);
            worker = developerIndex.get(developers.get(role).get(random.nextInt(developers.get(role).size())));
            newTeamStrength = generateNewTeam(teamStrength, index, collaboration, role, worker);
            if (probability(teamStrength, newTeamStrength) >= Math.random()) {
                index[role] = worker;
                teamStrength = newTeamStrength;
                if (teamStrength > bestTeamStrength) {
                    bestTeamStrength = teamStrength;
                    System.arraycopy(index, 0, bestIndex, 0, index.length);
                }
            }
        }
        return bestTeamStrength;
    }

    /**
     * @param bestIndex      项目中每个任务的开发者
     * @param developerIndex 开发者名字-下标映射关系
     * @param developers     每一个任务的开发者候选集
     * @param collaboration  开发者协作值
     * @return
     */
    public double searchForMaxCollaboration(int[] bestIndex, Map<String, Integer> developerIndex, List<List<String>> developers, double[][] collaboration) {
        int t, m, position, role;
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < bestIndex.length; i++) {
            t = random.nextInt(developers.get(i).size());
            bestIndex[i] = developerIndex.get(developers.get(i).get(t));
        }
        double teamStrength = calculateTeamCollaboration(bestIndex, collaboration), newTeamStrength, currentScore;
        List<String> worker;
        logger.info("searching the best role each step");
        for (int i = 0; i < Constant.ITERATIONS; i++) {
            role = 0;
            position = bestIndex[role];
            newTeamStrength = teamStrength;
            for (int j = 0; j < developers.size(); j++) {
                worker = developers.get(j);
                for (int k = 0; k < worker.size(); k++) {
                    m = developerIndex.get(worker.get(k));
                    currentScore = generateNewTeam(teamStrength, bestIndex, collaboration, j, m);
                    if (currentScore > newTeamStrength) {
                        newTeamStrength = currentScore;
                        role = j;
                        position = m;
                    }
                }
            }
            if (newTeamStrength > teamStrength) {
                teamStrength = newTeamStrength;
                bestIndex[role] = position;
            }
        }
        return teamStrength;
    }

    /**
     * 从每一个任务的Top 5个开发者中选取最佳组合
     *
     * @param result         团队协作值最大值
     * @param index          每一个角色的位置0-5
     * @param developers     每一个任务的开发者候选集
     * @param developerIndex
     * @param collaboration
     * @param position       确定当前第k个任务的开发者
     * @param team           项目团队
     */
    public void maxCollaboration(double[] result, int[] index, List<List<String>> developers, Map<String, Integer> developerIndex, double[][] collaboration, int position, List<String> team) {
        if (position == index.length) {
            int[] bestIndex = new int[index.length];
            for (int i = 0; i < bestIndex.length; i++) {
                bestIndex[i] = developerIndex.get(developers.get(i).get(index[i]));
            }
            double teamStrength = calculateTeamCollaboration(bestIndex, collaboration);
            if (result[0] > teamStrength) {
                result[0] = teamStrength;
                for (int i = 0; i < team.size(); i++) {
                    team.set(i, developers.get(i).get(index[i]));
                }
            }
            return;
        }
        int top = 5;
        for (int i = 0; i < developers.get(position).size() && i < top; i++) {
            index[position] = i;
            maxCollaboration(result, index, developers, developerIndex, collaboration, position + 1, team);
        }
    }

    /**
     * 从Top5 developer中选择最佳团队组合
     *
     * @param projectId
     * @return
     */
    public Map<Integer, String> findBestTeamTopKDevelopers(int projectId) throws Exception {
        logger.info("select a team in top five de developers for a project,projectId" + projectId);
        List<List<Integer>> taskIds = msgFilter.getProjectAndTasks(projectId);
        List<TaskItem> items = getRecommendTasksInProject(projectId);
        List<List<String>> workers = recommendDevelopersForTasksInProject(items);
        List<String> allWorkers = new ArrayList<>();
        Map<String, Integer> workerIndex = getDeveloperIndex(workers, allWorkers);
        double[][] collaboration = getCollaborations(taskIds, workerIndex);
        int[] index = new int[workers.size()];
        double[] teamStrength = new double[1];
        List<String> team = new ArrayList<>(workers.size());
        for (int i = 0; i < workers.size(); i++) {
            team.add(workers.get(i).get(0));
        }
        maxCollaboration(teamStrength, index, workers, workerIndex, collaboration, 0, team);
        Map<Integer, String> result = new HashMap<>(team.size());
        for (int i = 0; i < workers.size(); i++) {
            result.put(items.get(i).getChallengeId(), team.get(i));
        }
        return result;
    }

    /**
     * 为项目寻找最佳团队开发者，ICMLA方法
     *
     * @param projectId
     * @return
     */
    public Map<Integer, String> findBestTeamMaxLogit(int projectId) throws Exception {
        logger.info("using max-logit to recommend a team for a project,projectId" + projectId);
        List<List<Integer>> taskIds = msgFilter.getProjectAndTasks(projectId);
        List<TaskItem> items = getRecommendTasksInProject(projectId);
        List<List<String>> workers = recommendDevelopersForTasksInProject(items);
        List<String> allWorkers = new ArrayList<>();
        Map<String, Integer> workerIndex = getDeveloperIndex(workers, allWorkers);
        double[][] collaboration = getCollaborations(taskIds, workerIndex);
        int[] bestIndex = new int[workers.size()];
        maxLogit(bestIndex, workerIndex, workers, collaboration);
        List<String> bestTeam = new ArrayList<>(bestIndex.length);
        for (int i = 0; i < bestIndex.length; i++) {
            bestTeam.add(allWorkers.get(bestIndex[i]));
        }
        Map<Integer, String> result = new HashMap<>(bestTeam.size());
        for (int i = 0; i < workers.size(); i++) {
            result.put(items.get(i).getChallengeId(), bestTeam.get(i));
        }
        return result;
    }

    /**
     * 为项目寻找最佳团队开发者：每次寻找最大的协作值，记录相应的任务和对应替换开发者
     *
     * @param projectId
     * @return
     */
    public Map<Integer, String> generateBestTeamUsingHeuristic(int projectId) throws Exception {
        logger.info("recommend a team for project using heuristic algorithm");
        List<TaskItem> items = getRecommendTasksInProject(projectId);
        if (items == null) {
            return null;
        }
        if(items.size()==0){
            return new HashMap<>();
        }
        List<List<Integer>> taskIds = msgFilter.getProjectAndTasks(projectId);
        List<List<String>> workers = recommendDevelopersForTasksInProject(items);
        List<String> allWorkers = new ArrayList<>();
        Map<String, Integer> workerIndex = getDeveloperIndex(workers, allWorkers);
        double[][] collaboration = getCollaborations(taskIds, workerIndex);
        int[] bestIndex = new int[workers.size()];
        searchForMaxCollaboration(bestIndex, workerIndex, workers, collaboration);
        List<String> bestTeam = new ArrayList<>(bestIndex.length);
        for (int i = 0; i < bestIndex.length; i++) {
            bestTeam.add(allWorkers.get(bestIndex[i]));
        }
        Map<Integer, String> result = new HashMap<>(bestTeam.size());
        for (int i = 0; i < workers.size(); i++) {
            result.put(items.get(i).getChallengeId(), bestTeam.get(i));
        }
        return result;
    }

    public double maxLogitTeam(Map<String, Integer> developerIndex, double[][] collaboration, List<List<String>> developers) {
        int[] bestIndex = new int[developers.size()];
        double teamStrength = maxLogit(bestIndex, developerIndex, developers, collaboration);
        return teamStrength;
    }

    public double heuristicTeam(Map<String, Integer> developerIndex, List<List<String>> developers, double[][] collaboration) {
        int[] bestIndex = new int[developers.size()];
        double teamStrength = searchForMaxCollaboration(bestIndex, developerIndex, developers, collaboration);
        return teamStrength;
    }

    public double topKDeveloperTeam(List<List<String>> developers, Map<String, Integer> developerIndex, double[][] collaboration) {
        int[] index = new int[developers.size()];
        double[] teamStrength = new double[1];
        List<String> team = new ArrayList<>(developers.size());
        for (int i = 0; i < developers.size(); i++) {
            team.add(developers.get(i).get(0));
        }
        maxCollaboration(teamStrength, index, developers, developerIndex, collaboration, 0, team);
        return teamStrength[0];
    }

}
