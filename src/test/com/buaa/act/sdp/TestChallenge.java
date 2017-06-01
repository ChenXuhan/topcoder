package com.buaa.act.sdp;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.model.challenge.ChallengeSubmission;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.service.api.*;
import com.buaa.act.sdp.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.service.recommend.TaskRecommend;
import com.buaa.act.sdp.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.service.recommend.network.Collaboration;
import com.buaa.act.sdp.service.recommend.network.Competition;
import com.buaa.act.sdp.service.recommend.network.relationGen;
import com.buaa.act.sdp.service.statistics.ProjectMsg;
import com.buaa.act.sdp.service.update.ChallengeStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by yang on 2016/10/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestChallenge {

    @Autowired
    private ChallengeApi challengeApi;

    @Autowired(required = false)
    private ChallengeSubmissionDao challengeSubmissionDao;

    @Autowired(required = false)
    private ContentBase contentBase;

    @Autowired(required = false)
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeStatistics challengeStatistics;

    @Autowired
    private TaskRecommend recommendResult;

    @Autowired
    private AbilityExp exp;

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private ProjectMsg projectMsg;


    @Autowired(required = false)
    private ChallengeRegistrantDao challengeRegistrantDao;

    @Autowired
    private relationGen gen;

    @Autowired
    private neo4jConn neo4j;

    @Autowired
    private handle hand;

    @Autowired
    private DatabaseOpe databaseOpe;

    @Autowired
    private Competition competition;
    @Test
    public void testProjectId(){
        System.out.println(projectMsg.getProjectToChallenges());
        System.out.println(projectMsg.getProjectToChallenges().size());
    }

    @Test
    public void  testGetWorkerScores(){
        Map<Integer, Map<String, Double>> scores = competition.getAllWorkerScores();
        Iterator<Map.Entry<Integer, Map<String, Double>>> entries = scores.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Map<String, Double>> entry = entries.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println(competition.getAllWorkerScores().size());
    }


    @Test
    public void testRelationGen(){
       // neo4j.getTry();
        //exp.userAbility.userAbilityInsert();
        //gen.collaborationGen();
       // gen.writeToDb();
     //  exp.userAbility.userAbilityInsert();
      // System.out.println(exp.userAbility.getAbility("Breusov"));
       // exp.userAbility.getCollaboration("appiriorob");
//       exp.userAbility.spiltSkill();
       // hand.getSkillsFromDatabase();
    }


    @Test
    public void testCsv(){
        gen.collaborationGen();
    }


    @Test
    public void testChallenge() {
        featureExtract.getFeatures("Assembly Competition");
        List<ChallengeItem> items = featureExtract.getItems();
        List<String>winner=featureExtract.getWinners();
        System.out.println(items.size()+"\t"+winner.size());
        Map<String,List<Integer>>map=new HashMap<>();
        for(int i=0;i<winner.size();i++){
            if(map.containsKey(winner.get(i))){
                map.get(winner.get(i)).add(i);
            }else {
                List<Integer>list=new ArrayList<>();
                list.add(i);
                map.put(winner.get(i),list);
            }
        }
        for(Map.Entry<String,List<Integer>>entry:map.entrySet()){
            List<Integer>list=entry.getValue();
            System.out.println(entry.getKey()+"\t"+list.size());
            for(int j=0;j<list.size();j++){
                ChallengeItem item=items.get(list.get(j));
                System.out.println(item.getChallengeName()+" "+item.getPostingDate()+" "+item.getDuration()+" "+Arrays.toString(item.getPrize())+"\t"+Arrays.toString(item.getTechnology())+" "+Arrays.toString(item.getPlatforms()));
            }
        }
    }

    @Test
    public void testGetMissedChallenge() {
        challengeApi.getMissedChallenges(30012813);
    }

    @Test
    public void testPhrase() {
    }

    @Test
    public void updateChallenges() {
        challengeStatistics.updateChallenges();
    }

    @Test
    public void teat1() {
     databaseOpe.run();
        /*ability.ope.run();
        ability.getAbility("nomo_kazza","CSS");*/
        //exp.getTech(30054047);
       // exp.userAbility.ope.run();
        //exp.getCoder(30050505);
        //exp.userAbility.getUserAllAbility("ksladkov");
        //exp.userAbility.getAbility("ksladkov","CSS");
        /*int sun = 0;
        for(int i = 0; i < Constant.TECH.length -1;i ++){
            for(int j = i+1; j <Constant.TECH.length;j++){
                if(Constant.TECH[i]==Constant.TECH[j]){
                    sun++;
                    System.out.println(Constant.TECH[i]);
                }
            }
        }
        System.out.println(sun);*/
        //System.out.println(Constant.TECH.length);
        //System.out.println(exp.userAbility.getAbility("-jacob-"));
        //neo4j.getTry();
     //exp.userAbility.ttt();
      /* String str =  exp.userAbility.getAbility("12778");
        System.out.println(str);*/
    }

    @Test
    public void challengeSkill() {
        Set<String> set = new HashSet<>();
        List<ChallengeItem> items = challengeItemDao.getAllChallenges();
        Set<String> sets = new HashSet<>();
        for (ChallengeItem item : items) {
            if (item.getTechnology() != null) {
                for (String s : item.getTechnology()) {
                    sets.add(s);
                }
            }
        }
        for (String s : Constant.TECHNOLOGIES) {
            set.add(s);
        }
        for (String s : sets) {
            boolean flag = false;
            for (String ss : set) {
                if (ss.startsWith(s)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testNoSubmission(){
        Map<Integer,Set<String>>submissionMap=new HashMap<>();
        Map<Integer,Set<String>>registerMap=new HashMap<>();
        List<ChallengeSubmission>submissions=challengeSubmissionDao.getChallengeWinner();
        List<ChallengeRegistrant>registrants=challengeRegistrantDao.getAllRegistrant();
        for(ChallengeSubmission challengeSubmission:submissions){
            if(submissionMap.containsKey(challengeSubmission.getChallengeID())){
                submissionMap.get(challengeSubmission.getChallengeID()).add(challengeSubmission.getHandle());
            }else {
                Set<String>set=new HashSet<>();
                set.add(challengeSubmission.getHandle());
                submissionMap.put(challengeSubmission.getChallengeID(),set);
            }
        }
        for(ChallengeRegistrant challengeRegistrant:registrants){
            if(registerMap.containsKey(challengeRegistrant.getChallengeID())){
                registerMap.get(challengeRegistrant.getChallengeID()).add(challengeRegistrant.getHandle());
            }else {
                Set<String>set=new HashSet<>();
                set.add(challengeRegistrant.getHandle());
                registerMap.put(challengeRegistrant.getChallengeID(),set);
            }
        }
        int regCount=0,subCount=0;
        for(Map.Entry<Integer,Set<String>>entry:submissionMap.entrySet()){
            subCount+=entry.getValue().size();
        }
        for(Map.Entry<Integer,Set<String>>entry:registerMap.entrySet()){
            regCount+=entry.getValue().size();
        }
        System.out.println(subCount+"\t"+regCount+"\t"+1.0*subCount/regCount);
    }
}
