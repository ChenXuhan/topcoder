package com.buaa.act.sdp.util;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import com.buaa.act.sdp.common.Constant;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by yang on 2016/11/24.
 */
public class WekaArffUtil {

    public static void writeToArffClassfiler(String filename, double[][] data,List<String>classes ) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(filename+".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data[0].length; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            Set<String>type=new LinkedHashSet<>();
            for(int i=0;i<classes.size();i++){
                type.add(classes.get(i));
            }
            StringBuilder stringBuilder=new StringBuilder();
            for(String str:type){
                stringBuilder.append(str+",");
            }
            writer.write("@attribute class {" +stringBuilder.substring(0,stringBuilder.length()-1)+"}\n");
            writer.write("@data\n");
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    writer.write(data[i][j] + ",");
                }
                writer.write(classes.get(i) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeToArffCluster(String filename, double[][] data) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(filename+".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data[0].length; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            writer.write("@data\n");
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length-1; j++) {
                    writer.write(data[i][j] + ",");
                }
                writer.write(data[i][data[i].length-1]+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Instances getInstances(String path, double[][] features, List<String> winners) {
        WekaArffUtil.writeToArffClassfiler(path, features, winners);
        Instances instances = WekaArffUtil.getInstances(path);
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    // weka分类器类别对应的下标
    public static Map<Double, String> getWinnerIndex(List<String> winner, int len) {
        Map<Double, String> map = new HashMap<>();
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < len; i++) {
            set.add(winner.get(i));
        }
        int k = 0;
        double index;
        for (String s : set) {
            index = k++;
            map.put(index, s);
        }
        return map;
    }

    public static Instances getInstances(String fileName){
        File file = new File(fileName+".arff");
        ArffLoader loader = new ArffLoader();
        Instances instances=null;
        try {
            loader.setFile(file);
            instances = loader.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

    public static void writeTaskAndWinner(List<ChallengeItem>items, List<String>winner, String challengeType){
        BufferedWriter writer=null;
        try {
            FileWriter fileWriter=new FileWriter(Constant.CLASSIFIER_DIRECTORY+challengeType+".txt");
             writer=new BufferedWriter(fileWriter);
            for(int i=0;i<items.size();i++){
                writer.write(items.get(i).getChallengeId()+"\t"+winner.get(i)+'\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
