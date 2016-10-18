package com.buaa.act.sdp.service;

/**
 * Created by YLT on 2016/10/18.
 */

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.bean.challenge.ChallengePhase;
import com.buaa.act.sdp.bean.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by yang on 2016/9/30.
 */
public class ChallengeApi {
    public  String getChallengeById(String challengeId){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.topcoder.com/v2/challenges/"+challengeId)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        //System.out.println("status: " + response.getStatus());
        // System.out.println("headers: " + response.getHeaders());
        String str = response.readEntity(String.class);
        //System.out.println("body:" + str);
        return( str);
    }

    public  String getChallengeRegistrantsById(String challengeId){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.topcoder.com/v2/challenges/registrants/"+challengeId)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
      /*  System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));*/
        String str = response.readEntity(String.class);
        return str;
    }

    public  String getChallengeSubmissionsById(String challengeId){
        Client client = ClientBuilder.newClient();
        //http://api.topcoder.com/v2/challenges/submissions/  也可以
        Response response = client.target("http://api.topcoder.com/v2/develop/challenges/result/"+challengeId)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
       /* System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));*/
        String str = response.readEntity(String.class);
        return str;
    }

    //Search Past Software Challenges
    public  String  getPastChallenges(int pageIndex,int pageSize){
        Client client = ClientBuilder.newClient();
        // past open upcoming develop/design
        Response response = client.target("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=" + pageIndex+"&pageSize="+pageSize)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        //System.out.println("status: " + response.getStatus());
        // System.out.println("headers: " + response.getHeaders());
        // System.out.println("body:" + response.readEntity(String.class));
        String str = response.readEntity(String.class);
        return(str);

    }

    public  void getOpenChallenges(){
        Client client = ClientBuilder.newClient();
        // past open upcoming develop/design
        Response response = client.target("http://api.topcoder.com/v2/challenges/open?type=develop")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        String str = response.readEntity(String.class);
        System.out.println("body:" + str);
    }
    public  void getActiveChallenges(){
        Client client = ClientBuilder.newClient();
        // past open upcoming develop/design
        Response response = client.target("http://api.topcoder.com/v2/challenges/active?type=develop")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));
    }

    public  String getChallengePhasesById(String challengeId){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.topcoder.com/v2/challenges/phases/"+challengeId)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        /*System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));*/
        String str = response.readEntity(String.class);
        return str;
    }

    //token:eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3RvcGNvZGVyLmF1dGgwLmNvbS8iLCJzdWIiOiJhZHw0MDQ3NzkxNiIsImF1ZCI6IjZad1pFVW8yWks0YzUwYUxQcGd1cGVnNXYyRmZ4cDlQIiwiZXhwIjoxODM2MDYxMjQxLCJpYXQiOjE0NzYwNjEyNDEsImF6cCI6IjZad1pFVW8yWks0YzUwYUxQcGd1cGVnNXYyRmZ4cDlQIn0.teRa7VElbANFDNZVSb-n4sGpu9VzYAcd9qBnAOAKHAU
    public  void test(){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.topcoder.com/v2/design/download/submissionId?submission=515320")
                .request(MediaType.TEXT_PLAIN_TYPE).get();
        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));
    }

    public Connection dataBaseConnect(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String url = "jdbc:mysql://127.0.0.1:3306/topcoder";
        try {
            conn = DriverManager.getConnection(url, "root", "123456");
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return conn;
    }

    public ChallengeItem challengeItemGenerate(JSONObject item1,JSONObject item2){
        String challengeID = item1.getString("challengeId");
        String challengeName=item1.getString("challengeName");
        String challengeType=item1.getString("challengeType");
        String projectId=item2.getString("projectId");
        String forumId=item1.getString("forumId");
        String screeningScorecardId=item2.getString("screeningScorecardId");
        String reviewScorecardId=item2.getString("reviewScorecardId");
        String numberOfCheckpointsPrizes=item1.getString("numberOfCheckpointsPrizes");
        String topCheckPointPrize=item2.getString("topCheckPointPrize");
        String currentStatus=item1.getString("status");
        String postingDate=item2.getString("postingDate");
        String registrationEndDate=item1.getString("registrationEndDate");
        String submissionEndDate=item1.getString("submissionEndDate");
        String finalFixEndDate = "";
        if(item2.containsKey("finalFixEndDate")) {
            finalFixEndDate = item2.getString("finalFixEndDate");
        }
        String appealsEndDate=item2.getString("appealsEndDate");
        String checkpointSubmissionEndDate=item1.getString("checkpointSubmissionEndDate");
        String forumLink=item2.getString("forumLink");
        String registrationStartDate=item1.getString("registrationStartDate");
        String digitalRunPoints=item1.getString("digitalRunPoints");
        String reliabilityBonus=item1.getString("reliabilityBonus");
        String challengeCommunity=item1.getString("challengeCommunity");
        String technology=item1.getString("technologies");
        String prize=item2.getString("prize");
        String platforms=item1.getString("platforms");
        int numRegistrants=item1.getInt("numRegistrants");
        int numSubmissions=item1.getInt("numSubmissions");
        return (new ChallengeItem(challengeID,challengeName, challengeType, projectId, forumId, screeningScorecardId, reviewScorecardId, numberOfCheckpointsPrizes, topCheckPointPrize, currentStatus, postingDate, registrationEndDate, submissionEndDate, finalFixEndDate, appealsEndDate, checkpointSubmissionEndDate, forumLink, registrationStartDate, digitalRunPoints, reliabilityBonus, challengeCommunity, technology, prize, platforms, numRegistrants, numSubmissions));
    }
    public ChallengeRegistrant challengeRegistrantGenerate(String challengeId, JSONObject item){
        String challengeID=challengeId;
        String handle=item.getString("handle");
        String reliability=item.getString("reliability");
        String registrationDate=item.getString("registrationDate");
        String submissionDate="";
        if(item.containsKey("submissionDate")){
            submissionDate =item.getString("submissionDate");
        }
        String rating="";
        if(item.containsKey("rating")){
            rating=item.getString("rating");
        }
        return(new ChallengeRegistrant(challengeID,handle,reliability,registrationDate,submissionDate,rating));
    }
    public ChallengeSubmission challengeSubmissionGenerate(String challengeId, JSONObject item){
        String challengeID=challengeId;
        String handle=item.getString("handle");
        String placement=item.getString("placement");
        String submissionDate=item.getString("submissionDate");
        String submissionStatus=item.getString("submissionStatus");
        String points=item.getString("points");
        String finalScore=item.getString("finalScore");
        String screeningScore=item.getString("screeningScore");
        String initialScore=item.getString("initialScore");
        String submissionDownloadLink=item.getString("submissionDownloadLink");
        return (new ChallengeSubmission(challengeID,handle,placement,submissionDate,submissionStatus,points,finalScore,screeningScore,initialScore,submissionDownloadLink));

    }
    public ChallengePhase challengePhaseGenerate(String challengeId, JSONObject item){
        String challengeID=challengeId;
        String type=item.getString("type");
        String status=item.getString("status");
        String scheduledStartTime=item.getString("scheduledStartTime");
        String actualStartTime=item.getString("actualStartTime");
        String scheduledEndTime=item.getString("scheduledEndTime");
        String actualendTime=item.getString("actualendTime");
        return (new ChallengePhase(challengeID,type,status,scheduledStartTime,actualStartTime,scheduledEndTime,actualendTime));
    }



    //submissionId": 515320
    public static void main(String[] args) {
        ChallengeApi challenge=new ChallengeApi();
        int i=1;
        String str="";
        while((str=challenge.getPastChallenges(i,50))!=null){
            System.out.println(i);
            JSONObject jsonObj = JSONObject.fromObject(str);
            JSONArray jsonAar= (JSONArray) jsonObj.get("data");
            /*get information of every challenge*/
            for(int m=0;m<jsonAar.size();m++){
                JSONObject temp0 = (JSONObject) jsonAar.get(m);
                String challengeId =  temp0.getString("challengeId");
                /*api data mining*/
                String idInfo1 = challenge.getChallengeById(challengeId);
                String idInfo2 = challenge.getChallengeRegistrantsById(challengeId);
                String idInfo3 = challenge.getChallengeSubmissionsById(challengeId);
                String idInfo4 = challenge.getChallengePhasesById(challengeId);

                /*api data-> json*/
                JSONObject temp1 = JSONObject.fromObject(idInfo1);
                JSONArray temp2 = JSONArray.fromObject(idInfo2);
                JSONObject temp3 = JSONObject.fromObject(idInfo3);
                JSONObject temp4 = JSONObject.fromObject(idInfo4);

                /*json->object*/
                ChallengeItem item1 =  challenge.challengeItemGenerate(temp0,temp1);
                for(int k=0;k<temp2.size();k++){
                    ChallengeRegistrant item2 = challenge.challengeRegistrantGenerate(challengeId,temp2.getJSONObject(k));
                }
                JSONArray submissionResults=new JSONArray();
                if(temp3.containsKey("results")){
                    submissionResults= temp3.getJSONArray("results");
                }
                for(int k=0;k<submissionResults.size();k++){
                    ChallengeSubmission item3 = challenge.challengeSubmissionGenerate(challengeId,submissionResults.getJSONObject(k));
                }
                JSONArray phases =new JSONArray();
                if(temp4.containsKey("phases")) {
                    phases = temp4.getJSONArray("phases");
                }
                for(int k=0;k<phases.size();k++){
                    ChallengePhase item4 =challenge.challengePhaseGenerate(challengeId,phases.getJSONObject(k));
                }
            }
               /* bw.close();*/
            i++;
            System.out.print("success");
        }
        Statement statement;
        ResultSet rs = null;
    }
}
