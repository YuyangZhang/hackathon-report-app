package com.legacy.report.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件服务 - 根据环境返回对应的邮件组
 */
@Service
public class EmailService {

    @Value("${email.active-profile:dev}")
    private String activeProfile;
    
    @Autowired
    private Environment environment;

    /**
     * 获取当前环境的checker邮件组
     * @return 邮件组地址
     */
    public String getCheckerEmailGroup() {
        return getEmailGroupByProfile(activeProfile);
    }

    /**
     * 获取指定环境的邮件组
     * @param profile 环境名称
     * @return 邮件组地址
     */
    public String getEmailGroupByProfile(String profile) {
        return environment.getProperty("email.groups." + profile);
    }

    /**
     * 获取当前激活的环境
     * @return 环境名称
     */
    public String getActiveProfile() {
        return activeProfile;
    }
}
