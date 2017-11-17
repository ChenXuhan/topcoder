package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yang on 2017/11/17.
 */
@Controller
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @ResponseBody
    public TCResponse<ChallengeItem> getTaskById(@RequestParam("taskId") int taskId) {
        logger.info("get task,taskId=" + taskId);
        TCResponse<ChallengeItem> response = new TCResponse<>();
        try {
            ChallengeItem item = taskService.getChallengeById(taskId);
            if (item == null) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(item);
        } catch (Exception e) {
            logger.error("error occurred when get task,taskId=" + taskId, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    public TCResponse<List<Integer>> getProjectTasks(@RequestParam("projectId") int projectId) {
        logger.info("get tasks in a project,projectId=" + projectId);
        TCResponse<List<Integer>> response = new TCResponse<>();
        try {
            List<Integer> tasks = taskService.getProjectTasks(projectId);
            if (tasks.size() == 0) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(tasks);
        } catch (Exception e) {
            logger.error("error occurred when get tasks for a project, projectId=" + projectId);
            response.setErrorResponse();
        }
        return response;
    }


    @ResponseBody
    public TCResponse<List<Integer>> getAllTasks() {
        logger.info("get all tasks");
        TCResponse<List<Integer>> response = new TCResponse<>();
        try {
            List<Integer> data = taskService.getAllTasks();
            response.setSuccessResponse(data);
        } catch (Exception e) {
            response.setErrorResponse();
            logger.error("error occurred in getAllTasks", e);
        }
        return response;
    }

}
