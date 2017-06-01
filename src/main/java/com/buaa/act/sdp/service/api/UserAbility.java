package com.buaa.act.sdp.service.api;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.challenge.ChallengeSubmission;
import com.buaa.act.sdp.model.user.UserSkill;
import com.buaa.act.sdp.dao.*;
import com.csvreader.CsvReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by YLT on 2017/2/26.
 */
@Service
public class UserAbility {

    @Autowired
    public ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    private ChallengeItemDao challengeItemDao;
    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private CollaborationRelationDao collaborationRelationDao;
    @Autowired
    private UserSkillDao userSkillDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private neo4jConn neo4j;

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

        if (submissions != null) {
            for (int i = 0; i < submissions.length; i++) {
                int id = submissions[i].getChallengeID();
                if ((itemSub = challengeItemDao.getChallengeItemById(id)) != null) {
                    HashSet allTech = new HashSet();
                    List<String> techStrs = Arrays.asList(itemSub.getTechnology());
                    List<String> platformStrs = Arrays.asList(itemSub.getPlatforms());
                    for (int m = 0; m < techStrs.size(); m++) {
                        allTech.add(techStrs.get(m).toLowerCase());
                    }
                    for (int m = 0; m < platformStrs.size(); m++) {
                        allTech.add(platformStrs.get(m).toLowerCase());
                    }
                    allTech.remove("");
                    Iterator iter = allTech.iterator();
                    while (iter.hasNext()) {
                        String techEach = (String) iter.next();
                        if (userSkill.containsKey(techEach)) {
                            double score = userSkill.get(techEach);
                            score += Double.parseDouble(submissions[i].getFinalScore()) * challengeItemDao.getDifficultyDegree(id);
                            userSkill.put(techEach, score);
                            TIMES.put(techEach, TIMES.get(techEach) + 1);
                        } else {
                            userSkill.put(techEach, Double.parseDouble(submissions[i].getFinalScore()) * challengeItemDao.getDifficultyDegree(id));
                            TIMES.put(techEach, 1);
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
            double finalScore = userSkill.get(name) / t;
            if(finalScore !=0.0){
            DecimalFormat df = new DecimalFormat("#.####");
            finalScore = Double.parseDouble(df.format(finalScore));
            userSkill.put(name, finalScore);
            jsonObject.put(name, finalScore);}
        }
        return jsonObject.toString();
    }

    public void userAbilityInsert() {
        List<String> userList = userDao.getUsers();
        //Connection con = neo4j.getTry();
        for (String user : userList) {
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

    public JSONObject getContribution(String handle) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RegNum", challengeRegistrantDao.getUserRegers(handle));
        jsonObject.put("SubNum", challengeSubmissionDao.getUserSubers(handle));
        jsonObject.put("WinNum", challengeSubmissionDao.getUserWiners(handle));
        // System.out.println(jsonObject.toString());
        return jsonObject;
    }

    /*
    *使用neo4j找出一个用户有哪些协作关系
    * */
    public JSONObject getCollaboration(String handle) {
        Connection con = neo4j.getTry();
        String query = "MATCH (u:User)<-[:CollaborateWith]-(f:User) WHERE u.handle = {1} RETURN f.handle";
        JSONObject jsonObject = new JSONObject();
        HashMap<String, Integer> colHashMap = new HashMap<String, Integer>();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(query);
            stmt.setString(1, handle);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String handle2 = rs.getString("f.handle");
                if (colHashMap.containsKey(handle2)) {
                    colHashMap.put(handle2, colHashMap.get(handle2) + 1);
                } else {
                    colHashMap.put(handle2, 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Iterator iter = colHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            jsonObject.put((String) entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }

    public JSONObject getCompetition(String handle) {
        Connection con = neo4j.getTry();
        String query = "MATCH (u:User)<-[c:CompleteWith]-(f:User) WHERE u.handle = {1} RETURN f.handle,c.scoreA,c.scoreB";
        JSONObject jsonObject = new JSONObject();
        HashMap<String, Integer> winHashMap = new HashMap<String, Integer>();
        HashMap<String, Integer> lostHashMap = new HashMap<String, Integer>();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(query);
            stmt.setString(1, handle);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Double score1 = Double.valueOf(rs.getString("c.scoreA"));
                Double score2 = Double.valueOf(rs.getString("c.scoreB"));
                String handle2 = rs.getString("f.handle");
                if (score1 > score2) {
                    if (winHashMap.containsKey(handle2)) {
                        winHashMap.put(handle2, winHashMap.get(handle2) + 1);
                    } else {
                        winHashMap.put(handle2, 1);
                    }
                } else if (score1 < score2) {
                    if (lostHashMap.containsKey(handle2)) {
                        lostHashMap.put(handle2, lostHashMap.get(handle2) + 1);
                    } else {
                        lostHashMap.put(handle2, 1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Iterator iter = winHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String handle2 = (String) entry.getKey();
            String vs = entry.getValue().toString();
            if (lostHashMap.containsKey(handle2)) {
                jsonObject.put(handle2, vs + ":" + lostHashMap.get(handle2));
                lostHashMap.remove(handle2);
            } else {
                jsonObject.put(handle2, vs + ":0");
            }
        }

        iter = lostHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            jsonObject.put((String) entry.getKey(), "0:" + entry.getValue());
        }
        return jsonObject;
    }

    public void ttt() {
        String query = "MATCH (u:User) RETURN u.handle";
        Connection con = neo4j.getTry();
        ResultSet rs;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject json = new JSONObject();
                String handle = rs.getString("u.handle");
                json.put("skill", new JSONObject(getAbility(handle)));
                json.put("contribution", getContribution(handle));
                JSONArray comm = new JSONArray();
                comm.put(getCompetition(handle));
                comm.put(getCollaboration(handle));
                json.put("communication", comm);
                userDao.insertSkillDegree(handle, json.toString());
                System.out.println(handle + json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void spiltSkill() {
        String path = "C:\\Users\\YLT\\Desktop\\skillDegree.csv";
        CsvReader r = null;
        List<UserSkill> skillList = new ArrayList<UserSkill>();
        try {
            r = new CsvReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] nextLine = null;
        try {
            while (r.readRecord()) {
                nextLine = r.getValues();
                JSONObject jsonObject = new JSONObject(nextLine[1]);
                System.out.println(nextLine[0] + "++++++++++++++++++++++++++");
                JSONObject skillJson = jsonObject.getJSONObject("skill");
                Iterator iter = skillJson.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = skillJson.get(key).toString();
                    if (value.equals("0")) {
                        continue;
                    }
                    userSkillDao.insertEach(new UserSkill(nextLine[0], key, value.toString()));
                    //skillList.add(new UserSkill(nextLine[0], key, value.toString()));
                    // System.out.println(nextLine[0] + "   " + key + "   " + value.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ok");
        // userSkillDao.insert(skillList);
        //从neo4j中取出数据，得到分隔的技能值。但是neo4j的连接老是出问题
        /*String query = "MATCH (u:User) RETURN u.handle";
        Connection con = neo4j.getTry();
        List<UserSkill> skillList = new ArrayList<UserSkill>();

        try {
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String handle = rs.getString("u.handle");
                System.out.println(handle + "++++++++++++++++++++++++++");
                String queryEach = "MATCH (u:User) where u.handle = {1} RETURN u.skillDegree";
                PreparedStatement stmtEach = con.prepareStatement(queryEach);
                stmtEach.setString(1, handle);
                ResultSet rsEach = stmtEach.executeQuery();

                rsEach.next();
                String str = rsEach.getString("u.skillDegree");
                JSONObject json = new JSONObject(str);
                JSONObject skillJson = (JSONObject) json.get("skill");
                Iterator iter = skillJson.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = skillJson.get(key).toString();
                    if (value.equals("0")) {
                        continue;
                    }
                    skillList.add(new UserSkill(handle, key, value.toString()));
                    System.out.println(handle + "   " + key + "   " + value.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("ok");
        userSkillDao.insert(skillList);*/
    }
}
