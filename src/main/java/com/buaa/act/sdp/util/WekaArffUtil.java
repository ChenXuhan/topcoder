package com.buaa.act.sdp.util;

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

    public static void writeToArffClassfiler(String filename, double[][] data, List<String> classes) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(filename + ".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data[0].length; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            Set<String> type = new LinkedHashSet<>();
            for (int i = 0; i < classes.size(); i++) {
                type.add(classes.get(i));
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : type) {
                stringBuilder.append(str + ",");
            }
            writer.write("@attribute class {" + stringBuilder.substring(0, stringBuilder.length() - 1) + "}\n");
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

    public static void writeToArff(String filename, List<double[]> data, List<String> workers, Map<Integer, Double> index) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(filename + ".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data.get(0).length; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            writer.write("@attribute class {0.0,1.0,2.0}\n");
            writer.write("@data\n");
            int t = 0, k = 0;
            Set<Double> set = new HashSet<>();
            Map<String, Integer> worker = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.get(i).length - 1; j++) {
                    writer.write(data.get(i)[j] + ",");
                }
                if (worker.containsKey(workers.get(i))) {
                    writer.write(worker.get(workers.get(i)) + ",");
                } else {
                    writer.write(k + ",");
                    worker.put(workers.get(i), k);
                    k++;
                }
                writer.write(data.get(i)[data.get(i).length - 1] + "\n");
                if (!set.contains(data.get(i)[data.get(i).length - 1])) {
                    index.put(t++, data.get(i)[data.get(i).length - 1]);
                    set.add(data.get(i)[data.get(i).length - 1]);
                }
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
            fileWriter = new FileWriter(filename + ".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@relation user-features\n");
            for (int i = 0; i < data[0].length; i++) {
                writer.write("@attribute " + "feature" + i + " numeric\n");
            }
            writer.write("@data\n");
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length - 1; j++) {
                    writer.write(data[i][j] + ",");
                }
                writer.write(data[i][data[i].length - 1] + "\n");
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

    public static Instances getInstances(String path, List<double[]> features, List<String> workers, Map<Integer, Double> index) {
        WekaArffUtil.writeToArff(path, features, workers, index);
        Instances instances = WekaArffUtil.getInstances(path);
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    /**
     * weka分类器类别对应的下标
     * @param winner
     * @param len
     * @param <T>
     * @return
     */
    public static <T> Map<Double, T> getWinnerIndex(List<T> winner, int len) {
        Map<Double, T> map = new HashMap<>();
        Set<T> set = new LinkedHashSet<>();
        for (int i = 0; i < len; i++) {
            set.add(winner.get(i));
        }
        int k = 0;
        double index;
        for (T s : set) {
            index = k++;
            map.put(index, s);
        }
        return map;
    }

    public static Instances getInstances(String fileName) {
        File file = new File(fileName + ".arff");
        ArffLoader loader = new ArffLoader();
        Instances instances = null;
        try {
            loader.setFile(file);
            instances = loader.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

}
