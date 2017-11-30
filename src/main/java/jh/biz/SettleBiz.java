package jh.biz;

import hf.base.model.WithDrawInfo;
import hf.base.model.WithDrawRequest;
import hf.base.utils.Pagenation;
import jh.model.po.SettleTask;

public interface SettleBiz {
    void saveSettle(SettleTask settleTask);
    void finishSettle(Long id);
    Pagenation<WithDrawInfo> getWithDrawPage(WithDrawRequest withDrawRequest);
}
