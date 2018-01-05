package jh.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

    @Value("#{commonConfig[callbackurl]}")
    private String callbackUrl;

    public String getCallbackUrl() {
        return callbackUrl;
    }
}
