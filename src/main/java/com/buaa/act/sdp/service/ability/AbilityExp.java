package com.buaa.act.sdp.service.ability;

import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.challenge.ChallengeRegistrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验。给定项目是否能给出合适的开发者
 * Created by YLT on 2017/3/1.
 */
@Service
public class AbilityExp {

    @Autowired
    public UserAbility userAbility;
    @Autowired
    ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    ChallengeItemDao challengeItemDao;
    ChallengeRegistrant[] challengeRegistrants;

    public List<String> getTech(int itemId) {
        ChallengeItem item = challengeItemDao.getChallengeItemById(itemId);
        String tech[] = item.getTechnology();
        String platform[] = item.getPlatforms();
        List<String> technology = new ArrayList<String>();
        for (int i = 0; i < tech.length; i++) {
            technology.add(tech[i]);
        }
        for (int j = 0; j < platform.length; j++) {
            if (!technology.contains(platform[j])) {
                technology.add(platform[j]);
            }
        }
        return technology;
    }

    /*
    * 给定一个challenge，选择合适的开发者
    * */
    /*public void getCoder(int itemId){
        challengeRegistrants = challengeRegistrantDao.getRegistrantById(itemId);
        List<String> techsNeed = getTech(itemId);
        if(challengeRegistrants != null){
            double[] scores = new double[challengeRegistrants.length];
            for(int i = 0; i < challengeRegistrants.length;i ++){
                for(int j = 0;j < techsNeed.size();j ++){
                    scores[i] += userAbility.getAbility(challengeRegistrants[i].getHandle(),techsNeed.get(j));
                }
                DecimalFormat df = new DecimalFormat("#.####");
                scores[i] = Double.parseDouble(df.format(scores[i]));
            }
            bubbleSort(scores);
            for( int i = challengeRegistrants.length - 1; i >= 0 ; i --){
                System.out.println(challengeRegistrants[i].getHandle()+"...get"+scores[i]);
            }
        }
    }*/
}
