package com.wcw.project.controller;
import com.wcw.project.common.BaseResponse;
import com.wcw.project.common.ErrorCode;
import com.wcw.project.common.ResultUtils;
import com.wcw.project.exception.BusinessException;
import com.wcw.project.service.QiNiuService;
import com.wcw.project.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private QiNiuService ossService;
    @Resource
    private UserService userService;
    private static final String filePath=System.getProperty("user.dir")+"/files";
    @PostMapping("/upload")
    public BaseResponse<Map<String, Object>> upload(MultipartFile file, HttpServletRequest request) {
        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请上传文件");
        }
        Map<String, Object> result = new HashMap<>(2);

        // 上传到阿里云对象存储
        String fileUrl = ossService.uploadFileAvatar(file);

        result.put("url", fileUrl);
        return ResultUtils.success(result);
    }
}
