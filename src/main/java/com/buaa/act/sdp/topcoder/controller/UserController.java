package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.service.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yang on 2016/10/16.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserApi userApi;

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public String saveUserByName(@RequestParam("userName") String userName) {
        userApi.getUserByName(userName);
        return "success";
    }
}
