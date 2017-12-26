package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.developer.Competitor;
import com.buaa.act.sdp.topcoder.model.developer.DeveloperInfo;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.basic.DeveloperService;
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
@RequestMapping(value = "/developer")
public class DeveloperController {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperController.class);

    @Autowired
    private DeveloperService developerService;

    @ResponseBody
    @RequestMapping("/info")
    public TCResponse<DeveloperInfo> getDeveloperInfo(@RequestParam("userName") String userName) {
        logger.info("get developer profile, name=" + userName);
        TCResponse<DeveloperInfo> response = new TCResponse<>();
        try {
            DeveloperInfo info = developerService.getDeveloperInfo(userName);
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
    public TCResponse<List<TaskItem>> getDeveloperRegisterTasks(@RequestParam("userName") String userName) {
        logger.info("get developer's registered tasks, userName=" + userName);
        TCResponse<List<TaskItem>> response = new TCResponse<>();
        try {
            List<TaskItem> data = developerService.getDeveloperRegistrantTasks(userName);
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

    @ResponseBody
    @RequestMapping("/competitors")
    public TCResponse<List<Competitor>> getMostAttractiveCompetitors(@RequestParam("userName") String userName) {
        logger.info("get developer's most attractive competitors, userName=" + userName);
        TCResponse<List<Competitor>> response = new TCResponse<>();
        try {
            List<Competitor> data = developerService.getDeveloperCompetitors(userName);
            if (data == null) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(data);
        } catch (Exception e) {
            logger.error("error occurred in getting developer's most attractive competitors, userName=" + userName, e);
            response.setErrorResponse();
        }
        return response;
    }
}
