package com.wcw.wapiclientsdk.model.params;

import lombok.Data;

import java.io.Serializable;

/**
 * 星座运势接收参数
 * @author PYW
 */
@Data
public class JokeParams implements Serializable {
    private String text;
    private static final long serialVersionUID = 1L;
}
