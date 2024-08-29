package com.wcw.project.yuapicommon.service;


import com.wcw.project.yuapicommon.model.entity.User;


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
