package jh.biz;

import jh.model.po.SettleTask;

public interface SettleBiz {
    void saveSettle(SettleTask settleTask);
    void finishSettle(Long id);
}
