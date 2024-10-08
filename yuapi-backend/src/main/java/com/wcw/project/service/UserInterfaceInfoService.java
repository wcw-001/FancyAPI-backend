package com.wcw.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.yuapicommon.model.entity.UserInterfaceInfo;

import java.util.List;


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

    List<InterfaceInfoVO> listTopInvokeInterfaceInfo();
}
