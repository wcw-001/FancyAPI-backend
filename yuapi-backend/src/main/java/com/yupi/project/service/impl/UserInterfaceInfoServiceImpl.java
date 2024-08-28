package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.manger.RedisLimiterManager;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import com.yupi.project.service.UserInterfaceInfoService;
import com.yupi.project.yuapicommon.model.entity.UserInterfaceInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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

}




