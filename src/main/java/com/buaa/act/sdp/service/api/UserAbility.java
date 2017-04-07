package com.buaa.act.sdp.service.api;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import com.buaa.act.sdp.bean.user.DevelopmentHistory;
import com.buaa.act.sdp.bean.user.User;
import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.dao.UserDao;

import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.jar.JarEntry;

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
    private UserDao userDao;

    @Autowired
    private neo4jConn neo4j;

    @Autowired
    public DatabaseOpe ope;

    /*public double getAbility(String handle, String techName) {
        ChallengeSubmission[] submissions = challengeSubmissionDao.getSubmissionByHandle(handle);
        ChallengeItem itemSub;
        double abilityScore = 0;
        int num = 0;
        if (submissions != null) {
            for (int i = 0; i < submissions.length; i++) {
                int id = submissions[i].getChallengeID();
                if ((itemSub = challengeItemDao.getChallengeItemById(id)) != null) {
                    List<String> techStrs = Arrays.asList(itemSub.getTechnology());
                    List<String> platformStrs = Arrays.asList(itemSub.getPlatforms());
                    if (techStrs.contains(techName) || platformStrs.contains(techName)) {
                        //abilityScore = abilityScore + Double.parseDouble(submissions[i].getFinalScore()) * ope.scores.get(itemSub.getChallengeId());
                        abilityScore = abilityScore + Double.parseDouble(submissions[i].getFinalScore()) * challengeItemDao.getDifficultyDegree(id);
                        num ++;
                    }
                }
            }
            if(num !=0) {
                abilityScore = abilityScore / num;
            }
        }
        DecimalFormat df = new DecimalFormat("#.####");
        abilityScore = Double.parseDouble(df.format(abilityScore));
        return abilityScore;
    }

    public String[] getAllTechs(){
        String[] techs = new String[200];
        techs= Constant.TECHNOLOGIES;
        int len = Constant.TECHNOLOGIES.length + Constant.PLATFORMS.length;
        for(int i = Constant.TECHNOLOGIES.length;i < len; i ++ ){
          //  techs
        }
        return techs;
    }

    public String getUserAllAbility(String handle){
        JsonObject member1 = new JsonObject();
        for(int i = 0; i < Constant.TECHNOLOGIES.length;i ++){
            String tech = Constant.TECHNOLOGIES[i];
            member1.addProperty(tech,getAbility(handle,tech));
        }
        for(int i = 0; i < Constant.PLATFORMS.length;i++){
            String tech = Constant.PLATFORMS[i];
            if(!member1.has(tech)){
                member1.addProperty(tech,getAbility(handle,tech));
            }
        }
        return member1.toString();
    }

    public void userAbilityInsert() {
        List<String> userList = userDao.getUsers();
        //List<String> ability = new ArrayList<String>();
        for (String user : userList) {
            userDao.insertSkillDegree(user,getUserAllAbility(user));
            System.out.println(user);
        }
    }*/
    public String getAbility(String handle) {
        ChallengeSubmission[] submissions = challengeSubmissionDao.getSubmissionByHandle(handle);
        ChallengeItem itemSub;
        HashMap<String, Double> userSkill = new HashMap<String, Double>();
        HashMap<String, Integer> TIMES = new HashMap<String, Integer>();

        for (int m = 0; m < Constant.TECH.length; m++) {
            userSkill.put(Constant.TECH[m], 0.0);
            TIMES.put(Constant.TECH[m], 0);
        }

        if (submissions != null) {
            for (int i = 0; i < submissions.length; i++) {
                int id = submissions[i].getChallengeID();
                if ((itemSub = challengeItemDao.getChallengeItemById(id)) != null) {
                    List<String> techStrs = new ArrayList<>();
                    Collections.addAll(techStrs, itemSub.getTechnology());
                    List<String> platformStrs = Arrays.asList(itemSub.getPlatforms());
                    for (int m = 0; m < platformStrs.size(); m++) {
                        if (!techStrs.contains(platformStrs.get(m))) {
                            techStrs.add(platformStrs.get(m));
                        }
                    }
                    for (int m = 0; m < Constant.TECH.length; m++) {
                        if (techStrs.contains(Constant.TECH[m])) {
                            double score = userSkill.get(Constant.TECH[m]);
                            score += Double.parseDouble(submissions[i].getFinalScore()) * challengeItemDao.getDifficultyDegree(id);
                            userSkill.put(Constant.TECH[m], score);
                            TIMES.put(Constant.TECH[m], TIMES.get(Constant.TECH[m]) + 1);
                        }
                    }
                }
            }
        }

        Iterator iter = userSkill.entrySet().iterator();
        JSONObject jsonObject = new JSONObject();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            int t = TIMES.get(name);
            if (t != 0) {
                double finalScore = userSkill.get(name) / t;
                DecimalFormat df = new DecimalFormat("#.####");
                finalScore = Double.parseDouble(df.format(finalScore));
                userSkill.put(name, finalScore);
                jsonObject.put(name,finalScore);
            }else
            {
                jsonObject.put(name,0.0);
            }
        }
        return jsonObject.toString();
    }

    public void userAbilityInsert() {
        List<String> userList = userDao.getUsers();
        //Connection con = neo4j.getTry();
        for (String user : userList) {
            if(user.equals("novserj")){
                break;
            }
            String skill = getAbility(user);
            userDao.insertSkillDegree(user, skill);
            System.out.println(user);
            /*String cypher = "MATCH (n:User{handle:{1}}) SET n.skillDegree = {2}";
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(cypher);
                stmt.setString(1, user);
                stmt.setString(2, skill);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        }
    }
    public void ttt(){
        HashMap<String ,String> map = new HashMap<>();
        List<String> userList = userDao.getUsers();
        //Connection con = neo4j.getTry();
        for (String user : userList) {
            String skill = getAbility(user);
            map.put(user,skill);
        }
        userDao.insertSkillDegreeBatch(map);
    }
}
