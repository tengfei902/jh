package jh.biz.trade.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.enums.OperateType;
import hf.base.enums.TradeType;
import jh.biz.adapter.Adapter;
import jh.biz.adapter.impl.FxtPayRequestAdapter;
import jh.biz.adapter.impl.FxtPayResponseAdapter;
import jh.dao.local.PayMsgRecordDao;
import jh.dao.remote.PayClient;
import jh.model.po.PayMsgRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FxtTradeBiz extends AbstractTradeBiz {
    @Autowired
    @Qualifier("fxtClient")
    private PayClient payClient;
    @Autowired
    private FxtPayRequestAdapter fxtPayRequestAdapter;
    @Autowired
    private FxtPayResponseAdapter fxtPayResponseAdapter;

    @Override
    Adapter getRequestAdapter() {
        return fxtPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return fxtPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return payClient;
    }
}
