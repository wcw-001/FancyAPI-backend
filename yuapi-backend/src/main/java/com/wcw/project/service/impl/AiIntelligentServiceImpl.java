package com.wcw.project.service.impl;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcw.project.common.BaseResponse;
import com.wcw.project.common.ErrorCode;
import com.wcw.project.common.ResultUtils;
import com.wcw.project.exception.BusinessException;
import com.wcw.project.manger.RedisLimiterManager;
import com.wcw.project.manger.SparkClient;
import com.wcw.project.mapper.AiIntelligentMapper;
import com.wcw.project.model.dto.ai.SparkMessage;
import com.wcw.project.model.dto.ai.SparkSyncChatResponse;
import com.wcw.project.model.dto.ai.request.SparkRequest;
import com.wcw.project.model.dto.ai.response.SparkTextUsage;
import com.wcw.project.service.AiIntelligentService;
import com.wcw.project.service.InterfaceInfoService;
import com.wcw.project.yuapicommon.model.entity.AiIntelligent;
import com.wcw.project.yuapicommon.model.entity.InterfaceInfo;
import com.wcw.project.yuapicommon.model.entity.UserInterfaceInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
* @author wcw
* @description 针对表【t_ai_intelligent】的数据库操作Service实现
* @createDate 2024-08-29 07:42:25
*/
@Service
public class AiIntelligentServiceImpl extends ServiceImpl<AiIntelligentMapper, AiIntelligent>
    implements AiIntelligentService {
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Resource
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;
    @Resource
    @Lazy
    private AiIntelligentService aiIntelligentService;
    /**
     * 客户端实例，线程安全
     */
    SparkClient sparkClient = new SparkClient();

    // todo 图书管理系统 1.2 版本设置认证信息 讯飞星火
    {
        sparkClient.appid = "bbedc838";
        sparkClient.apiKey = "175d1ccbdcfb13870ae299c37b9e8f26";
        sparkClient.apiSecret = "ZTA1YjQ4MjY2MzRkNDI3ZWI0Zjg2ZTk0";
    }

    @Override
    public BaseResponse<String> getGenResult(AiIntelligent aiIntelligent) {
        // 判断用户输入文本是否过长，超过128字，直接返回，防止资源耗尽
        String message = aiIntelligent.getInputMessage();
        if (StringUtils.isBlank(message)) {
            return ResultUtils.error(200, "文本不能为空");
        }
        if (message.length() > 128) {
            return ResultUtils.error(200, "文本字数过长");
        }
        Long userId = aiIntelligent.getUserId();
        // 查看用户接口次数是否足够，如果不够直接返回接口次数不够
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        queryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, 1);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            return ResultUtils.error(200, "该接口已废弃");
        }
        Integer leftNum = userInterfaceInfo.getLeftNum();
        Integer totalNum = userInterfaceInfo.getTotalNum();
        if (leftNum <= 0) {
            return ResultUtils.error(200, "AI接口次数不足，请明天再来");
        }
        // 限流
        //redisLimiterManager.doRateLimit("genResultByAi_" + user_id);
        // 调用自己的密钥
        // 创建开发请求，设置模型id和消息内容
        List<InterfaceInfo> list = interfaceInfoService.list();
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<String> hashSet = new HashSet<>();
        String presetInformation = "请根据数据库内容与接口描述作出推荐,API接口优先选择数据库里面有的，如果用户喜欢的API接口，数据库没有，你可能根据自身的知识去推荐，可以是一个也可以是多个接口，但不可以超过三接口，根据用户喜欢的信息作出推荐，输出内容为。如果用户问的问题与API接口推荐无关，请拒绝回答！";

        stringBuilder.append(presetInformation).append("\n").append("数据库内容: ");
        for (InterfaceInfo interfaceInfo : list) {
            if (!hashSet.contains(interfaceInfo.getName())) {
                hashSet.add(interfaceInfo.getName());
                stringBuilder.append(interfaceInfo.getName()).append(",");
                stringBuilder.append("\n").append("接口描述：").append(interfaceInfo.getDescription()).append("\n");
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("用户信息: ").append(message).append("\n");
//        list.forEach(System.out::println);
//        System.out.println(stringBuilder.toString());

        // 发送请求给AI，进行对话 由讯飞星火模型切换为阿里AI模型
        // 超时判断 利用ExecutorService
//
        // 调用之前先获取该用户最近的五条历史记录
        BaseResponse<List<AiIntelligent>> history = aiIntelligentService.getAiInformationByUserId(userId);
        List<AiIntelligent> historyData = history.getData();
        String response;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        if(historyData != null){
            historyData.forEach(item -> {
                messages.add(SparkMessage.userContent(item.getInputMessage()));
                messages.add(SparkMessage.assistantContent(item.getAiResult()));
            });
        }

        messages.add(SparkMessage.userContent(stringBuilder.toString()));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                .build();
        int timeout = 25; // 超时时间，单位为秒
        Future<String> future = executor.submit(() -> {
            try {
                // 同步调用
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
                SparkTextUsage textUsage = chatResponse.getTextUsage();
                stopWatch.stop();
                long total = stopWatch.getTotal(TimeUnit.SECONDS);
                System.out.println("本次接口调用耗时:" + total + "秒");
                System.out.println("\n回答：" + chatResponse.getContent());
                System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                        + "，回答tokens：" + textUsage.getCompletionTokens()
                        + "，总消耗tokens：" + textUsage.getTotalTokens());
                return chatResponse.getContent();
//                return AlibabaAIModel.doChatWithHistory(stringBuilder.toString(),recentHistory);
            } catch (Exception exception) {
                throw new RuntimeException("遇到异常");
            }
        });

        try {
            response = future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            return ResultUtils.error(5000, "服务器内部错误，请求超时，请稍后重试");
        }
//        // 关闭ExecutorService
        executor.shutdown();

        // 得到消息
//        System.out.println(response);
        AiIntelligent saveResult = new AiIntelligent();
        saveResult.setInputMessage(aiIntelligent.getInputMessage());
        saveResult.setAiResult(response);
        saveResult.setUserId(userId);
        boolean save = this.save(saveResult);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取AI推荐信息失败");
        }
        // 更新调用接口的次数 剩余接口调用次数-1.总共调用次数+1
        userInterfaceInfo.setLeftNum(leftNum - 1);
        userInterfaceInfo.setTotalNum(totalNum + 1);
        boolean update = userInterfaceInfoService.updateById(userInterfaceInfo);
        if (!update) {
            return ResultUtils.error(200, "调用接口信息失败");
        }
        return ResultUtils.success(response);
    }

    @Override
    public BaseResponse<List<AiIntelligent>> getAiInformationByUserId(Long userId) {
        QueryWrapper<AiIntelligent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.orderByDesc("createTime");
        queryWrapper.last("LIMIT 5");
        List<AiIntelligent> list = this.list(queryWrapper);
        if (list.size() == 0) {
            return ResultUtils.error(40000, "获取和AI最近的5条聊天记录失败");
        }
        Collections.reverse(list);
        return ResultUtils.success(list);

    }
}



