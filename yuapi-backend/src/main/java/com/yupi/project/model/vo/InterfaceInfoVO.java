package com.yupi.project.model.vo;

import com.yupi.project.yuapicommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoVO extends InterfaceInfo {
    private static final long serialVersionUID = -7757097411934084089L;
    private int totalNum;
    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
    /**
     * 主机名
     */
    private String host;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;
    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVO user;


    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 是否被当前用户所拥有
     */
    private Boolean isOwnerByCurrentUser;

    /**
     * 包装类转对象
     *
     * @param interfaceInfoVO
     * @return
     */
    public static InterfaceInfo voToObj(InterfaceInfoVO interfaceInfoVO) {
        if (interfaceInfoVO == null) {
            return null;
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoVO, interfaceInfo);
        return interfaceInfo;
    }

    /**
     * 对象转包装类
     *
     * @param interfaceInfo
     * @return
     */
    public static InterfaceInfoVO objToVo(InterfaceInfo interfaceInfo) {
        if (interfaceInfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
        return interfaceInfoVO;
    }
}
