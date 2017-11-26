package jh.biz;

import hf.base.model.TradeRequest;
import hf.base.model.TradeRequestDto;
import hf.base.utils.Pagenation;

public interface TrdBiz {

    Pagenation<TradeRequestDto> getTradeList(TradeRequest request);
}
