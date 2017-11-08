package com.buaa.act.sdp.util;

import java.util.*;

/**
 * Created by yang on 2017/3/15.
 */
public class Maths {

    /**
     * 余弦相似度计算
     * @param vectorOne
     * @param vectorTwo
     * @return
     */
    public static double taskSimilariry(double[] vectorOne, double[] vectorTwo) {
        double num = 0, a = 0, b = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            num += vectorOne[i] * vectorTwo[i];
            a += vectorOne[i] * vectorOne[i];
            b += vectorTwo[i] * vectorTwo[i];
        }
        return num / Math.sqrt(a * b);
    }

    public static double taskSimilariry1(double[] vectorOne, double[] vectorTwo) {
        double similarity = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            similarity += (vectorOne[i] - vectorTwo[i]) * (vectorOne[i] - vectorTwo[i]);
        }
        return similarity;
    }

    public static List<Map.Entry<Integer, Double>> findNeighbor(int index, double[][] features) {
        Map<Integer, Double> similarity = new HashMap<>();
        for (int i = 0; i < index; i++) {
            similarity.put(i, taskSimilariry(features[index], features[i]));
        }
        List<Map.Entry<Integer, Double>> lists = new ArrayList<>();
        lists.addAll(similarity.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return lists;
    }

    public static List<Integer> getNeighbors(double[][] features, int index, int k) {
        Map<Integer, Double> similarity = new HashMap<>();
        for (int i = 0; i < index; i++) {
            similarity.put(i, taskSimilariry(features[index], features[i]));
        }
        List<Map.Entry<Integer, Double>> lists = new ArrayList<>();
        lists.addAll(similarity.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<Integer> list = new ArrayList<>(k + 1);
        for (int i = 0; i < k; i++) {
            list.add(lists.get(i).getKey());
        }
        list.add(index);
        return list;
    }

    /**
     * 获取某一任务较为相似的任务
     * @param features
     * @param index
     * @return
     */
    public static List<Integer> getSimilarityChallenges(double[][] features, int index) {
        Map<Integer, Double> map = new HashMap<>();
        double k, sum1, sum2;
        for (int i = 0; i < index; i++) {
            k = 0;
            sum1 = 0;
            sum2 = 0;
            if (Math.abs(features[i][2] - features[index][2]) > 366) {
                continue;
            }
            for (int j = 5; j < features[0].length; j++) {
                if (features[i][j] == 1.0) {
                    sum1++;
                }
                if (features[index][j] == 1.0) {
                    sum2++;
                }
                if (features[i][j] == features[index][j] && features[i][j] == 1.0) {
                    k++;
                }
            }
            if (k >= 1.0) {
                map.put(i, k / Math.sqrt(sum1 * sum2));
            }
        }
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<Integer> result = new ArrayList<>(list.size());
        for (int i = 0; i < 8 * list.size() / 10; i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

    /**
     * 向量归一化处理,[0-1]
     * @param features
     * @param k
     */
    public static void normalization(double[][] features, int k) {
        double max, min;
        for (int i = 0; i < k; i++) {
            max = 0;
            min = Integer.MAX_VALUE;
            for (int j = 0; j < features.length; j++) {
                if (features[j][i] > max) {
                    max = features[j][i];
                }
                if (features[j][i] < min) {
                    min = features[j][i];
                }
            }
            for (int j = 0; j < features.length; j++) {
                features[j][i] = (features[j][i] - min) / (max - min);
            }
        }
    }

    /**
     * 从全部向量中复制出需要的部分数据进行分类
     * @param features
     * @param data
     * @param winners
     * @param user
     * @param index
     */
    public static void copy(double[][] features, double[][] data, List<String> winners, List<String> user, List<Integer> index) {
        int row = index.size(), column = features[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                data[i][j] = features[index.get(i)][j];
            }
            user.add(winners.get(index.get(i)));
        }
    }
}
