package com.wcw.project.yuapicommon.service;

import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;

/**
 * @Entity com.yupi.project.model.entity.InterfaceInfo
 */
public interface InnerInterfaceInfoMapper {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求参数、请求方法）
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

}




