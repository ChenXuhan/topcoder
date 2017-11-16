package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.user.DevelopmentHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface DevelopmentHistoryDao {

    void insertBatch(List<DevelopmentHistory> list);

    void updateBatch(List<DevelopmentHistory> list);

    List<DevelopmentHistory> getDevelopmentHistoryByHandle(@Param("handle") String handle);
}
