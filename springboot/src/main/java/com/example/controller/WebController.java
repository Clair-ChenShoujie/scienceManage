package com.example.controller;

import com.example.common.Result;
import com.example.common.config.AutoLog;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Teacher;
import com.example.service.AdminService;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    private TeacherService teacherService;

    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello () {
        return Result.success();
    }

//    @AutoLog("登录系统")
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account loginAccount = null;
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            loginAccount = adminService.login(account);
        }
        if (RoleEnum.TEACHER.name().equals(account.getRole())) {
            loginAccount = teacherService.login(account);
        }
        return Result.success(loginAccount);
    }

//    @AutoLog("注册系统")
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        Teacher teacher = new Teacher();
        if (RoleEnum.TEACHER.name().equals(account.getRole())) {
            teacher = teacherService.register(account);
        }
        return Result.success(teacher);
    }


    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        }
        if (RoleEnum.TEACHER.name().equals(account.getRole())) {
            teacherService.updatePassword(account);
        }
        return Result.success();
    }
}
