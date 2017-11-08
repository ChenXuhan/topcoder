package com.buaa.act.sdp.service.recommend.result;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.network.Collaboration;
import com.buaa.act.sdp.service.statistics.MsgFilter;
import com.buaa.act.sdp.service.statistics.ProjectMsg;
import com.buaa.act.sdp.service.statistics.TaskMsg;
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

    /*

     */
    // 候选者下标
    public Map<String, Integer> getWorkerIndex(List<List<String>> workers,List<String>allWorkers) {
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

    // 为一个project内单个任务推荐开发者
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

    // 获取project待推荐worker之间的协作力
    public double[][] getCollaborations(List<List<Integer>> preTaskIds, Map<String, Integer> workerIndex) {
        return collaboration.generateCollaboration(workerIndex, preTaskIds);
    }

    // 计算新Team替换原Team概率
    public double probability(double before, double after) {
        double previous = Math.exp(-before), current = Math.exp(-after);
        double max = Math.max(previous, current);
        return current / max;
    }

    // 选取的worker组合对应的团队协作值
    public double teamCollaboration(int[] index, double[][] collaboration) {
        double result = 0.0;
        for (int i = 0; i < index.length; i++) {
            for (int j = i + 1; j < index.length; j++) {
                result+=collaboration[index[i]][index[j]];
            }
        }
        return result;
    }

    // 替换一个角色后的团队协作力
    public double teamCollaborationChanged(double teamStrength, int[] index, double[][] collaboration, int role, int worker) {
        if (index[role] != worker) {
            for(int i=0;i<index.length;i++){
                if(i==role){
                    continue;
                }
                teamStrength+=(collaboration[index[i]][worker]-collaboration[index[i]][index[role]]);
            }
        }
        return teamStrength;
    }

    // 最佳Team，迭代选取maxLogit算法
    public int[] maxLogit(Map<String, Integer> workerIndex, List<List<String>> workers, double[][] collaboration) {
        Random random = new Random();
        int[] index = new int[workers.size()];
        int[] bestIndex = new int[workers.size()];
        for (int i = 0; i < index.length; i++) {
            int t=random.nextInt(workers.get(i).size());
            index[i] = workerIndex.get(workers.get(i).get(t));
            bestIndex[i]=index[i];
        }
        double teamStrength = teamCollaboration(index, collaboration),newTeamStrength,bestTeamStrength=teamStrength;
        int role,worker;
        for(int i=0;i<1000;i++) {
            role = random.nextInt(index.length);
            worker = workerIndex.get(workers.get(role).get(random.nextInt(workers.get(role).size())));
            newTeamStrength=teamCollaborationChanged(teamStrength,index,collaboration,role,worker);
            if(probability(teamStrength,newTeamStrength)>=Math.random()){
                index[role]=worker;
                teamStrength=newTeamStrength;
                if(teamStrength>bestTeamStrength){
                    bestTeamStrength=teamStrength;
                    for(int t=0;t<index.length;t++){
                        bestIndex[t]=index[t];
                    }
                }
            }
        }
        System.out.println("max:="+bestTeamStrength);
        return bestIndex;
    }

    public List<String> findBestTeam(int projectId) {
        List<List<Integer>> taskIds = msgFilter.getProjectAndChallenges(projectId);
        List<List<String>> workers = recommendWorkers(projectId);
        List<String>allWorkers=new ArrayList<>();
        Map<String, Integer> workerIndex = getWorkerIndex(workers,allWorkers);
        double[][] collaboration = getCollaborations(taskIds, workerIndex);
        int[]teamIndex= maxLogit(workerIndex, workers, collaboration);
        List<String>bestTeam=new ArrayList<>(teamIndex.length);
        for(int i=0;i<teamIndex.length;i++){
            bestTeam.add(allWorkers.get(teamIndex[i]));
        }
        return bestTeam;
    }
}
