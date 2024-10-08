package com.wcw.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcw.project.common.ErrorCode;
import com.wcw.project.exception.BusinessException;
import com.wcw.project.manger.RedisLimiterManager;
import com.wcw.project.mapper.UserInterfaceInfoMapper;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.service.InterfaceInfoService;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;
import com.wcw.project.yuapicommon.model.entity.UserInterfaceInfo;
import com.wcw.project.service.UserInterfaceInfoService;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
* @author wcw
* @description 针对表【user_interface_info】的数据库操作Service实现
* @createDate 2024-03-17 10:58:46
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {
    @Resource
    RedissonClient redissonClient;
    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0 ) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口剩余次数不能小于0");
        }
    }
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if(interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserInterfaceInfo> invokeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        invokeLambdaQueryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        invokeLambdaQueryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(invokeLambdaQueryWrapper);
        redisLimiterManager.doRateLimit("invokeUser_"+userId);
        //只有一个线程能获取锁
        //RLock lock = redissonClient.getLock("yupao:join_team");
            //抢到锁并执行
        //try {
        //    while (true) {
        //        if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    boolean invokeResult;
                    if (userInterfaceInfo == null) {
                        userInterfaceInfo = new UserInterfaceInfo();
                        userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
                        userInterfaceInfo.setUserId(userId);
                        userInterfaceInfo.setTotalNum(1);
                        userInterfaceInfo.setLeftNum(99);
                        invokeResult = this.save(userInterfaceInfo);
                    }else {
                        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper();
                        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
                        updateWrapper.eq("userId", userId);
                        updateWrapper.gt("leftNum", 0);
                        updateWrapper.setSql("leftNum = leftNum-1,totalNum = totalNum+1");
                        invokeResult = this.update(updateWrapper);
                    }
                    if (!invokeResult) {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用失败");
                    }
                    return true;
                }
            //}
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public List<InterfaceInfoVO> listTopInvokeInterfaceInfo(){
        List<InterfaceInfoVO> interfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        if(CollectionUtils.isEmpty(interfaceInfoList)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        LinkedHashMap<Long, InterfaceInfoVO> voHashMap = new LinkedHashMap<>(interfaceInfoList.size());
        for (InterfaceInfoVO userInterfaceInfoVO : interfaceInfoList ){
            voHashMap.put(userInterfaceInfoVO.getId(),userInterfaceInfoVO);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",voHashMap.keySet());
        queryWrapper.eq("status",1);
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        InterfaceInfoVO interfaceVO = new InterfaceInfoVO();
        List<InterfaceInfoVO> interfaceInfoVOS = new ArrayList<>();
        voHashMap.values().forEach(interfaceInfoVO -> {
            for(InterfaceInfo interfaceInfo : list){
                if(interfaceInfo.getId().equals(interfaceInfoVO.getId())){
                    InterfaceInfoVO interfaceInfoResult = BeanUtil.copyProperties(interfaceInfo, InterfaceInfoVO.class);
                    interfaceInfoResult.setTotalNum(interfaceInfoVO.getTotalNum());
                    interfaceInfoVOS.add(interfaceInfoResult);
                }
            }
        });
        return interfaceInfoVOS;
    }

}




