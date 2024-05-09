package com.wcw.wapiclientsdk;

import com.wcw.wapiclientsdk.client.WapiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("wapi.client")
@Data
@ComponentScan
public class WapiClientConfig {
    private String accessKey;
    private String secretKey;
    @Bean
    public WapiClient wapiClient(){
        return new WapiClient(accessKey,secretKey);
    }
}
