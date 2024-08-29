package com.wcw.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcw.project.model.vo.InterfaceInfoVO;
import com.wcw.project.yuapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author wcw
* @description 针对表【user_interface_info】的数据库操作Mapper
* @createDate 2024-03-17 10:58:46
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<InterfaceInfoVO> listTopInvokeInterfaceInfo(int limit);
}




