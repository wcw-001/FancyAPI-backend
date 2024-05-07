package com.yupi.project.yuapicommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.project.yuapicommon.model.entity.User;

import javax.servlet.http.HttpServletRequest;


/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService {
    /**
     * 数据库中查是否已分配给用户密钥（accessKey、secretKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
