package com.wcw.wapiclientsdk.model.dto;

import javax.servlet.http.HttpServletRequest;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BaseRequest {

    private String path;

    private String method;

    private String requestParams;

    private HttpServletRequest userRequest;
}
