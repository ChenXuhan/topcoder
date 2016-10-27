package com.buaa.act.sdp.service;

/**
 * Created by YLT on 2016/10/18.
 */

import com.buaa.act.sdp.bean.challenge.*;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengePhaseDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.util.JsonUtil;
import com.buaa.act.sdp.util.RequestUtil;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yang on 2016/9/30.
 */
@Service
public class ChallengeApi {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private ChallengePhaseDao challengePhaseDao;
    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;

    public  ChallengeItem  getChallengeById(int challengeId){
        String str= RequestUtil.request("http://api.topcoder.com/v2/challenges/"+challengeId);
        //System.out.print(str);
        if(str!=null){
             return JsonUtil.fromJson(str,ChallengeItem.class);
        }
        return null;
    }

    public  ChallengeRegistrant[]  getChallengeRegistrantsById(int challengeId){
        String str=RequestUtil.request("http://api.topcoder.com/v2/challenges/registrants/"+challengeId);
        if(str!=null){
            return JsonUtil.fromJson(str,ChallengeRegistrant[].class);
        }
        return null;
    }

    public  ChallengePhase[] getChallengePhasesById(int challengeId){
        String str=RequestUtil.request("http://api.topcoder.com/v2/challenges/phases/"+challengeId);
        if(str!=null){
            JsonElement jsonElement=JsonUtil.getJsonElement(str,"phases");
            if(jsonElement!=null) {
            return JsonUtil.fromJson(jsonElement,ChallengePhase[].class);
            }
        }
        return null;
    }

    //http://api.topcoder.com/v2/challenges/submissions/
    public  ChallengeSubmission[] getChallengeSubmissionsById(int challengeId){
        //System.out.println(challengeId);
        String str=RequestUtil.request("http://api.topcoder.com/v2/develop/challenges/result/"+challengeId);
        if(str!=null){
            JsonElement jsonElement=JsonUtil.getJsonElement(str,"results");
            if(jsonElement!=null) {
                return JsonUtil.fromJson(jsonElement, ChallengeSubmission[].class);
            }
        }
        return null;
    }

    public int getCompleteChallengeCount(){
        String str=RequestUtil.request("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=1&pageSize=50");
        JsonElement jsonElement=JsonUtil.getJsonElement(str,"total");
        if(jsonElement.isJsonPrimitive()){
            System.out.print(jsonElement.getAsInt());
            return jsonElement.getAsInt();
        }
        return 0;
    }
    //Search Past Software Challenges
    public PastChallenge[] getPastChallenges(int pageIndex, int pageSize){
        String str=RequestUtil.request("http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=" + pageIndex+"&pageSize="+pageSize);
        if(str!=null){
            JsonElement jsonElement=JsonUtil.getJsonElement(str,"data");
            if(jsonElement!=null){
                PastChallenge[]pastChallenges=JsonUtil.fromJson(jsonElement,PastChallenge[].class);
                return pastChallenges;
            }
        }
        return null;
    }
    public void storeTest(int challengeId){
        ChallengeItem challengeItem;
        ChallengeRegistrant []challengeRegistrant;
        ChallengeSubmission[]challengeSubmissions;
        ChallengePhase[]challengePhases;
        challengeItem=getChallengeById(challengeId);
        if(challengeItem!=null) {
            System.out.println(challengeItem.toString());
            //challengeItemGenerate(challengeItem, pastChallenges[j]);
            challengeItemDao.insert(challengeItem);

            challengeRegistrant = getChallengeRegistrantsById(challengeId);
            if(challengeRegistrant!=null&&challengeRegistrant.length!=0) {
                challengeRegistrantGenerate(challengeId, challengeRegistrant);
                challengeRegistrantDao.insert(challengeRegistrant);
            }
            challengeSubmissions = getChallengeSubmissionsById(challengeId);
            if(challengeSubmissions!=null&&challengeSubmissions.length!=0) {
                challengeSubmissionGenerate(challengeId, challengeSubmissions);
                challengeSubmissionDao.insert(challengeSubmissions);
            }
            challengePhases = getChallengePhasesById(challengeId);
            if(challengePhases!=null&&challengePhases.length!=0) {
                challengePhaseGenerate(challengeId, challengePhases);
                challengePhaseDao.insert(challengePhases);
            }
        }

    }
    public boolean challengeExistOrNot(int challengeId){
        ChallengeItem []items=challengeItemDao.getChallengeItem(challengeId);
        if(items.length>0) {
            return true;
        }
        else
            return false;
    }
    public void  savePastChallenge(){
        int count =getCompleteChallengeCount();
        int pages=count/50;
        if(count%50!=0){
            pages++;
        }
        System.out.println(pages);
        PastChallenge[]pastChallenges;
        ChallengeItem challengeItem;
        ChallengeRegistrant []challengeRegistrant;
        ChallengeSubmission[]challengeSubmissions;
        ChallengePhase[]challengePhases;
        int challengeId;
        for(int i=1;i<=pages;i++){
            System.out.println(i+"oooooooooooooooooo");
            pastChallenges=getPastChallenges(i,50);
            if(pastChallenges==null){
                continue;
            }
            for(int j=0;j<pastChallenges.length;j++){
                challengeId=pastChallenges[j].getChallengeId();
                System.out.println(challengeId);
                challengeItem=getChallengeById(challengeId);
                if(challengeItem!=null&&(!challengeExistOrNot(challengeId))) {
                    challengeItemGenerate(challengeItem, pastChallenges[j]);
                    challengeItemDao.insert(challengeItem);
                    challengeRegistrant = getChallengeRegistrantsById(challengeId);
                    if(challengeRegistrant!=null&&challengeRegistrant.length!=0) {
                        challengeRegistrantGenerate(challengeId, challengeRegistrant);
                        challengeRegistrantDao.insert(challengeRegistrant);
                    }
                    challengeSubmissions = getChallengeSubmissionsById(challengeId);
                    if(challengeSubmissions!=null&&challengeSubmissions.length!=0) {
                        challengeSubmissionGenerate(challengeId, challengeSubmissions);
                        challengeSubmissionDao.insert(challengeSubmissions);
                    }
                    challengePhases = getChallengePhasesById(challengeId);
                    if(challengePhases!=null&&challengePhases.length!=0) {
                        challengePhaseGenerate(challengeId, challengePhases);
                        challengePhaseDao.insert(challengePhases);
                    }
                }
            }
        }
    }
    // open upcoming develop/design
    public  void getOpenChallenges(){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.topcoder.com/v2/challenges/open?type=develop")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        String str = response.readEntity(String.class);
        System.out.println("body:" + str);
    }

    // active develop/design
    public  void getActiveChallenges(){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.topcoder.com/v2/challenges/active?type=develop")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));
    }


    public void challengeItemGenerate(ChallengeItem item,PastChallenge pastChallenge){
        item.setNumRegistrants(pastChallenge.getNumRegistrants());
        item.setNumSubmissions(pastChallenge.getNumSubmissions());
        item.setRegistrationStartDate(pastChallenge.getRegistrationStartDate());
    }

    public void challengeRegistrantGenerate(int challengeId, ChallengeRegistrant[]challengeRegistrants){
            for(int i=0;i<challengeRegistrants.length;i++){
                challengeRegistrants[i].setChallengeID(challengeId);
            }
    }
    public void challengeSubmissionGenerate(int challengeId,  ChallengeSubmission[]challengeSubmissions){
           for(int i=0;i<challengeSubmissions.length;i++){
               challengeSubmissions[i].setChallengeID(challengeId);
           }
    }

    public void challengePhaseGenerate(int challengeId, ChallengePhase[]challengePhases){
            for (int i = 0; i < challengePhases.length; i++) {
                challengePhases[i].setChallengeID(challengeId);
            }
    }
}
