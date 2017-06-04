package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.network.Collaboration;
import com.buaa.act.sdp.service.recommend.result.TaskResult;
import com.buaa.act.sdp.service.statistics.MsgFilter;
import com.buaa.act.sdp.service.statistics.ProjectMsg;
import com.buaa.act.sdp.service.statistics.TaskMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/6/1.
 */
@Service
public class TeamRecommend {

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

    public double[][]getCollaborations(int projectId){
        List<List<Integer>>taskIds=msgFilter.getProjectAndChallenges(projectId);
        List<Integer>ids=projectMsg.getProjectToChallenges().get(projectId);
        Set<Integer> sets=new HashSet(ids.size());
        sets.addAll(ids);
        List<List<String>>workers=new ArrayList<>(ids.size());
        List<ChallengeItem>items=taskMsg.getChallenges(sets);
        for(ChallengeItem item:items){
            workers.add(taskResult.recommendWorkers(item));
        }
        return collaboration.generateCollaboration(workers,taskIds);
    }
}
