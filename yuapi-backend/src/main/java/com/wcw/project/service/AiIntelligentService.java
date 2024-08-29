package com.wcw.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.project.common.BaseResponse;
import com.wcw.project.yuapicommon.model.entity.AiIntelligent;

import java.util.List;

/**
* @author wcw
* @description 针对表【t_ai_intelligent】的数据库操作Service
* @createDate 2024-08-29 07:42:25
*/
public interface AiIntelligentService extends IService<AiIntelligent> {
    /**
     * 调用AI接口，获取推荐的图书信息字符串
     *
     * @param aiIntelligent
     * @return
     */
    BaseResponse<String> getGenResult(AiIntelligent aiIntelligent);

    /**
     * 根据用户ID 获取该用户和AI聊天的最近的五条消息
     * @param userId
     * @return
     */
    BaseResponse<List<AiIntelligent>> getAiInformationByUserId(Long userId);
}
