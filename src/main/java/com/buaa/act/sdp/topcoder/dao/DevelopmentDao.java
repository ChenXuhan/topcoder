package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.developer.Development;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface DevelopmentDao {

    void insertBatch(List<Development> list);

    void updateBatch(List<Development> list);

    List<Development> getDeveloperDevelopment(@Param("userName") String userName);
}
