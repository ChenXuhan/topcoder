package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.user.UserInfo;
import com.buaa.act.sdp.topcoder.service.basic.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yang on 2016/10/16.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/info")
    public TCResponse<UserInfo> getDeveloperInfo(@RequestParam("userName") String userName) {
        logger.info("get developer profile, name=" + userName);
        TCResponse<UserInfo> response = new TCResponse<>();
        try {
            UserInfo info = userService.getDeveloperInfo(userName);
            if (info == null) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(info);
        } catch (Exception e) {
            logger.error("error occurred in getting developer profile,name=" + userName, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/tasks")
    public TCResponse<List<Integer>> getDeveloperRegisterTasks(@RequestParam("userName") String userName) {
        logger.info("get developer's registered tasks, userName=" + userName);
        TCResponse<List<Integer>> response = new TCResponse<>();
        try {
            List<Integer> data = userService.getUserRegistrantTasks(userName);
            if (data == null || data.isEmpty()) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(data);
        } catch (Exception e) {
            logger.error("error occurred in getting developer's registered tasks, name=" + userName, e);
            response.setErrorResponse();
        }
        return response;
    }

}
