package jh.biz.trade.impl;

import jh.biz.adapter.Adapter;
import jh.biz.adapter.impl.YsPayRequestAdapter;
import jh.biz.adapter.impl.YsPayResponseAdapter;
import jh.dao.remote.PayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class YsTradeBiz extends AbstractTradeBiz {
    @Autowired
    private YsPayRequestAdapter ysPayRequestAdapter;
    @Autowired
    private YsPayResponseAdapter ysPayResponseAdapter;
    @Autowired
    @Qualifier("ysClient")
    private PayClient payClient;

    @Override
    Adapter getRequestAdapter() {
        return ysPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return ysPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return payClient;
    }
}
