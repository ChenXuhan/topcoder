package com.buaa.act.sdp.util;

import com.buaa.act.sdp.common.Constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yang on 2016/11/24.
 */
public class FileUtil {
    public static void writeToArff(String filename, double[][] data,List<String>classes ) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(Constant.DIRECTORY +filename+".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data[0].length; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            Set<String>type=new HashSet<>();
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
}
