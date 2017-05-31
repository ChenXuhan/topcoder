package com.buaa.act.sdp.service.recommend.network;

import com.buaa.act.sdp.bean.challenge.CollaborationRelation;
import com.buaa.act.sdp.dao.CollaborationRelationDao;
import com.csvreader.CsvWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YLT on 2017/3/18.
 */
@Component
public class relationGen {

    @Autowired
    private Competition competition;

    @Autowired
    private Collaboration collaboration;

    @Autowired
    private CollaborationRelationDao collaborationRelationDao;

    public void completeGen() {
        Map<Integer, Map<String, Double>> scores = competition.getAllWorkerScores();
        List<String[]> allComplete = new ArrayList<String[]>();
        for (Map.Entry<Integer, Map<String, Double>> entry : scores.entrySet()) {
            int key = entry.getKey();
            Map<String, Double> valueMap = entry.getValue();
            int i = 0;
            String[] handles = new String[valueMap.size()];
            for (String handle : valueMap.keySet()) {
                handles[i++] = handle;
            }
            for (int j = 0; j < handles.length; j++) {
                if (valueMap.get(handles[j]) != 0.0) {
                    for (int k = 0; k < handles.length; k++) {
                        if (j != k && valueMap.get(handles[k]) != 0.0) {
                            String[] contents = {handles[j], handles[k], String.valueOf(valueMap.get(handles[j])), String.valueOf(valueMap.get(handles[k])), String.valueOf(key)};
                            allComplete.add(contents);
                        }
                    }
                }
            }
        }
        writeCsv(allComplete, "C:\\Users\\YLT\\Desktop\\col.csv");
    }

    public void writeCsv(List<String[]> allRelation, String path) {
        //CsvWriter wr = new CsvWriter("C:\\Users\\YLT\\Desktop\\col.csv", ',', Charset.forName("GBK"));
        CsvWriter wr = new CsvWriter(path, ',', Charset.forName("GBK"));

        int num = 0;
        for (int i = 0; i < allRelation.size(); i++) {
            try {
                wr.writeRecord(allRelation.get(i));
              /*  num++;
                if (num > 100000) {
                    wr.flush();
                    num = 0;
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        wr.flush();
        wr.close();
    }

    public void collaborationGen() {

        Map<Integer, List<Integer>> allProject = collaboration.getProjectToChallenges();
        Map<Integer, Map<String, Double>> scores = competition.getAllWorkerScores();
        List<String[]> allCollaboration = new ArrayList<String[]>();
        int fileNumber = 0;

        for (Map.Entry<Integer, List<Integer>> entry : allProject.entrySet()) {

            int project = entry.getKey();
            List<Integer> challengeIdToProject = entry.getValue();
            System.out.println(project);
            for (int i = 0; i < challengeIdToProject.size(); ) {
                if (!scores.containsKey(challengeIdToProject.get(i))) {
                    challengeIdToProject.remove(i);
                } else {
                    i++;
                }
            }

            //遍历每个project中的challenge
            for (int i = 0; i < challengeIdToProject.size() - 1; i++) {
                for (int j = i + 1; j < challengeIdToProject.size(); j++) {
                    Map<String, Double> challengeMap1 = scores.get(challengeIdToProject.get(i));
                    Map<String, Double> challengeMap2 = scores.get(challengeIdToProject.get(j));
                    int p = 0, q = 0;
                    String[] handles1 = new String[challengeMap1.size()];
                    String[] handles2 = new String[challengeMap2.size()];
                    for (String handle : challengeMap1.keySet()) {
                        handles1[p++] = handle;
                    }
                    for (String handle : challengeMap2.keySet()) {
                        handles2[q++] = handle;
                    }
                    for (int m = 0; m < handles1.length; m++) {
                        if (challengeMap1.get(handles1[m]) != 0.0) {
                            for (int n = 0; n < handles2.length; n++) {
                                if ((challengeMap2.get(handles2[n]) != 0.0) && (!handles1[m].equals(handles2[n]))) {
                                    String[] contents1 = {handles1[m], handles2[n], String.valueOf(challengeMap1.get(handles1[m])), String.valueOf(challengeMap2.get(handles2[n])), String.valueOf(project), String.valueOf(challengeIdToProject.get(i)), String.valueOf(challengeIdToProject.get(j))};
                                    String[] contents2 = {handles2[n], handles1[m], String.valueOf(challengeMap2.get(handles2[n])), String.valueOf(challengeMap1.get(handles1[m])), String.valueOf(project), String.valueOf(challengeIdToProject.get(j)), String.valueOf(challengeIdToProject.get(i))};
                                    allCollaboration.add(contents1);
                                    allCollaboration.add(contents2);
                                    if (allCollaboration.size() > 4000000) {
                                        String pathFile = "C:\\Users\\YLT\\Desktop\\relation\\col" + fileNumber + ".csv";
                                        fileNumber++;
                                        writeCsv(allCollaboration, pathFile);
                                        allCollaboration.clear();
                                    }
                                    /*CollaborationRelation collaborationRelation = new CollaborationRelation(handles1[m], handles2[n], String.valueOf(challengeMap1.get(handles1[m])), String.valueOf(challengeMap2.get(handles2[n])),project,challengeIdToProject.get(i),challengeIdToProject.get(j));
                                    collaborationRelationDao.insert(collaborationRelation);
                                    collaborationRelation = new CollaborationRelation(handles2[n], handles1[m], String.valueOf(challengeMap2.get(handles2[n])), String.valueOf(challengeMap1.get(handles1[m])),project,challengeIdToProject.get(j),challengeIdToProject.get(i));
                                    collaborationRelationDao.insert(collaborationRelation);*/
                                }
                            }
                        }
                    }
                }
            }
        }
        writeCsv(allCollaboration, "C:\\Users\\YLT\\Desktop\\relation\\col.csv");
    }

    /*public void writeToDb(List<String[]> allRelation){
        for(int i = 0;i < allRelation.size();i ++) {
            String[] strs = allRelation.get(i);
            CollaborationRelation collaborationRelation = new CollaborationRelation(strs[0],strs[1],strs[2],strs[3],Integer.parseInt(strs[4]),Integer.parseInt(strs[5]),Integer.parseInt(strs[6]));
            collaborationRelationDao.insert(collaborationRelation);
        }
        //collaborationRelationDao.deleteAll();
    }*/
}

