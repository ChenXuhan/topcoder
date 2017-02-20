package com.buaa.act.sdp.service.recommend.classification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/2/19.
 */
@Service
public class Bayes {
    @Autowired
    private FeatureExtract featureExtract;

    private Map<String,List<Integer>> lableIndex;

    private List<String>handle;

    public double[][] getFeature() {
        lableIndex = new HashMap<>();
        double[][]features=featureExtract.getFeatures();
        handle=featureExtract.getHandle();
        for(int i=0;i<(int)(handle.size()*0.9);i++){
            if(lableIndex.containsKey(handle.get(i))){
                lableIndex.get(handle.get(i)).add(i);
            }else{
                List<Integer>list=new ArrayList<>();
                list.add(i);
                lableIndex.put(handle.get(i),list);
            }
        }
        return features;
    }

    public double getClassProbality(double[][]features,double[]feature,String type,Map<String,List<Integer>>lableIndex){
        List<Integer>list=lableIndex.get(type);
        double[]vector;
        int[]count=new int[feature.length];
        for(int i=0;i<count.length;i++){
            count[i]=0;
        }
        for(int i=0;i<list.size();i++){
            vector=features[list.get(i)];
            for(int j=0;j<feature.length;j++){
                if(feature[j]==vector[j]){
                    count[j]++;
                }
            }
        }
        double result=1;
        for(int i=0;i<count.length;i++){
            result*=(1.0*count[i]/list.size());
        }
        return result*list.size();
    }

    public List<Map.Entry<String,Double>> getAllClassProbality(double[][]features, double[]feature, Map<String,List<Integer>>lableIndex){
        Map<String,Double>result=new HashMap<>();
        for(String type:lableIndex.keySet()){
            result.put(type,getClassProbality(features,feature,type,lableIndex));
        }
        List<Map.Entry<String,Double>>sortResult=new ArrayList<>();
        sortResult.addAll(result.entrySet());
        Collections.sort(sortResult, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if(o1.getValue()<o2.getValue()){
                    return 1;
                }else if(o1.getValue()==o2.getValue()){
                    return 0;
                }else {
                    return -1;
                }
            }
        });
        return sortResult;
    }

    public double calAccurary(){
        List<List<Map.Entry<String,Double>>>result=getRecommendResult();
        int start=(int)(handle.size()*0.9),count=0;
        List<Map.Entry<String,Double>>temp;
        for(int i=0;i<result.size();i++){
            temp=result.get(i);
            for(int j=0;j<temp.size()&&j<10;j++){
                if(temp.get(j).getKey().equals(handle.get(start+i))){
                    count++;
                    break;
                }
            }
        }
        return 1.0*count/(handle.size()-start);
    }

    public List<List<Map.Entry<String,Double>>>getRecommendResult(){
        double[][]features=getFeature();
        int trainLength=(int)(features.length*0.9);
        double[]testFeature;
        List<List<Map.Entry<String,Double>>>result=new ArrayList<>();
        for(int i=trainLength;i<features.length;i++){
            testFeature=features[i];
            result.add(getAllClassProbality(features,testFeature,lableIndex));
        }
        return result;
    }
}
