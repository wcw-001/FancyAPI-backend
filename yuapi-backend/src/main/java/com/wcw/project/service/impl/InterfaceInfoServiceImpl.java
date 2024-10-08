package com.wcw.project.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcw.project.common.IdRequest;
import com.wcw.project.mapper.InterfaceInfoMapper;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.wcw.project.model.enums.InterfaceInfoEnum;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.service.UserService;
import com.wcw.project.common.ErrorCode;
import com.wcw.project.constant.CommonConstant;
import com.wcw.project.exception.BusinessException;
import com.wcw.project.service.InterfaceInfoService;
import com.wcw.project.service.UserInterfaceInfoService;
import com.wcw.project.util.SqlUtils;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;
import com.wcw.project.yuapicommon.model.entity.User;
import com.wcw.project.yuapicommon.model.entity.UserInterfaceInfo;
import com.wcw.wapiclientsdk.client.WapiClient;
import com.wcw.wapiclientsdk.model.dto.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {
    @Resource
    UserService userService;
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private WapiClient wapiClient;
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }

        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String method = interfaceInfoQueryRequest.getMethod();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String searchText = interfaceInfoQueryRequest.getSearchText();
        Date createTime = interfaceInfoQueryRequest.getCreateTime();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        Long id = interfaceInfoQueryRequest.getId();
        Long userId = interfaceInfoQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("name", searchText).or().like("description", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(method), "method", method);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.gt(ObjectUtils.isNotEmpty(createTime), "createTime", createTime);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOByUserIdPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(interfaceInfoList)) {
            return interfaceInfoVOPage;
        }
        // 传入当前用户ID
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        // 过滤掉不是当前用户的接口，并且填充信息
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream()
                .map(interfaceInfo -> {
                    InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);
                    UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.lambdaQuery()
                            .eq(UserInterfaceInfo::getUserId, userId)
                            .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfo.getId())
                            .one();
                    if (userInterfaceInfo != null) {
                        interfaceInfoVO.setTotalNum(userInterfaceInfo.getTotalNum());
                        interfaceInfoVO.setLeftNum(userInterfaceInfo.getLeftNum());
                        return interfaceInfoVO;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return interfaceInfoVOPage;
    }

    @Override
    public Page<InterfaceInfo> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = this.page(new Page<>(current, size), queryWrapper);
        return interfaceInfoPage;
    }

    @Override
    public Object invokeInterfaceInfo(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if(interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = this.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(oldInterfaceInfo.getStatus() != InterfaceInfoEnum.FEMALE.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已关闭");
        }
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        WapiClient wapiClient = new WapiClient(accessKey,secretKey);
        URL url = null;
        try {
            url = new URL(oldInterfaceInfo.getUrl());
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无效的地址转换");
        }
        String params = interfaceInfoInvokeRequest.getUserRequestParams();
        String path = url.getPath();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setPath(path);
        baseRequest.setMethod(oldInterfaceInfo.getMethod());
        baseRequest.setRequestParams(params);
        baseRequest.setUserRequest(request);
        Object result = null;
        try {
            // 调用sdk解析地址方法
            result = wapiClient.parseAddressAndCallInterface(baseRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ObjUtil.isEmpty(request)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求SDK失败");
        }
        return result;
    }

    @Override
    public Boolean offlineInterfaceInfo(IdRequest idRequest, HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = this.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //仅管理员或者本人可以修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.MALE.getValue());
        boolean result = this.updateById(interfaceInfo);
        return result;
    }

    @Override
    public Boolean onlineInterfaceInfo(IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = this.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断该接口是否可以调用
        URL url = null;
        try {
            url = new URL(oldInterfaceInfo.getUrl());
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无效的地址转换");
        }
        String params = null;
        String path = url.getPath();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setPath(path);
        baseRequest.setMethod(oldInterfaceInfo.getMethod());
        baseRequest.setRequestParams(params);
        baseRequest.setUserRequest(request);
        Object clientResult = null;
        try {
            // 调用sdk解析地址方法
            clientResult = wapiClient.parseAddressAndCallInterface(baseRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ObjUtil.isEmpty(request)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求SDK失败");
        }
        log.info("调用api接口返回结果：" + clientResult);
        //仅管理员或者本人可以修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.FEMALE.getValue());
        boolean result = this.updateById(interfaceInfo);
        return result;
    }

}




