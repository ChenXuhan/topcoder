package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.service.recommend.WordCount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by yang on 2017/2/19.
 */
@Component
public class Bayes {

    public BigDecimal getClassProbality(double[][]features,double[]feature,String type,Map<String,List<Integer>>lableIndexMap){
        List<Integer>list=lableIndexMap.get(type);
        double[]vector;
        int[]count=new int[feature.length];
        for(int i=0;i<count.length;i++){
            count[i]=0;
        }
        BigDecimal result=BigDecimal.valueOf(1.0);
        for(int i=0;i<feature.length;i++){
            for (int j = 0; j < list.size(); j++) {
                vector= features[list.get(j)];
                if(Math.abs(vector[i]-feature[i])<0.1){
                    count[i]++;
                }
            }
        }
        for(int i=0;i<count.length;i++){
            result=result.multiply(BigDecimal.valueOf(1.0*count[i]/list.size()));
        }
        result= result.multiply(BigDecimal.valueOf(1.0*list.size()));
        return result;
    }

    public Map<String,Double> getAllClassProbality(double[][]features, double[]feature, Map<String,List<Integer>>lableIndexMap){
        Map<String,BigDecimal>map=new HashMap<>();
        BigDecimal bigDecimal=BigDecimal.valueOf(0.0),temp;
        for(String type:lableIndexMap.keySet()){
            temp=getClassProbality(features,feature,type,lableIndexMap);
            bigDecimal=bigDecimal.add(temp);
            map.put(type,temp);
        }
        Map<String,Double>workerMap=new HashMap<>();
        for(String type:lableIndexMap.keySet()){
            if(bigDecimal.compareTo(BigDecimal.valueOf(0))==0){
                workerMap.put(type,0.0);
            }else {
                workerMap.put(type, map.get(type).divide(bigDecimal, 10, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
        }
        return workerMap;
    }

    public List<Map<String,Double>>getRecommendResult(double[][]features,List<String>winners,int start){
        Map<String,List<Integer>> lableIndexMap=getLableIndexMap(winners,start);
        double[]testFeature;
        List<Map<String,Double>>result=new ArrayList<>();
        for(int i=start;i<features.length;i++){
            testFeature=features[i];
            result.add(getAllClassProbality(features,testFeature,lableIndexMap));
        }
        return result;
    }

    public Map<String,List<Integer>> getLableIndexMap(List<String>winners,int start) {
        Map<String, List<Integer>> lableIndexMap = new HashMap<>();
        for (int i = 0; i < start; i++) {
            if (lableIndexMap.containsKey(winners.get(i))) {
                lableIndexMap.get(winners.get(i)).add(i);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                lableIndexMap.put(winners.get(i), list);
            }
        }
        return lableIndexMap;
    }

    public  List<Map<String,Double>>getRecommendResultUcl(WordCount[]wordCounts,double[][]features,List<String>winners,int start){
        Map<String,List<Integer>>indexMap=getLableIndexMap(winners,start);
        List<Map<String,Double>>list=new ArrayList<>(winners.size()-start);
        for(int i=start;i<winners.size();i++){
            list.add(getTypeProbalityUcl(wordCounts,features,indexMap,i));
        }
        return list;
    }

    public Map<String,Double>getTypeProbalityUcl(WordCount[]wordCounts, double[][]features,Map<String,List<Integer>>indexMap,int index){
        Map<String,BigDecimal>map=new HashMap<>();
        BigDecimal sum=BigDecimal.valueOf(0.0);
        for(Map.Entry<String,List<Integer>>entry:indexMap.entrySet()){
            BigDecimal bigDecimal=BigDecimal.valueOf(1.0);
            bigDecimal=bigDecimal.multiply(wordCounts[0].getTypeProbality(index,entry.getValue()));
            bigDecimal=bigDecimal.multiply(wordCounts[1].getTypeProbality(index,entry.getValue()));
            bigDecimal=bigDecimal.multiply(wordCounts[2].getTypeProbality(index,entry.getValue()));
            bigDecimal=bigDecimal.multiply(getClassProbality(features,features[index],entry.getKey(),indexMap));
            sum=sum.add(bigDecimal);
            map.put(entry.getKey(),bigDecimal);
        }
        Map<String,Double>result=new HashMap<>();
        for(String type:indexMap.keySet()){
            if(sum.compareTo(BigDecimal.valueOf(0))==0){
                result.put(type,0.0);
            }else {
                result.put(type, map.get(type).divide(sum, 10, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
        }
        return result;
    }
}
