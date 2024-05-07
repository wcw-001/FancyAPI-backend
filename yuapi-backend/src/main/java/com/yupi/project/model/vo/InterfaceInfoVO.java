package com.yupi.project.model.vo;

import com.yupi.project.yuapicommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoVO extends InterfaceInfo {
    private static final long serialVersionUID = -7757097411934084089L;
    private int totalNum;
}
