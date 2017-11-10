package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.user.User;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface UserDao {

    void insert(User user);

    void update(User user);

    List<String> getDistinctUsers();

    List<User> getAllUsers();

    void updateUser(User user);

}
