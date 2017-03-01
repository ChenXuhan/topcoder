package com.buaa.act.sdp.service.api;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by YLT on 2017/2/26.
 */
@Service
public class UserAbility {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;

    @Autowired
    public DatabaseOpe ope;

    public double getAbility(String handle, String techName) {
        ChallengeSubmission[] submissions = challengeSubmissionDao.getSubmissionByHandle(handle);
        ChallengeItem itemSub;
        double abilityScore = 0;
        if (submissions != null) {
            for (int i = 0; i < submissions.length; i++) {
                if ((itemSub = challengeItemDao.getChallengeItemById(submissions[i].getChallengeID())) != null) {
                    List<String> techStrs = Arrays.asList(itemSub.getTechnology());
                    List<String> platformStrs = Arrays.asList(itemSub.getPlatforms());
                    if (techStrs.contains(techName) || platformStrs.contains(techName)) {
                        abilityScore = abilityScore + Double.parseDouble(submissions[i].getFinalScore()) * ope.scores.get(itemSub.getChallengeId());
                    }
                }
            }
        }
        DecimalFormat df = new DecimalFormat("#.####");
        abilityScore = Double.parseDouble(df.format(abilityScore));
        //System.out.println(abilityScore);
        return abilityScore;
    }
}
