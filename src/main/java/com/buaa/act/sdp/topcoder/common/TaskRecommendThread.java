package com.buaa.act.sdp.topcoder.common;

import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by yang on 2017/12/5.
 */
public class TaskRecommendThread implements Callable<List<String>> {

    private TaskItem item;
    private DeveloperRecommend developerRecommend;

    public TaskRecommendThread() {
    }

    public TaskRecommendThread(TaskItem item, DeveloperRecommend developerRecommend) {
        this.item = item;
        this.developerRecommend = developerRecommend;
    }

    @Override
    public List<String> call() throws Exception {
        return developerRecommend.recommendDevelopers(item);
    }
}
