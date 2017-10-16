package com.buaa.act.sdp.service.ability;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.dao.UserDao;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.model.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.model.user.User;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by YLT on 2017/5/31.
 */
@Service
public class AbilityFileMongodb {

    @Autowired
    UserDao userDao;

    @Autowired
    ChallengeItemDao challengeItemDao;

    @Autowired
    ChallengeRegistrantDao challengeRegistrantDao;

    public void linkToMongodb() {
        // 连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient("192.168.7.109", 27017);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("topcoder");
        System.out.println("Connect to database successfully");
        getEachAbility(674);
    }

    public void genMongodbFile() {
        // 连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient("192.168.7.113", 30000);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("topcoder");
        MongoCollection collection = mongoDatabase.getCollection("UserInfo");
        ArrayList<User> allUser = (ArrayList<User>) userDao.getAllUsers();
        for (User user : allUser
                ) {
            if (user.getSkillDegree() == null || user.getId() == 21506) {
                continue;
            }
            collection.insertOne(getEachAbility(user.getId()));
            System.out.println(user.getId());
        }
    }

    public Document getEachAbility(int userId) {

        //userID添加
        User user = userDao.getUserById(userId);
        Document document = new Document().append("userID", userId);

        //profile添加
        Document profileJson = new Document();
        profileJson.append("name", user.getHandle());
        profileJson.append("mail", "");
        profileJson.append("country", user.getCountry());
        profileJson.append("address", "");
        profileJson.append("url", "https://www.topcoder.com/members/" + user.getHandle() + "/");
        String date = user.getMemberSince().substring(0, user.getMemberSince().indexOf('T'));
        profileJson.append("joinDate", date);
        profileJson.append("belongTo", 1);
        profileJson.append("aboutMe", user.getQuote());
        profileJson.append("age","");
        profileJson.append("imageUrl",user.getPhotoLink());
        String[] skills = user.getSkills();
        if(skills != null && skills[0] !=""){
        profileJson.append("tagsAppend",new ArrayList<String>(Arrays.asList(skills)));}
        else{
            profileJson.append("tagsAppend",new ArrayList<String>());
        }
        document.append("profile", profileJson);

        //skill添加
        Document skillsJson = new Document();
        Document tagJson = new Document();
        JSONObject originAll = new JSONObject(user.getSkillDegree());
        JSONObject originTag = (JSONObject) originAll.get("skill");
        ArrayList<String> pl = new ArrayList<String>();
        ArrayList<String> others = new ArrayList<String>();
        //tag添加
        Iterator iterator = originTag.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (Constant.PL.contains(key)) {
                pl.add(key);
            } else {
                others.add(key);
            }
        }
        tagJson.append("pl", pl);
        tagJson.append("others", others);

        //document添加
        StringBuffer requirement = new StringBuffer();
        ChallengeRegistrant[] challengeRegsEveryUser = challengeRegistrantDao.getRegistrantByHandle(user.getHandle());
        for (ChallengeRegistrant eachReg : challengeRegsEveryUser
                ) {
            ChallengeItem item = challengeItemDao.getChallengeItemById(eachReg.getChallengeID());
            String req = item.getRequirements();
            if (req != null) {
                requirement.append(req);
            }
        }
        skillsJson.append("tag", tagJson);
        skillsJson.append("document", requirement.toString());
        document.append("skills", skillsJson);

        //contributions、collaboration添加
        document.append("contributions", new Document((Map<String, Object>) originAll.toMap().get("contribution")));
        JSONArray communicationJsonArray = (JSONArray) originAll.get("communication");
        JSONObject originCompleteJson = (JSONObject) communicationJsonArray.get(0);
        int winNum = 0, loseNum = 0, collaborNum = 0;
        Iterator iter1 = originCompleteJson.keys();
        while (iter1.hasNext()) {
            String[] vs = originCompleteJson.get(iter1.next().toString()).toString().split(":");
            winNum = winNum + Integer.parseInt(vs[0]);
            loseNum = loseNum + Integer.parseInt(vs[1]);
        }
        JSONObject originCollaborJson = (JSONObject) communicationJsonArray.get(1);
        Iterator iter2 = originCollaborJson.keys();
        while (iter2.hasNext()) {
            collaborNum = collaborNum + Integer.parseInt(originCollaborJson.get(iter2.next().toString()).toString());
        }
        Document collaborJson = new Document();
        collaborJson.append("winNum", winNum);
        collaborJson.append("loseNum", loseNum);
        collaborJson.append("collaborNum", collaborNum);
        document.append("collaborations", collaborJson);
        return document;
    }

    /*
    * 处理原始PL文档
    * */
    public void dealPLDoc(String filePath) {
        String encoding = "GBK";
        File file = new File(filePath);
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = null;//考虑到编码格式
            try {
                read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null && lineTxt != "") {
                    lineTxt = lineTxt.substring(lineTxt.indexOf('>') + 1);
                    System.out.println("PL.add(\"" + lineTxt.toLowerCase() + "\");");
                }
                read.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * 调用StandardAnalyzer对项目需求进行简单分词
    */
    public ArrayList<String> textHandle(String requirements) {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = null;
        ArrayList<String> words = new ArrayList<String>();
        String test = challengeItemDao.getChallengeItemRequirementById(30054266);

        try {
            tokenStream = analyzer.tokenStream(null, test);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);
                words.add(ch.toString());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    /*
    *删除项目requirements中的html标签
    * */
    public void HTMLTagDelete() {
        String encoding = "GBK";
        File file = new File("C:\\Users\\YLT\\Desktop\\新建文本文档.txt");
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = null;//考虑到编码格式
            try {
                read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null && lineTxt != "") {
                    lineTxt = lineTxt.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
                            .replaceAll("</[a-zA-Z]+[1-9]?>", "").replaceAll("&nbsp;", "").replaceAll("\\\\t","").replaceAll("\\\\n", "").replaceAll("&lt;", "").replaceAll("&gt;", "").replaceAll("&quot;", "").replaceAll("&qpos;", "").replaceAll("&amp;", "");
                    System.out.println(lineTxt);
                }
                read.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateMongodb(int id){
        // 连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient("192.168.7.113", 30000);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("topcoder");
        MongoCollection collection = mongoDatabase.getCollection("UserInfo");

        Document doc = (Document) collection.find(Filters.eq("userID",4021));
        collection.updateOne(Filters.eq("userID",4021),new Document("$set",new Document("document",doc.get("document").toString().replaceAll("\\\\t","").replaceAll("\\\\n", ""))));
    }
}
