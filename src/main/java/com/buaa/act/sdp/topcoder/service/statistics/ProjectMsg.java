package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/6/1.
 */
@Component
public class ProjectMsg {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    private Map<Integer, List<Integer>> projectIdToChallengeIds;
    private Map<Integer, Integer> challengeToProject;

    public ProjectMsg() {
        challengeToProject = new HashMap<>();
        projectIdToChallengeIds = new HashMap<>();
    }

    /**
     * project中所有的challengeId
     * @return
     */
    public synchronized Map<Integer, List<Integer>> getProjectToChallenges() {
        if (projectIdToChallengeIds.isEmpty()) {
            challengeProjectMapping();
        }
        return projectIdToChallengeIds;
    }

    /**
     * challenge和project对应关系
     */
    public void challengeProjectMapping() {
        List<Map<String, Object>> list = challengeItemDao.getProjectId();
        List<Integer> challengeIds;
        int challengeId, projectId;
        String type;
        for (Map<String, Object> map : list) {
            challengeId = Integer.parseInt(map.get("challengeId").toString());
            projectId = Integer.parseInt(map.get("projectId").toString());
            type = map.get("challengeType").toString();
            if (type.equals("Code") || type.equals("First2Finish") || type.equals("Assembly Competition")) {
                challengeIds = projectIdToChallengeIds.getOrDefault(projectId, null);
                if (challengeIds != null) {
                    challengeIds.add(challengeId);
                } else {
                    challengeIds = new ArrayList<>();
                    challengeIds.add(challengeId);
                    projectIdToChallengeIds.put(projectId, challengeIds);
                }
            }
            challengeToProject.put(challengeId, projectId);
        }
    }

    /**
     * challenge对应的项目
     * @return
     */
    public synchronized Map<Integer, Integer> getChallengeToProject() {
        if (challengeToProject.isEmpty()) {
            challengeProjectMapping();
        }
        return challengeToProject;
    }

    public synchronized void update(){
        projectIdToChallengeIds.clear();
        challengeToProject.clear();
        challengeProjectMapping();
    }
}
