package com.buaa.act.sdp.service.recommend.classification;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.*;

/**
 * Created by yang on 2017/2/16.
 */
public class TfIdf {
    //所有的单词及次数
    private Map<String, Integer> allWords;
    //每一个任务的单词数量
    private Map<Integer, Integer> taskWords;
    //每一个任务中每一个单词的数量
    private Map<Integer, Map<String, Integer>> taskWordCount;

    public TfIdf() {
        allWords = new HashMap<>();
        taskWords = new HashMap<>();
        taskWordCount = new HashMap<>();
    }

    public List<String>[] getWordsFromRequirement(String[] requirements) {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = null;
        List<String>[] words = new List[requirements.length];
        int i = 1;
        try {
            for (i = 0; i < requirements.length; i++) {
                List<String> word = new ArrayList<>();
                tokenStream = analyzer.tokenStream(null, requirements[i]);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);
                    word.add(ch.toString());
                }
                words[i] = word;
                tokenStream.end();
                tokenStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public void init(int[] challengeIds, String[] requirements) {
        List<String>[] words = getWordsFromRequirement(requirements);
        List<String> word;
        for (int i = 0; i < challengeIds.length; i++) {
            word = words[i];
            taskWords.put(challengeIds[i], word.size());
            Map<String, Integer> map = new HashMap<>();
            if (word != null) {
                for (String s : word) {
                    if (map.containsKey(s)) {
                        map.put(s, map.get(s) + 1);
                    } else {
                        map.put(s, 1);
//                        if (allWords.containsKey(s)) {
//                            allWords.put(s, allWords.get(s) + 1);
//                        } else {
//                            allWords.put(s, 1);
//                        }
                    }
                }
                List<Map.Entry<String, Integer>> list = new ArrayList<>();
                list.addAll(map.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });
                for (int j = 0; j < 15 && j < list.size(); j++) {
                    if (allWords.containsKey(list.get(j).getKey())) {
                        allWords.put(list.get(j).getKey(), allWords.get(list.get(j).getKey()) + 1);
                    } else {
                        allWords.put(list.get(j).getKey(), 1);
                    }
                }
            }
            taskWordCount.put(challengeIds[i], map);
        }
    }

    public double[] getTf(Integer challengeId) {
        double[] tf = new double[allWords.size()];
        int index = 0;
        Map<String, Integer> map;
        for (Map.Entry<String, Integer> entry : allWords.entrySet()) {
            map = taskWordCount.get(challengeId);
            if (map.containsKey(entry.getKey())) {
                tf[index++] = 1.0 * map.get(entry.getKey()) / taskWords.get(challengeId);
            } else {
                tf[index++] = 0;
            }
        }
        return tf;
    }

    public double[] getIdf() {
        double[] idf = new double[allWords.size()];
        int index = 0, num;
        for (Map.Entry<String, Integer> entry : allWords.entrySet()) {
            num = entry.getValue();
            idf[index++] = Math.log(taskWords.size() * 1.0 / num);
        }
        return idf;
    }

    public List<double[]> getTfIdf(int[] challengeIds, String[] requirements) {
        init(challengeIds, requirements);
        List<double[]> tfIdf = new ArrayList<>();
        double[] idf = getIdf();
        for (int id : challengeIds) {
            double[] tf = getTf(id);
            for (int i = 0; i < tf.length; i++) {
                tf[i] *= idf[i];
            }
            tfIdf.add(tf);
        }
        return tfIdf;
    }

    public double cosinSimilarity(double[] vectorOne, double[] vectorTwo) {
        double num = 0, a = 0, b = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            num += vectorOne[i] * vectorTwo[i];
            a += vectorOne[i] * vectorOne[i];
            b += vectorTwo[i] * vectorTwo[i];
        }
        return num / Math.sqrt(a * b);
    }

}
