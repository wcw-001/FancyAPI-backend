package com.wcw.project.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wcw.project.common.BaseResponse;
import com.wcw.project.common.ErrorCode;
import com.wcw.project.common.ResultUtils;
import com.wcw.project.mapper.UserInterfaceInfoMapper;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.service.InterfaceInfoService;
import com.wcw.project.annotation.AuthCheck;
import com.wcw.project.exception.BusinessException;
import com.wcw.project.service.UserInterfaceInfoService;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;
    @GetMapping("/top/interfaceInfo/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(){
        List<InterfaceInfoVO> interfaceInfoVOS = userInterfaceInfoService.listTopInvokeInterfaceInfo();
        return ResultUtils.success(interfaceInfoVOS);
    }
}
