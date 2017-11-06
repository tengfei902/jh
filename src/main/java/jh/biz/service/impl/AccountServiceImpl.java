package jh.biz.service.impl;

import jh.biz.service.AccountService;
import jh.dao.local.*;
import jh.exceptions.BizException;
import jh.model.*;
import jh.model.dto.RefundResponse;
import jh.model.dto.ReverseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private PayProofDao payProofDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private PayRefundOrderDao payRefundOrderDao;
    @Autowired
    private PayReverseOrderDao payReverseOrderDao;

    @Transactional
    @Override
    public void pay(PayTrdOrder payTrdOrder) {
//        AccountOprLog oprLog = accountOprLogDao.selectByTrdOrderId(payTrdOrder.getId(),AccountOprLog.OPR_TYPE.UNIFIED);
//
//        PayProof payProof = payProofDao.selectByTrdNo(payTrdOrder.getOutTradeNo());
//        UserInfo userInfo = userInfoDao.selectByMerchantNo(payTrdOrder.getMerchantNo());
//        Account account = accountDao.selectByUserId(userInfo.getId());
//        int count = accountDao.addAmount(account.getId(),new BigDecimal(payProof.getActualTotal()),account.getVersion());
//        if(count<=0) {
//            throw new BizException("更新账户失败");
//        }
//
//        count = accountOprLogDao.updateStatusById(oprLog.getId(),AccountOprLog.STATUS.INIT,AccountOprLog.STATUS.SUCCESS);
//
//        if(count<=0) {
//            throw new BizException("更新账户日志失败");
//        }
    }

    @Transactional
    @Override
    public void refund(RefundResponse refundResponse) {
//        PayRefundOrder payRefundOrder = payRefundOrderDao.selectByNo(refundResponse.getNo());
//        AccountOprLog oprLog = accountOprLogDao.selectByTrdOrderId(payRefundOrder.getId(),AccountOprLog.OPR_TYPE.REFUND);
//
//        UserInfo userInfo = userInfoDao.selectByMerchantNo(payRefundOrder.getMerchantNo());
//        Account account = accountDao.selectByUserId(userInfo.getId());
//        int count = accountDao.addAmount(account.getId(),oprLog.getPrice().negate(),account.getVersion());
//        if(count <= 0) {
//            throw new BizException("更新账户失败");
//        }
//
//        count = accountOprLogDao.updateStatusById(oprLog.getId(),AccountOprLog.STATUS.INIT,AccountOprLog.STATUS.SUCCESS);
//        if(count<=0) {
//            throw new BizException("更新明细状态失败");
//        }
    }

    @Transactional
    @Override
    public void reverse(ReverseResponse reverseResponse) {
//        PayReverseOrder payReverseOrder = payReverseOrderDao.selectByTrdOrder(reverseResponse.getOut_trade_no());
//        AccountOprLog oprLog = accountOprLogDao.selectByTrdOrderId(payReverseOrder.getId(),AccountOprLog.OPR_TYPE.REVERSE);
//
//        UserInfo userInfo = userInfoDao.selectByMerchantNo(payReverseOrder.getMerchantNo());
//        Account account = accountDao.selectByUserId(userInfo.getId());
//        int count = accountDao.addAmount(account.getId(),oprLog.getPrice().negate(),account.getVersion());
//        if(count <= 0) {
//            throw new BizException("更新账户失败");
//        }
//
//        count = accountOprLogDao.updateStatusById(oprLog.getId(),AccountOprLog.STATUS.INIT,AccountOprLog.STATUS.SUCCESS);
//        if(count<=0) {
//            throw new BizException("更新明细状态失败");
//        }
    }
}
