package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.user.User;
import org.apache.ibatis.annotations.Param;

/**
 * Created by yang on 2016/10/15.
 */
public interface UserDao {
     void insert(User user);
     User getUserByName(@Param("name") String name);
}
