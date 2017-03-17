package com.buaa.act.sdp.service.recommend.network;

import com.buaa.act.sdp.dao.ChallengeItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/17.
 */
@Component
public class Collaboration {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    private Map<Integer,List<Integer>>projectIdToChallengeIds;

    public Collaboration() {
        projectIdToChallengeIds=new HashMap<>();
    }

    public Map<Integer,List<Integer>>getProjectToChallenges(){
        List<Map<String,Integer>>list=challengeItemDao.getProjectId();
        List<Integer>challengeIds;
        for(Map<String,Integer>map:list) {
            challengeIds=projectIdToChallengeIds.getOrDefault(map.get("projectId"),null);
            if(challengeIds!=null) {
                challengeIds.add(map.get("challengeId"));
            }else{
                challengeIds=new ArrayList<>();
                challengeIds.add(map.get("challengeId"));
                projectIdToChallengeIds.put(map.get("projectId"),challengeIds);
            }
        }
        return projectIdToChallengeIds;
    }

}
