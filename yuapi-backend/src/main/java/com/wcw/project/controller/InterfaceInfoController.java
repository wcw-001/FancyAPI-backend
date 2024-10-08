package com.wcw.project.controller;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcw.project.common.*;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.wcw.project.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.wcw.wapiclientsdk.client.WapiClient;
import com.wcw.wapiclientsdk.model.dto.BaseRequest;
import com.wcw.project.annotation.AuthCheck;
import com.wcw.project.constant.CommonConstant;
import com.wcw.project.exception.BusinessException;

import com.wcw.project.model.enums.InterfaceInfoEnum;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.service.InterfaceInfoService;
import com.wcw.project.service.UserService;
import com.wcw.project.util.ThrowUtils;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;
import com.wcw.project.yuapicommon.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 接口
 *
 * @author wcw
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.listInterfaceInfoByPage(interfaceInfoQueryRequest, request);
        return ResultUtils.success(interfaceInfoPage);
    }
    /**
     * 根据 当前用户ID 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @param request                   请求
     * @return 分页列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByUserIdPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                               HttpServletRequest request) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        interfaceInfoQueryRequest.setSortField("createTime");
        // 倒序排序
        interfaceInfoQueryRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        // 限制爬虫
        ThrowUtils.throwIf(size > 30, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOByUserIdPage(interfaceInfoPage, request));
    }

    // endregion
    /**
     * 上线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
//    @PostMapping("/online")
//    @AuthCheck(mustRole = "admin")
//    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
//                                                     HttpServletRequest request) {
//        if (idRequest == null || idRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long id = idRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        //判断该接口是否可以调用
//        com.wcw.wapiclientsdk.model.User user = new com.wcw.wapiclientsdk.model.User();
//        user.setName("test");
//        String username = wapiClient.getNameByPost(user);
//        if(StringUtils.isBlank(username)){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口验证失败");
//        }
//        //仅管理员或者本人可以修改
//        InterfaceInfo interfaceInfo = new InterfaceInfo();
//        interfaceInfo.setId(id);
//        interfaceInfo.setStatus(InterfaceInfoEnum.FEMALE.getValue());
//        boolean result = interfaceInfoService.updateById(interfaceInfo);
//        return ResultUtils.success(result);
//    }
    /**
     * 上线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        Boolean result = interfaceInfoService.onlineInterfaceInfo(idRequest, request);
        return ResultUtils.success(result);
    }
    /**
     * 下线接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        Boolean result = interfaceInfoService.offlineInterfaceInfo(idRequest, request);
        return ResultUtils.success(result);
    }
    /**
     * 调用接口
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
//    @PostMapping("/invoke")
//    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
//                                                      HttpServletRequest request) {
//        if(interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long id = interfaceInfoInvokeRequest.getId();
//        String params = interfaceInfoInvokeRequest.getUserRequestParams();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        if(oldInterfaceInfo.getStatus() != InterfaceInfoEnum.FEMALE.getValue()){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已关闭");
//        }
//        User loginUser = userService.getLoginUser(request);
//        String accessKey = loginUser.getAccessKey();
//        String secretKey = loginUser.getSecretKey();
//        WapiClient wapiClient = new WapiClient(accessKey,secretKey);
//        Gson gson = new Gson();
//        com.wcw.wapiclientsdk.model.User user = gson.fromJson(params, com.wcw.wapiclientsdk.model.User.class);
//        String userNameByPost = wapiClient.getNameByPost(user);
//        return ResultUtils.success(userNameByPost);
//    }

    /**
     * 调用接口
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                                 HttpServletRequest request) {
        Object result = interfaceInfoService.invokeInterfaceInfo(interfaceInfoInvokeRequest, request);
        log.info("调用api接口返回结果：" + result);
        // 重构用户缓存
        return ResultUtils.success(result);
    }

}
