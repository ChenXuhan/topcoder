package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2017/11/17.
 */
@Service
public class TaskService {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    public ChallengeItem getChallengeById(int challengeId) {
        return challengeItemDao.getChallengeItemById(challengeId);
    }

    public boolean projectExist(int projectId) {
        if (projectId <= 0) {
            return false;
        }
        int num = challengeItemDao.projectExist(projectId);
        return num > 0 ? true : false;
    }

    public List<Integer> getProjectTasks(int projectId) {
        if (projectId <= 0) {
            return new ArrayList<>();
        }
        return challengeItemDao.getProjectTasks(projectId);
    }

    public List<Integer>getAllTasks(){
        return challengeItemDao.getChallengeIds();
    }
}
