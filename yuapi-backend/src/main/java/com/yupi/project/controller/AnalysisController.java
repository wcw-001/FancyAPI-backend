package com.yupi.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yupi.project.annotation.AuthCheck;
import com.yupi.project.common.BaseResponse;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.common.ResultUtils;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import com.yupi.project.model.vo.InterfaceInfoVO;
import com.yupi.project.service.InterfaceInfoService;
import com.yupi.project.yuapicommon.model.entity.InterfaceInfo;
import org.springframework.beans.BeanUtils;
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
    UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Resource
    InterfaceInfoService interfaceInfoService;
    @GetMapping("/top/interfaceInfo/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(){
        List<InterfaceInfoVO> InterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        LinkedHashMap<Long, InterfaceInfoVO> voHashMap = new LinkedHashMap<>(InterfaceInfoList.size());
        for (InterfaceInfoVO userInterfaceInfoVO : InterfaceInfoList ){
            voHashMap.put(userInterfaceInfoVO.getId(),userInterfaceInfoVO);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",voHashMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();

        for (InterfaceInfo interfaceInfo : list){
            BeanUtils.copyProperties(interfaceInfo,voHashMap.get(interfaceInfo.getId()));
        }

//        List<InterfaceInfoVO> collect = list.stream().map(interfaceInfo -> {
//            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
//            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
//            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
//            interfaceInfoVO.setTotalNum(totalNum);
//            return interfaceInfoVO;
//        }).collect(Collectors.toList());
        List<InterfaceInfoVO> interfaceInfoVOS = new ArrayList<>(voHashMap.values());
        return ResultUtils.success(interfaceInfoVOS);
    }
}
