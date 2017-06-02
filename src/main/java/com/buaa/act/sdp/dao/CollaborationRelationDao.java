package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.model.challenge.CollaborationRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by YLT on 2017/3/19.
 */
public interface CollaborationRelationDao {
    void insert(CollaborationRelation collaborationRelation);

    void deleteAll();

    List<String> getCollaborations(@Param("handle1") String handle1);
}
