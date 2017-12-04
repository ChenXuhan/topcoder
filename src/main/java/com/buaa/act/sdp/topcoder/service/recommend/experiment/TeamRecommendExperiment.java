package com.buaa.act.sdp.topcoder.service.recommend.experiment;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.result.TeamRecommend;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private TeamRecommend teamecommend;

    public List<Integer> getTestProjectId() {
        List<ChallengeItem> items = taskMsg.getTasks(false);
        Map<Integer, Integer> taskToProject = projectMsg.getChallengeToProject();
        Map<Integer, List<Integer>> projectToTask = new HashMap<>();
        int projectId;
        for (ChallengeItem item : items) {
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
        Collections.sort(projectIdList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        return projectIdList.subList(projectIdList.size() * 5 / 10, projectIdList.size());
    }

    public void compareTeamRecommendResult() {
        List<Integer> projectIdList = getTestProjectId();
        int count = 0;
        for (int projectId : projectIdList) {
            if (teamecommend.teamRecommend(projectId) > teamecommend.findBestTeamMaxLogit(projectId)) {
                count++;
            }
        }
        System.out.println(1.0 * count / projectIdList.size());
    }

}
