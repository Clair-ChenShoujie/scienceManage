package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.Activity;
import com.example.entity.Feedback;
import com.example.entity.Project;
import com.example.entity.Teacher;
import com.example.service.ActivityService;
import com.example.service.FeedbackService;
import com.example.service.ProjectService;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计前端调用的接口入口
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private ProjectService projectService;
    @Resource
    private FeedbackService feedbackService;
    @Resource
    private ActivityService activityService;
    @Resource
    private TeacherService teacherService;

    @GetMapping("/base")
    public Result base() {
        Map<String, Integer> resultMap = new HashMap<>();

        Project project = new Project();
        project.setStatus("审核通过");
        List<Project> projects = projectService.selectAll(project);

        List<Feedback> feedbacks = feedbackService.selectAll(new Feedback());
        List<Activity> activities = activityService.selectAll(new Activity());
        List<Teacher> teachers = teacherService.selectAll(new Teacher());

        resultMap.put("project", projects.size());
        resultMap.put("feedback", feedbacks.size());
        resultMap.put("activity", activities.size());
        resultMap.put("teacher", teachers.size());

        return Result.success(resultMap);
    }

    @GetMapping("/pie")
    public Result pie() {
        List<Map<String, Object>> result = new ArrayList<>();
        Project project = new Project();
        project.setStatus("审核通过");
        List<Project> projects = projectService.selectAll(project);
        Map<String, Long> collect = projects.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getLevel()))
                .collect(Collectors.groupingBy(Project::getLevel, Collectors.counting()));
        for (String key : collect.keySet()) {
            Map<String, Object> temMap = new HashMap<>();
            temMap.put("name", key);
            temMap.put("value", collect.get(key));
            result.add(temMap);
        }
        return Result.success(result);
    }


}