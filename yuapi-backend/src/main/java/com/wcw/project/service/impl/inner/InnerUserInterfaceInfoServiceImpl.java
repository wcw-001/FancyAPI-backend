package com.wcw.project.service.impl.inner;

import com.wcw.project.service.UserInterfaceInfoService;
import com.wcw.project.yuapicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId){
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }
}
