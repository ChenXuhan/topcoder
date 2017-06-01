package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.model.challenge.ChallengeRegistrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/6/1.
 */
@Component
public class TaskScores {

    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private TaskMsg taskMsg;

    public Map<Integer, Map<String, Double>> getAllWorkerScores(List<String>types) {
        List<ChallengeRegistrant> challengeRegistrants = challengeRegistrantDao.getAllRegistrant();
        Map<Integer,Map<String,Double>>allScores=new HashMap<>();
        Map<String, Double> score;
        for (ChallengeRegistrant challengeRegistrant : challengeRegistrants) {
            score = allScores.getOrDefault(challengeRegistrant.getChallengeID(), null);
            if (score != null) {
                score.put(challengeRegistrant.getHandle(), 0.0);
            } else {
                score = new HashMap<>();
                score.put(challengeRegistrant.getHandle(), 0.0);
                allScores.put(challengeRegistrant.getChallengeID(), score);
            }
        }
        if(types!=null){
            for(String type:types){
                updateWorkerScores(allScores,type);
            }
        }
        return allScores;
    }

    // 依据submission表更新worker的得分
    public void updateWorkerScores(Map<Integer,Map<String,Double>>allScores,String type) {
        Map<Integer, Map<String, Double>> submissionScores = taskMsg.getScores(type);
        if (submissionScores != null) {
            Map<String, Double> registrant, submission;
            for (Map.Entry<Integer, Map<String, Double>> entry : submissionScores.entrySet()) {
                if (allScores.containsKey(entry.getKey())) {
                    registrant = allScores.get(entry.getKey());
                    submission = entry.getValue();
                    for (Map.Entry<String, Double> temp : submission.entrySet()) {
                        if (temp.getValue() < 0.1) {
                            registrant.put(temp.getKey(), 1.0);
                        } else {
                            registrant.put(temp.getKey(), temp.getValue());
                        }
                    }
                } else {
                    registrant = new HashMap<>();
                    submission = entry.getValue();
                    for (Map.Entry<String, Double> temp : submission.entrySet()) {
                        if (temp.getValue() < 0.1) {
                            registrant.put(temp.getKey(), 1.0);
                        } else {
                            registrant.put(temp.getKey(), temp.getValue());
                        }
                    }
                    allScores.put(entry.getKey(), registrant);
                }
            }
        }
    }
}
