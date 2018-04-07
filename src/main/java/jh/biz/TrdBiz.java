package jh.biz;

import hf.base.model.TradeRequest;
import hf.base.model.TradeRequestDto;
import hf.base.utils.Pagenation;

import java.util.Map;

public interface TrdBiz {

    Pagenation<TradeRequestDto> getTradeList(TradeRequest request);

    Map<String,Object> orderInfo(String outTradeNo);
}
