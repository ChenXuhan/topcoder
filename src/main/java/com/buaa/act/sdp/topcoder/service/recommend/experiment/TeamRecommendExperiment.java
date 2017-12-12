package com.buaa.act.sdp.topcoder.service.recommend.experiment;

import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.recommend.result.TeamRecommend;
import com.buaa.act.sdp.topcoder.service.statistics.MsgFilter;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/12/4.
 */
@Service
public class TeamRecommendExperiment {

    @Autowired
    private ProjectMsg projectMsg;
    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private TeamRecommend teamRecommend;
    @Autowired
    private MsgFilter msgFilter;

    public List<Integer> getTestProjectId() {
        List<TaskItem> code = taskMsg.getItems("Code");
        List<TaskItem> f2f = taskMsg.getItems("First2Finish");
        List<TaskItem> assembly = taskMsg.getItems("Assembly Competition");
        List<TaskItem> items = new ArrayList<>(code.size() * 2 / 3 + f2f.size() * 2 / 3 + assembly.size() * 2 / 3);
        items.addAll(code.subList(code.size() / 3, code.size()));
        items.addAll(f2f.subList(f2f.size() / 3, f2f.size()));
        items.addAll(assembly.subList(assembly.size() / 3, assembly.size()));
        Map<Integer, Integer> taskToProject = projectMsg.getTaskToProjectMapping();
        Map<Integer, List<Integer>> projectToTask = new HashMap<>();
        int projectId;
        for (TaskItem item : items) {
            projectId = taskToProject.get(item.getChallengeId());
            List<Integer> list = projectToTask.get(projectId);
            if (list == null) {
                list = new ArrayList<>();
                projectToTask.put(projectId, list);
            }
            list.add(item.getChallengeId());
        }
        List<Integer> projectIdList = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : projectToTask.entrySet()) {
            if (entry.getValue().size() >= 10) {
                projectIdList.add(entry.getKey());
            }
        }
        return projectIdList;
    }

    public void compareTeamRecommendResult() throws Exception {
        List<Integer> projectIdList = getTestProjectId();
        int compareMaxlogit = 0, compareTopK = 0;
        for (int projectId : projectIdList) {
            List<List<Integer>> taskIds = msgFilter.getProjectAndTasks(projectId);
            List<TaskItem> items = teamRecommend.getRecommendTasksInProject(projectId);
            List<List<String>> workers = teamRecommend.recommendDevelopersForTasksInProject(items);
            List<String> allWorkers = new ArrayList<>();
            Map<String, Integer> workerIndex = teamRecommend.getDeveloperIndex(workers, allWorkers);
            double[][] collaboration = teamRecommend.getCollaborations(taskIds, workerIndex);
            double a = teamRecommend.maxLogitTeam(workerIndex, collaboration, workers);
            double b = teamRecommend.heuristicTeam(workerIndex, workers, collaboration);
            double c = teamRecommend.topKDeveloperTeam(workers, workerIndex, collaboration);
            if (b > a) {
                compareMaxlogit++;
            }
            if (b > c) {
                compareTopK++;
            }
        }
        System.out.println(1.0 * compareMaxlogit / projectIdList.size() + "\t" + 1.0 * compareTopK / projectIdList.size());
    }

}
