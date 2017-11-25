package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
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
 * Created by yang on 2017/11/24.
 */
@Controller
@RequestMapping("/project")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private TaskService taskService;

    @ResponseBody
    @RequestMapping("/tasks")
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
            logger.error("error occurred when get tasks in a project, projectId=" + projectId);
            response.setErrorResponse();
        }
        return response;
    }
}
