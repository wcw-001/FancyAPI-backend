package com.yupi.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.project.yuapicommon.model.entity.UserInterfaceInfo;


/**
* @author wcw
* @description 针对表【user_interface_info】的数据库操作Service
* @createDate 2024-03-17 10:58:46
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);


    /**
     * 调用接口id
     *
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
