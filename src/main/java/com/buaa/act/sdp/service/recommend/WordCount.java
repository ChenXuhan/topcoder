package com.buaa.act.sdp.service.recommend;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by yang on 2017/2/16.
 */
public class WordCount {
    //所有的单词及次数
    private Map<String, Integer> allWords;
    //每一个任务的单词数量
    private List<Integer> taskWords;
    //每一个任务中每一个单词的数量
    private List<Map<String, Integer>> taskWordCount;

    public WordCount() {
        allWords = new HashMap<>();
        taskWords = new ArrayList<>();
        taskWordCount = new ArrayList<>();
    }

    public Map<String, Integer> getAllWords() {
        return allWords;
    }

    public void setAllWords(Map<String, Integer> allWords) {
        this.allWords = allWords;
    }

    public List<Integer> getTaskWords() {
        return taskWords;
    }

    public void setTaskWords(List<Integer> taskWords) {
        this.taskWords = taskWords;
    }

    public List<Map<String, Integer>> getTaskWordCount() {
        return taskWordCount;
    }

    public void setTaskWordCount(List<Map<String, Integer>> taskWordCount) {
        this.taskWordCount = taskWordCount;
    }

    public List<String>[] getWordsFromText(String[] texts) {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = null;
        List<String>[] words = new List[texts.length];
        try {
            for (int i = 0; i < texts.length; i++) {
                List<String> word = new ArrayList<>();
                tokenStream = analyzer.tokenStream(null, texts[i]);
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

    public void init(String[] texts,int start) {
        List<String>[] words = getWordsFromText(texts);
        List<String> word;
        for (int i = 0; i < texts.length; i++) {
            word = words[i];
            taskWords.add(word.size());
            Map<String, Integer> map = new HashMap<>();
            if (word != null) {
                for (String s : word) {
                    if (map.containsKey(s)) {
                        map.put(s, map.get(s) + 1);
                    } else {
                        map.put(s, 1);
                        if(i<start) {
                            if (allWords.containsKey(s)) {
                                allWords.put(s, allWords.get(s) + 1);
                            } else {
                                allWords.put(s, 1);
                            }
                        }
                    }
                }
//                if(i<start) {
//                    List<Map.Entry<String, Integer>> list = new ArrayList<>();
//                    list.addAll(map.entrySet());
//                    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
//                        @Override
//                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                            return o2.getValue() - o1.getValue();
//                        }
//                    });
//                    for (int j = 0; j < 10 && j < list.size(); j++) {
//                        if (allWords.containsKey(list.get(j).getKey())) {
//                            allWords.put(list.get(j).getKey(), allWords.get(list.get(j).getKey()) + 1);
//                        } else {
//                            allWords.put(list.get(j).getKey(), 1);
//                        }
//                    }
//                }
            }
            taskWordCount.add(map);
        }
    }

    public double[] getTf(int index) {
        double[] tf = new double[allWords.size()];
        int k = 0;
        Map<String, Integer> map;
        for (Map.Entry<String, Integer> entry : allWords.entrySet()) {
            map = taskWordCount.get(index);
            if (map.containsKey(entry.getKey())) {
                tf[k++] = 1.0 * map.get(entry.getKey()) / taskWords.get(index);
            } else {
                tf[k++] = 0;
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

    public List<double[]> getTfIdf(String[] texts) {
        init(texts,0);
        List<double[]> tfIdf = new ArrayList<>();
        double[] idf = getIdf();
        for (int i = 0; i < texts.length; i++) {
            double[] tf = getTf(i);
            for (int j = 0; j < tf.length; j++) {
                tf[j] *= idf[j];
            }
            tfIdf.add(tf);
        }
        return tfIdf;
    }

    public BigDecimal getTypeProbality(int k, List<Integer> list) {
        BigDecimal bigDecimal=BigDecimal.valueOf(1.0);
        Map<String, Integer> map;
        Map<String, Integer> current = taskWordCount.get(k);
        int count, sum;
        for (Map.Entry<String, Integer> entry : current.entrySet()) {
            count = 0;
            sum = 0;
            for (int index : list) {
                map = taskWordCount.get(index);
                if (map.containsKey(entry.getKey())) {
                    count += map.get(entry.getKey());
                }
                sum += taskWords.get(index);
            }
            BigDecimal decimal=BigDecimal.valueOf(1.0 * (count + 1) / (sum + allWords.size()));
            for(int j=0;j<entry.getValue();j++){
                bigDecimal=bigDecimal.multiply(decimal);
            }
        }
        return bigDecimal;
    }
}
