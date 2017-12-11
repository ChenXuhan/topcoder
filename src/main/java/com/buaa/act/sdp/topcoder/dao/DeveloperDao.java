package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.developer.Developer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface DeveloperDao {

    void insert(Developer developer);

    void updateDeveloperBasicInfo(Developer developer);

    List<String> getDistinctDevelopers();

    List<Developer> getAllDevelopers();

    void updateTask(Developer developer);

    Developer getDeveloperByName(@Param("userName") String userName);
}
