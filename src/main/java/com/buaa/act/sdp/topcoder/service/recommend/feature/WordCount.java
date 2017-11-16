package com.buaa.act.sdp.topcoder.service.recommend.feature;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.*;

/**
 * Created by yang on 2017/2/16.
 */
public class WordCount {

    /**
     * 待处理的文本
     * 所有的单词及次数
     * 每一个任务的单词数量
     * 每一个任务中每一个单词的数量
     */
    private String[] texts;
    private Map<String, Integer> allWords;
    private List<Integer> taskWords;
    private List<Map<String, Integer>> taskWordCount;


    public WordCount(String[] texts) {
        this.texts = texts;
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

    public int getWordSize() {
        return allWords.size();
    }

    /**
     * 分词，去除停顿词，获取所有的单词
     *
     * @return
     */
    public List<String>[] getWordsFromText() {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream;
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

    /**
     * 预处理，分别统计每段话的单词类别数及其数量
     *
     * @param start
     */
    public void init(int start) {
        List<String>[] words = getWordsFromText();
        List<String> word;
        int len = 10;
        for (int i = 0; i < texts.length; i++) {
            word = words[i];
            taskWords.add(word.size());
            Map<String, Integer> map = new HashMap<>();
            for (String s : word) {
                if (map.containsKey(s)) {
                    map.put(s, map.get(s) + 1);
                } else {
                    map.put(s, 1);
                    if (allWords.containsKey(s)) {
                        allWords.put(s, allWords.get(s) + 1);
                    } else {
                        allWords.put(s, 1);
                    }
                }
                if (i < start) {
                    List<Map.Entry<String, Integer>> list = new ArrayList<>();
                    list.addAll(map.entrySet());
                    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            return o2.getValue() - o1.getValue();
                        }
                    });
                    for (int j = 0; j < len && j < list.size(); j++) {
                        if (allWords.containsKey(list.get(j).getKey())) {
                            allWords.put(list.get(j).getKey(), allWords.get(list.get(j).getKey()) + 1);
                        } else {
                            allWords.put(list.get(j).getKey(), 1);
                        }
                    }
                }
            }
            taskWordCount.add(map);
        }
    }

    /**
     * 计算某一token的tf
     *
     * @param index
     * @return
     */
    public double[] getTf(int index) {
        double[] tf = new double[allWords.size()];
        int k = 0;
        Map<String, Integer> map;
        for (Map.Entry<String, Integer> entry : allWords.entrySet()) {
            map = taskWordCount.get(index);
            if (map.containsKey(entry.getKey())) {
                tf[k++] = 1 + Math.log(map.get(entry.getKey()));
            } else {
                tf[k++] = 1;
            }
        }
        return tf;
    }

    /**
     * 计算idf
     *
     * @return
     */
    public double[] getIdf() {
        double[] idf = new double[allWords.size()];
        int index = 0, num;
        for (Map.Entry<String, Integer> entry : allWords.entrySet()) {
            num = entry.getValue();
            idf[index++] = Math.log(1.0 * taskWords.size() / num);
        }
        return idf;
    }

    /**
     * 计算tf-idf
     *
     * @return
     */
    public List<double[]> getTfIdf() {
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

}
