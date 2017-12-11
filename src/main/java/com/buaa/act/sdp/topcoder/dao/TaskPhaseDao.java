package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.task.TaskPhase;

/**
 * Created by yang on 2016/10/19.
 */
public interface TaskPhaseDao {

    void insertBatch(TaskPhase[] taskPhases);

}
