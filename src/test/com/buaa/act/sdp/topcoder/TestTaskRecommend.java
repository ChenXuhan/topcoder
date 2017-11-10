package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.experiment.TaskRecommend;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.recommend.feature.Reliability;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by yang on 2017/3/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestTaskRecommend {

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private TaskRecommend recommendResult;

    @Autowired
    private Reliability reliability;

    @Test
    public void testFeatureExtract() {
        featureExtract.getFeatures("Code");
    }

    @Test
    public void testRecommend() {
//        Code
//        First2Finish
//        Assembly Competition
        String challengeType = "Assembly Competition";
//        recommendResult.contentBased(challengeType);
//        recommendResult.classifier(challengeType);
//        recommendResult.clusterClassifier(challengeType, 4);
//        recommendResult.localClassifier(challengeType);
        recommendResult.dcw_ds(challengeType);
    }

    @Test
    public void testTimeInterval() {
        reliability.timeInterval("Code");
    }

    @Test
    public void taskTime() {
        List<ChallengeItem> items = featureExtract.getItems("First2Finish");
        List<String> time = new ArrayList<>(items.size());
        for (ChallengeItem item : items) {
            time.add(item.getPostingDate().substring(0, 10));
        }
//        Collections.sort(time, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                String[]a=o1.split("-");
//                String[]b=o2.split("-");
//                if(Integer.parseInt(a[0])!=Integer.parseInt(b[0])){
//                    return Integer.parseInt(a[0])-Integer.parseInt(b[0]);
//                }
//                if(Integer.parseInt(a[1])!=Integer.parseInt(b[1])){
//                    return Integer.parseInt(a[1])-Integer.parseInt(b[1]);
//                }
//                return Integer.parseInt(a[2])-Integer.parseInt(b[2]);
//            }
//        });
        int[][] count = new int[12][12];
        for (String a : time) {
            count[Integer.parseInt(a.split("-")[0]) - 2006][Integer.parseInt(a.split("-")[1]) - 1]++;
        }
        for (int i = 0; i < count.length; i++) {
            System.out.println(2006 + i + "\t" + Arrays.toString(count[i]));
        }
    }
}
