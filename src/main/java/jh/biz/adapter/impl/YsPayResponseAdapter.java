package jh.biz.adapter.impl;

import jh.biz.adapter.Adapter;
import jh.model.dto.trade.unifiedorder.HfPayResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class YsPayResponseAdapter implements Adapter<HfPayResponse> {

    @Override
    public HfPayResponse adpat(Map<String, Object> request) {
        return null;
    }
}
