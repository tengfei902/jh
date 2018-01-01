package jh.dao.remote;

import com.google.gson.Gson;
import hf.base.client.BaseClient;
import hf.base.model.RemoteParams;
import jh.biz.impl.AbstractPayBiz;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CallBackClient extends BaseClient {
    protected Logger logger = LoggerFactory.getLogger(CallBackClient.class);

    public boolean post(String url,Map<String,Object> params) {
        RemoteParams remoteParams = new RemoteParams(url).withParams(params);
        String result = super.post(remoteParams);
        logger.info(String.format("remote call back result:%s,%s",result,new Gson().toJson(params)));
        return StringUtils.equalsIgnoreCase(result,"SUCCESS");
    }
}
