package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
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
    private List<ChallengeItem> codes;
    private List<ChallengeItem> assemblys;
    private List<ChallengeItem> first2finishs;

    public ProjectMsg() {
        challengeToProject = new HashMap<>();
        projectIdToChallengeIds = new HashMap<>();
    }

    // 返回所有project中所有的challengeId
    public Map<Integer, List<Integer>> getProjectToChallenges() {
        if (projectIdToChallengeIds.isEmpty()) {
            challengeProjectMapping();
        }
        return projectIdToChallengeIds;
    }

    public void challengeProjectMapping() {
        List<Map<String, Integer>> list = challengeItemDao.getProjectId();
        List<Integer> challengeIds;
        for (Map<String, Integer> map : list) {
            challengeIds = projectIdToChallengeIds.getOrDefault(map.get("projectId"), null);
            if (challengeIds != null) {
                challengeIds.add(map.get("challengeId"));
            } else {
                challengeIds = new ArrayList<>();
                challengeIds.add(map.get("challengeId"));
                projectIdToChallengeIds.put(map.get("projectId"), challengeIds);
            }
            challengeToProject.put(map.get("challengeId"), map.get("projectId"));
        }
    }

    public Map<Integer, Integer> getChallengeToProject() {
        if (challengeToProject.isEmpty()) {
            challengeProjectMapping();
        }
        return challengeToProject;
    }


}
