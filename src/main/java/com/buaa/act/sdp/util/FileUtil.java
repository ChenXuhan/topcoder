package com.buaa.act.sdp.util;

import com.buaa.act.sdp.common.Constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by yang on 2016/11/24.
 */
public class FileUtil {
    public static void writeToArff(String filename, String[][] data) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(Constant.DIRECTORY + filename + ".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data[0].length-1; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            writer.write("@attribute score numeric\n");
            writer.write("@data\n");
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length-1; j++) {
                    writer.write(data[i][j] + ',');
                }
                writer.write(data[i][data[i].length-1] + "\n");
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
