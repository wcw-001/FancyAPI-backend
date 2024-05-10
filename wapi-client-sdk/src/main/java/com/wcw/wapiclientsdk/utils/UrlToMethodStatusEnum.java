package com.wcw.wapiclientsdk.utils;

import java.lang.reflect.Method;

public enum UrlToMethodStatusEnum {
    NAME("/api/name/user","getNameByPost"),
    JOKE("/api/name/joke","getJoke");

    private String path;
    private String method;
    public static UrlToMethodStatusEnum getEnumByValue(String value){
        if (value == null){
            return null;
        }
        UrlToMethodStatusEnum[] values = UrlToMethodStatusEnum.values();
        for(UrlToMethodStatusEnum urlToMethodStatusEnum : values){
            if(urlToMethodStatusEnum.getPath().equals(value)){
                return urlToMethodStatusEnum;
            }
        }
        return null;
    }
    UrlToMethodStatusEnum(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
