package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.*;

import com.example.service.*;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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
    @Resource
    private ApplyService applyService;
    @Resource
    private AchievementService achievementService;

    @GetMapping("/base")
    public Result base() {
        Map<String, Integer> resultMap = new HashMap<>();

        Project project = new Project();
        project.setStatus("审核通过");
        List<Project> projects = projectService.selectAll(project);

//        List<Feedback> feedbacks = feedbackService.selectAll(new Feedback());
        Achievement achievement = new Achievement();
        achievement.setStatus("审核通过");
        List<Achievement> achievements = achievementService.selectAll(achievement);
        List<Activity> activities = activityService.selectAll(new Activity());
        List<Teacher> teachers = teacherService.selectAll(new Teacher());

        resultMap.put("project", projects.size());
        resultMap.put("achievement", achievements.size());
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

    @GetMapping("/bar1")
    public Result bar1() {
        Map<String, List<Object>> resultMap = new HashMap<>();
        List<Object> xList = new ArrayList<>();
        List<Object> yList = new ArrayList<>();

        // 封装xList和yList里面的值
        List<Apply> applies = applyService.selectAll(new Apply());
        Map<String, Long> collect = applies.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getActivityName()))
                .collect(Collectors.groupingBy(Apply::getActivityName, Collectors.counting()));
        for (String key : collect.keySet()) {
            xList.add(key);
            yList.add(collect.get(key));
        }

        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);
        return Result.success(resultMap);
    }

    @GetMapping("/bar2")
    public Result bar2() {
        Map<String, List<Object>> resultMap = new HashMap<>();
        List<Object> xList = new ArrayList<>();
        List<Object> yList = new ArrayList<>();

        // 封装xList和yList里面的值
        Achievement achievement = new Achievement();
        achievement.setStatus("审核通过");
        List<Achievement> achievements = achievementService.selectAll(achievement);
        Map<String, Long> collect = achievements.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getTypeName()))
                .collect(Collectors.groupingBy(Achievement::getTypeName, Collectors.counting()));

        for (String key : collect.keySet()) {
            xList.add(key);
            yList.add(collect.get(key));
        }

        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);
        return Result.success(resultMap);
    }

    @GetMapping("/line")
    public Result line() {
        Map<String, List<String>> resultMap = new HashMap<>();
        List<String> yList = new ArrayList<>();

        // 封装xList和yList里面的值
        List<Feedback> feedbacks = feedbackService.selectAll(new Feedback());
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today, -7);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate).toList();
        for (Object xTime : xList) {
            int size = feedbacks.stream().filter(x -> ObjectUtil.isNotEmpty(x.getTime()) && x.getTime().contains(xTime + "")).toList().size();
            yList.add(Integer.toString(size));
        }

        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);
        return Result.success(resultMap);
    }

    @GetMapping("/wordCloud")
    public Result wordCloud() {
        Achievement achievement = new Achievement();
        achievement.setStatus("审核通过");
        List<Achievement> achievements = achievementService.selectAll(achievement);

        Map<String, Long> collect = achievements.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getTypeName()))
                .collect(Collectors.groupingBy(Achievement::getTypeName, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("value", collect.get(key));
            result.add(map);
        }
        return Result.success(result);
    }

    @GetMapping("/radar")
    public Result radar() {
        Project project = new Project();
        project.setStatus("审核通过");
        List<Project> projects = projectService.selectAll(project);

        Map<String, Long> collect = projects.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getLevel()))
                .collect(Collectors.groupingBy(Project::getLevel, Collectors.counting()));

        List<String> indicator = new ArrayList<>(collect.keySet());
        List<Long> values = new ArrayList<>(collect.values());

        Map<String, Object> result = new HashMap<>();
        result.put("indicator", indicator);
        result.put("values", values);

        return Result.success(result);
    }

    @GetMapping("/heatmap")
    public Result heatmap() {
        List<Apply> applies = applyService.selectAll(new Apply());

        // 统计每个教师申请每个活动的次数
        Map<String, Map<String, Long>> teacherActivityCount = new HashMap<>();
        for (Apply apply : applies) {
            String teacher = apply.getTeacherName() == null ? "未知教师" : apply.getTeacherName();
            String activity = apply.getActivityName() == null ? "未知活动" : apply.getActivityName();
            teacherActivityCount.putIfAbsent(activity, new HashMap<>());
            Map<String, Long> teacherMap = teacherActivityCount.get(activity);
            teacherMap.put(teacher, teacherMap.getOrDefault(teacher, 0L) + 1);
        }

        List<String> activities = new ArrayList<>(teacherActivityCount.keySet());
        Set<String> teacherSet = new HashSet<>();
        teacherActivityCount.values().forEach(map -> teacherSet.addAll(map.keySet()));
        List<String> teachers = new ArrayList<>(teacherSet);

        // 生成二维数据
        List<List<Object>> data = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            for (int j = 0; j < teachers.size(); j++) {
                long value = teacherActivityCount.get(activities.get(i)).getOrDefault(teachers.get(j), 0L);
                data.add(Arrays.asList(j, i, value));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("xAxis", teachers);
        result.put("yAxis", activities);
        result.put("data", data);

        return Result.success(result);
    }

}