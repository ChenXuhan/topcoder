package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.user.Registrant;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yang on 2017/11/17.
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @ResponseBody
    @RequestMapping("/detail")
    public TCResponse<ChallengeItem> getTaskById(@RequestParam("taskId") int taskId) {
        logger.info("get task detail info,taskId=" + taskId);
        TCResponse<ChallengeItem> response = new TCResponse<>();
        try {
            ChallengeItem item = taskService.getChallengeById(taskId);
            if (item == null) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(item);
        } catch (Exception e) {
            logger.error("error occurred when getting task,taskId=" + taskId, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/all")
    public TCResponse<List<Integer>> getAllTasks() {
        logger.info("get all tasks");
        TCResponse<List<Integer>> response = new TCResponse<>();
        try {
            List<Integer> data = taskService.getAllTasks();
            response.setSuccessResponse(data);
        } catch (Exception e) {
            response.setErrorResponse();
            logger.error("error occurred in getting all tasks", e);
        }
        return response;
    }

    @RequestMapping("/register")
    @ResponseBody
    public TCResponse<List<Registrant>> getTaskRegistrant(@RequestParam("taskId") int taskId) {
        logger.info("get a task registrants info");
        TCResponse<List<Registrant>> response = new TCResponse<>();
        try {
            List<Registrant> data = taskService.getTaskRegistrants(taskId);
            if (data.isEmpty()) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(data);
        } catch (Exception e) {
            response.setErrorResponse();
            logger.error("error occurred in getting a task registrants");
        }
        return response;
    }
}
