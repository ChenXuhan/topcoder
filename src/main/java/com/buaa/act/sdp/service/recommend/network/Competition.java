package com.buaa.act.sdp.service.recommend.network;

import com.buaa.act.sdp.bean.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.service.recommend.FeatureExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/17.
 */
@Component
public class Competition {
    private Map<Integer, Map<String, Double>> scores;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private FeatureExtract featureExtract;

    public Competition() {
        scores=new HashMap<>();
    }

    public Map<Integer, Map<String, Double>> getAllWorkerScores() {
        List<ChallengeRegistrant>challengeRegistrants=challengeRegistrantDao.getAllRegistrant();
        Map<String,Double>score;
        for(ChallengeRegistrant challengeRegistrant:challengeRegistrants){
            score=scores.getOrDefault(challengeRegistrant.getChallengeID(),null);
            if(score!=null){
                score.put(challengeRegistrant.getHandle(),0.0);
            }else {
                score=new HashMap<>();
                score.put(challengeRegistrant.getHandle(),0.0);
                scores.put(challengeRegistrant.getChallengeID(),score);
            }
        }
        Map<Integer, Map<String, Double>>submissionScores=featureExtract.getScores();
        updateWorkerScores(submissionScores);
        return scores;
    }

    public void updateWorkerScores(Map<Integer, Map<String, Double>>submissionScores){
        if(submissionScores!=null) {
            Map<String,Double>registrant,submission;
            for (Map.Entry<Integer,Map<String, Double>>entry:submissionScores.entrySet()){
                if(scores.containsKey(entry.getKey())){
                    registrant=scores.get(entry.getKey());
                    submission=entry.getValue();
                    for(Map.Entry<String,Double>temp:submission.entrySet()){
                        registrant.put(temp.getKey(),temp.getValue());
                    }
                }
            }
        }
    }
}
