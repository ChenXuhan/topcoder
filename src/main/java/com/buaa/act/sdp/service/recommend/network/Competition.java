package com.buaa.act.sdp.service.recommend.network;

import com.buaa.act.sdp.bean.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.service.recommend.FeatureExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public List<String> workerRank(List<Integer>neighbors,List<String>worker){
        List<Map<String,Double>>scores=featureExtract.getUserScore();
        Map<String,Double>score;
        int [][]workerCompare=new int[worker.size()][worker.size()];
        Map<String,Integer>index=new HashMap<>(worker.size());
        for(int i=0;i<worker.size();i++){
            index.put(worker.get(i),i);
        }
        int one,two;
        for(int i=0;i<neighbors.size();i++){
            score=scores.get(neighbors.get(i));
            List<Map.Entry<String,Double>>list=new ArrayList<>(score.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            for(int j=0;j<list.size();j++){
                if((one=index.getOrDefault(list.get(j).getKey(),-1))>=0){
                    for(int k=j+1;k<list.size();k++){
                        if((two=index.getOrDefault(list.get(k).getKey(),-1))>=0){
                            workerCompare[one][two]++;
                        }
                    }
                }
            }
        }
        int[][]rank=rank(workerCompare);
        for(int i=0;i<rank.length;i++){
            System.out.println(worker.get(rank[i][0])+"\t"+rank[i][1]);
        }
        List<String>winner=new ArrayList<>(worker.size());
        for(int i=0;i<rank.length;i++){
            winner.add(worker.get(rank[i][0]));
        }
        return winner;
    }

    public int[][]rank(int [][]num){
        int[][]rank=new int[num.length][2];
        int count;
        for(int i=0;i<num.length;i++){
            count=0;
            for(int j=0;j<num.length;j++){
                if(num[i][j]>num[j][i]){
                    count+=num[i][j];
                }
            }
            rank[i][0]=i;
            rank[i][1]=count;
        }
        Arrays.sort(rank, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[1]-o1[1];
            }
        });
        return rank;
    }
}
