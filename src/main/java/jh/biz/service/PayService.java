package jh.biz.service;

import jh.model.po.AdminAccount;
import jh.model.po.AdminBankCard;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayService {
    void saveOprLog(PayRequest payRequest);
    void remoteSuccess(PayRequest payRequest,PayMsgRecord hfResultMsg);
    void payFailed(String outTradeNo,PayMsgRecord hfResultMsg);
    void paySuccess(String outTradeNo);
    void payFailed(String outTradeNo);
    void payPromote(String outTradeNo);
    void savePayMsg(List<PayMsgRecord> records);
    Map<Long,BigDecimal> chooseAdminBank(Long groupId, BigDecimal amount);
    AdminBankCard chooseAdminBank1(Long groupId, BigDecimal amount);
    void savePayRequest(List<PayMsgRecord> msgRecords,PayRequest payRequest);
    void updateDailyLimit();
    void savePayMsg(PayMsgRecord payMsgRecord);

}
