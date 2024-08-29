package com.wcw.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;

import javax.servlet.http.HttpServletRequest;

/**
 *接口
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    Page<InterfaceInfoVO> getInterfaceInfoVOByUserIdPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request);
}
