package com.legacy.report.controller;

import com.legacy.report.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件控制器 - 提供邮件相关接口
 */
@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * 获取当前环境的checker邮件组
     * @return 邮件组信息
     */
    @GetMapping("/checker-group")
    public ResponseEntity<Map<String, Object>> getCheckerEmailGroup() {
        Map<String, Object> response = new HashMap<>();
        String email = emailService.getCheckerEmailGroup();
        String profile = emailService.getActiveProfile();
        
        response.put("success", true);
        response.put("email", email);
        response.put("profile", profile);
        response.put("message", "已获取环境 " + profile + " 的邮件组: " + email);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 模拟发送邮件给checker
     * @param reportId 报表ID
     * @return 发送结果
     */
    @PostMapping("/send-to-checker")
    public ResponseEntity<Map<String, Object>> sendEmailToChecker(@RequestParam Long reportId) {
        Map<String, Object> response = new HashMap<>();
        String email = emailService.getCheckerEmailGroup();
        String profile = emailService.getActiveProfile();
        
        // 模拟发送邮件逻辑，实际只是返回邮件信息
        response.put("success", true);
        response.put("email", email);
        response.put("profile", profile);
        response.put("reportId", reportId);
        response.put("message", "邮件已发送至 " + email + " (环境: " + profile + ")");
        
        return ResponseEntity.ok(response);
    }
}
