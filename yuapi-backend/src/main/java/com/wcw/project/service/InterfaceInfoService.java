package com.wcw.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.project.common.IdRequest;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *接口
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    Page<InterfaceInfoVO> getInterfaceInfoVOByUserIdPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request);
    Page<InterfaceInfo> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request);
    Object invokeInterfaceInfo(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request);
    Boolean offlineInterfaceInfo(IdRequest idRequest,HttpServletRequest request);

    Boolean onlineInterfaceInfo(IdRequest idRequest, HttpServletRequest request);
}
