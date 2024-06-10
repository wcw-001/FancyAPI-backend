package com.wcw.wapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.wcw.wapiclientsdk.model.User;
import com.wcw.wapiclientsdk.model.dto.BaseRequest;
import com.wcw.wapiclientsdk.utils.SignUtils;
import com.wcw.wapiclientsdk.utils.UrlToMethodStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口的客户端
 *
 * @author wcw
 */
@Slf4j
public class WapiClient {
    private static final  String GATEWAY_HOST = "http://localhost:8090";
    private String accessKey;
    private String secretKey;
    private static final String EXTRA_BODY = "userInfoFancyAPI";

    public WapiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.get(GATEWAY_HOST+"/api/name/", paramMap);
        System.out.println(result);
        return result;
    }
    private Map<String,String> getHeaderMap( String body){
        Map<String,String> map = new HashMap<>();
        map.put("accessKey",accessKey);
        //一定不能传输，避免拦截
        //map.put("secretKey",secretKey);
        map.put("none", RandomUtil.randomNumbers(4));
        map.put("body", body);
        map.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        map.put("sign", SignUtils.genSign(body,secretKey));
        return map;
    }

    public String getNameByPost(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .body(json)
                .addHeaders(getHeaderMap(json))
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
    public String getNameByPost(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.post(GATEWAY_HOST+"/api/name/", paramMap);
        System.out.println(result);
        return result;
    }
    public String getJoke(){
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/api/name/joke")
                .addHeaders(getHeaderMap(EXTRA_BODY))
                .body(EXTRA_BODY)
                .execute();
        return httpResponse.body();
    }
    public Object parseAddressAndCallInterface(BaseRequest baseRequest) throws Exception {
        String params = baseRequest.getRequestParams();

        String path = baseRequest.getPath();
        String method = baseRequest.getMethod();
        switch (UrlToMethodStatusEnum.getEnumByValue(path)) {
            case NAME:
                Gson gson = new Gson();
                User requestParams = gson.fromJson(params, User.class);
                return invokeMethod(UrlToMethodStatusEnum.NAME.getMethod(), requestParams, User.class);
            case JOKE:
                return invokeMethod(UrlToMethodStatusEnum.JOKE.getMethod());
            default:
                return null;
        }

    }

//    /**
//     * 解析地址和调用接口
//     *
//     * @param baseRequest
//     * @return
//     */
//    public Object parseAddressAndCallInterface(BaseRequest baseRequest) {
//        String path = baseRequest.getPath();
//        String method = baseRequest.getMethod();
//        Map<String, Object> paramsList = baseRequest.getRequestParams();
//        HttpServletRequest userRequest = baseRequest.getUserRequest();
//        Class<?> clazz = WapiClient.class;
//        Object result = null;
//        try {
//            log.info("请求地址：{}，请求方法：{}，请求参数：{}", path, method, paramsList);
//            // 获取名称
//            if (path.equals(UrlToMethodStatusEnum.name.getPath())) {
//                return invokeMethod(UrlToMethodStatusEnum.name.getMethod(), paramsList, User.class);
//            }
//            // 获取星座运势
//            if (path.equals(UrlToMethodStatusEnum.Joke.getPath())) {
//                return invokeMethod(UrlToMethodStatusEnum.Joke.getMethod());
//            }
//
//            // todo 1.添加新的接口地址判断
//        } catch (Exception e) {
//            return null;
//        }
//        if (ObjUtil.isEmpty(result)) {
//            return null;
//        }
//        log.info("返回结果：{}", result);
//        return result;
//    }
    /**
     * 反射方法(不带参数)
     *
     * @param methodName
     * @return
     */
    private Object invokeMethod(String methodName) throws Exception {
        return this.invokeMethod(methodName, null, null);
    }
    /**
     * 反射方法(带参)
     *
     * @param methodName
     * @param params
     * @param paramsType
     * @return
     */
    private <T> Object invokeMethod(String methodName, T params, Class<?> paramsType) throws Exception {
        try {
            Class<?> clazz = WapiClient.class;
            if (params == null) {
                Method method = clazz.getMethod(methodName);
                return method.invoke(this);
            } else {
                log.info("接收到的参数 params:{} paramsType:{}", params, paramsType);
                Method method = clazz.getMethod(methodName, paramsType);
                // map转Object
                //Object paramsObject = BeanUtil.mapToBean(params, paramsType, true, CopyOptions.create());
                log.info("map转Object params:{} paramsType:{}", params, paramsType);
                return method.invoke(this, params);
            }
        } catch (NoSuchMethodException e){
            throw new Exception("接口不存在");
        } catch (Exception e) {
            // 处理异常
            throw new Exception("接口调用异常");
        }
    }

}
