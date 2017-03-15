package com.buaa.act.sdp.service.recommend;

import com.buaa.act.sdp.util.WekaArffUtil;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

/**
 * Created by yang on 2017/3/13.
 */
public class Cluster {
    public static void main(String[] args) {
        SimpleKMeans kMeans=new SimpleKMeans();
        Instances instances= WekaArffUtil.getInstances("Design");
        try {
            kMeans.setNumClusters(10);
            kMeans.buildClusterer(instances);
            System.out.println(kMeans.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
