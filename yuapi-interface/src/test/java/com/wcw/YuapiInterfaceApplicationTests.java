package com.wcw;

import com.wcw.wapiclientsdk.client.WapiClient;
import com.wcw.wapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.annotation.security.RunAs;

@SpringBootTest
class YuapiInterfaceApplicationTests {
	@Resource
	private WapiClient wapiClient;
	@Test
	public void contextLoads() {
		String result = wapiClient.getNameByGet("wcw");
		User user = new User();
		user.setName("wcwc");
		String usernameByPost = wapiClient.getNameByPost(user);
		System.out.println(result);
		System.out.println(usernameByPost);

	}
}
