package com.buaa.act.sdp.service;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.user.User;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.dao.UserDao;
import com.buaa.act.sdp.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/11/24.
 */
@Service
public class LinerRegression {
    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;

    @Autowired
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private UserDao userDao;

    public void getUsersSubmissions() {
        List<Map<String, String>> list = challengeSubmissionDao.getUserSubmissons();
        String[] challengeIds;
        String[] scores;
        String handle;
        double score;
        User user;
        for (Map<String, String> map : list) {
            handle = map.get("handle");
            challengeIds = map.get("challengeIds").split(",");
            scores = map.get("scores").split(",");
            Map<String, Double> temp = new HashMap<>();
            for (int i = 0; i < challengeIds.length; i++) {
                score=Double.parseDouble(scores[i]);
                if(!temp.containsKey(challengeIds[i])||(temp.containsKey(challengeIds[i])&&temp.get(challengeIds[i])<score)){
                    temp.put(challengeIds[i],score);
                }
            }
            user=userDao.getUserByName(handle);
            handUserSubmission(user,temp);
        }
    }

    public void handUserSubmission(User user, Map<String,Double>scores){
        ChallengeItem item;
        String [][]features=new String[scores.size()][213];
        int index=0,k,i,j;
        String[]begin=user.getMemberSince().substring(0,7).split("-");
        String []start=null,end=null;
        for(Map.Entry<String,Double>entry:scores.entrySet()){
            k=0;
            item=challengeItemDao.getChallengeItemById(30055120);
           // item=challengeItemDao.getChallengeItemById(Integer.parseInt(entry.getKey()));
            end=item.getRegistrationEndDate().substring(0,7).split("-");
            features[index][k++]=String.valueOf(12*(Integer.parseInt(end[0])-Integer.parseInt(begin[0]))+Integer.parseInt(end[1])-Integer.parseInt(begin[1]));
            start=item.getSubmissionEndDate().substring(0,7).split("-");
            features[index][k++]=String.valueOf(12*(Integer.parseInt(start[0])-Integer.parseInt(end[0]))+Integer.parseInt(start[1])-Integer.parseInt(end[1]));
            features[index][k++]=String.valueOf(item.getNumberOfCheckpointsPrizes());
            features[index][k++]=String.valueOf(item.getReliabilityBonus());
            features[index][k++]=String.valueOf(item.getNumRegistrants());
            //technology  prize platform
            start=item.getTechnology();
            end=Constant.TECHNOLOGIES;
            for(i=0;i<end.length;i++){
               for(j=0; j<start.length;j++) {
                  if(start[j].equals(end[i])){
                       break;
                  }
               }
               if(j<start.length){
                   features[index][k++]="1";
               }else {
                   features[index][k++]="0";
               }
            }
            start=item.getPlatforms();
            end=Constant.PLATFORMS;
            for(i=0;i<end.length;i++){
                for(j=0;j<start.length;j++) {
                    if(start[j].equals(end[i])) {
                        break;
                    }
                }
                if(j<start.length){
                    features[index][k++]="1";
                }else {
                    features[index][k++]="0";
                }
            }
            start=item.getPrize();
            for(String s:start){
                features[index][k++]=s;
            }
            features[index++][k++]=entry.getValue().toString();
        }
        FileUtil.writeToArff(user.getHandle(),features);
    }
}
