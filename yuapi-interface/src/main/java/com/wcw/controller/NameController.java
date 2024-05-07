package com.wcw.controller;

import com.wcw.wapiclientsdk.model.User;
import com.wcw.wapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.springframework.boot.system.SystemProperties.get;

/**
 * 名称api
 * @author wcw
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/get")
    public String getNameByGet(String name,HttpServletRequest request){
        System.out.println(request.getHeader("wcwc"));
        return "GET 你的名字是"+name;
    }
    @PostMapping("/user")
    public String getNameByPost(@RequestBody User user, HttpServletRequest request){

        String result = "POST 用户名是"+user.getName();
        return result;
    }
    @PostMapping("/post")
    public String getUsernameByPost(@RequestParam String name){
        return "POST 你的名字是"+name;
    }
    @GetMapping("/joke")
    public String getJoke(){
       return get("https://api.vvhan.com/api/text/joke");
    }
}
