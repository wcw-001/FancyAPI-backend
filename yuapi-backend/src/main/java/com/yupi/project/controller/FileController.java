package com.yupi.project.controller;
import com.yupi.project.common.BaseResponse;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.common.ResultUtils;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.service.QiNiuService;
import com.yupi.project.service.UserService;
import com.yupi.project.yuapicommon.model.entity.User;
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
