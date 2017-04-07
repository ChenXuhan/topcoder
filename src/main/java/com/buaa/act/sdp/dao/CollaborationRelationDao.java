package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.CollaborationRelation;

/**
 * Created by YLT on 2017/3/19.
 */
public interface CollaborationRelationDao {
    void insert(CollaborationRelation collaborationRelation);
    void deleteAll();
}
