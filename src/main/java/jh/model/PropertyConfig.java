package jh.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

    @Value("#{commonConfig[callbackurl]}")
    private String callbackUrl;
    @Value("#{commonConfig[wwPayCallbackUrl]}")
    private String wwCallbackUrl;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getWwCallbackUrl() {
        return wwCallbackUrl;
    }
}
