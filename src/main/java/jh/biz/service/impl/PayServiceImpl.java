package jh.biz.service.impl;

import jh.biz.service.AccountService;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.exceptions.BizException;
import jh.model.dto.*;
import jh.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private PayProofDao payProofDao;
    @Autowired
    private PayTrdOrderDao payTrdOrderDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PayRefundOrderDao payRefundOrderDao;
    @Autowired
    private PayReverseOrderDao payReverseOrderDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;

    @Transactional
    @Override
    public String savePayInfo(PayRequest request) {
        PayTrdOrder payTrdOrder = new PayTrdOrder(request);
        payTrdOrderDao.insertSelective(payTrdOrder);

        PayProof proof = new PayProof();
        proof.setMerchantNo(payTrdOrder.getMerchantNo());
        proof.setOutletNo(payTrdOrder.getOutletNo());
        proof.setOutTradeNo(payTrdOrder.getOutTradeNo());
        proof.setTotal(payTrdOrder.getTotal());

        payProofDao.insertSelective(proof);

        UserInfo userInfo = userInfoDao.selectByMerchantNo(payTrdOrder.getMerchantNo());
        Account account = accountDao.selectByUserId(userInfo.getId());

        AccountOprLog accountOprLog = new AccountOprLog();
        accountOprLog.setAccountId(account.getId());
        accountOprLog.setOutTradeNo(payTrdOrder.getOutTradeNo());
//        accountOprLog.setOprType(AccountOprLog.OPR_TYPE.UNIFIED);
        //total与actualTotal是否一致
        accountOprLog.setPrice(new BigDecimal(payTrdOrder.getTotal()));
        accountOprLog.setRemark(payTrdOrder.getRemark());
        accountOprLog.setTrdOrderId(payTrdOrder.getId());
        accountOprLog.setUserId(userInfo.getId());

        accountOprLogDao.insertSelective(accountOprLog);

        return payTrdOrder.getOutTradeNo();
    }

    @Transactional
    @Override
    public String saveRefund(RefundRequest refundRequest) {
        PayRefundOrder payRefundOrder = new PayRefundOrder(refundRequest);
        payRefundOrderDao.insertSelective(payRefundOrder);

        UserInfo userInfo = userInfoDao.selectByMerchantNo(payRefundOrder.getMerchantNo());
        Account account = accountDao.selectByUserId(userInfo.getId());

        AccountOprLog accountOprLog = new AccountOprLog();
        accountOprLog.setAccountId(account.getId());
        accountOprLog.setOutTradeNo(payRefundOrder.getOriNo());
//        accountOprLog.setOprType(AccountOprLog.OPR_TYPE.REFUND);
        //total与actualTotal是否一致
        accountOprLog.setPrice(new BigDecimal(refundRequest.getRefund_fee()));
        accountOprLog.setRemark(payRefundOrder.getMessage());
        accountOprLog.setTrdOrderId(payRefundOrder.getId());
        accountOprLog.setUserId(userInfo.getId());

        accountOprLogDao.insertSelective(accountOprLog);
        return payRefundOrder.getRefundNo();
    }

    @Transactional
    @Override
    public void saveReverse(ReverseRequest reverseRequest) {
        PayReverseOrder payReverseOrder = new PayReverseOrder(reverseRequest);
        payReverseOrderDao.insertSelective(payReverseOrder);

        UserInfo userInfo = userInfoDao.selectByMerchantNo(reverseRequest.getMerchant_no());
        Account account = accountDao.selectByUserId(userInfo.getId());
        PayTrdOrder payTrdOrder = payTrdOrderDao.selectByOrderNo(reverseRequest.getOut_trade_no());

        AccountOprLog accountOprLog = new AccountOprLog();
        accountOprLog.setAccountId(account.getId());
        accountOprLog.setOutTradeNo(reverseRequest.getOut_trade_no());
//        accountOprLog.setOprType(AccountOprLog.OPR_TYPE.REVERSE);
        //total与actualTotal是否一致
        accountOprLog.setPrice(new BigDecimal(payTrdOrder.getTotal()));
        accountOprLog.setRemark("交易撤销");
        accountOprLog.setTrdOrderId(payTrdOrder.getId());
        accountOprLog.setUserId(userInfo.getId());

        accountOprLogDao.insertSelective(accountOprLog);
    }

    @Transactional
    @Override
    public void pay(PayResponse response) {
        String trdNo = response.getOut_trade_no();
        PayProof payProof = payProofDao.selectByTrdNo(trdNo);
        Map<String,Object> params = response.getParams();
        params.put("id",payProof.getId());
        params.put("fromStatus",10);
        params.put("targetStatus",response.getErrcode());
        int count = payProofDao.update(params);
        if(count <=0 ) {
            throw new BizException("更新交易单状态失败");
        }

        PayTrdOrder payTrdOrder = payTrdOrderDao.selectByOrderNo(response.getOut_trade_no());
        count = payTrdOrderDao.updateStatusById(payTrdOrder.getId(),10,response.getErrcode());

        if(count<=0) {
            throw new BizException("更新交易单状态失败");
        }

        accountService.pay(payTrdOrder);
    }

    @Transactional
    @Override
    public void refund(RefundResponse refundResponse) {
        PayRefundOrder payRefundOrder = payRefundOrderDao.selectByRefundNo(refundResponse.getOut_trade_no(),0);

        int count = payRefundOrderDao.updateStatus(payRefundOrder.getId(),10,refundResponse.getErrcode());
        if(count<=0) {
            throw new BizException("更新退款单失败");
        }

        PayTrdOrder payTrdOrder = payTrdOrderDao.selectByOrderNo(payRefundOrder.getOriNo());
        count = payTrdOrderDao.updateStatusById(payTrdOrder.getId(),PayTrdOrder.STATUS.SUCCESS,PayTrdOrder.STATUS.REFUND);
        if(count<=0) {
            throw new BizException("更新交易单失败");
        }

        accountService.refund(refundResponse);
    }

    @Transactional
    @Override
    public void reverse(ReverseResponse reverseResponse) {
        PayReverseOrder payReverseOrder = payReverseOrderDao.selectByTrdOrder(reverseResponse.getOut_trade_no());
        if(Objects.isNull(payReverseOrder)) {
            throw new BizException("未查到取消记录");
        }

        int count = payReverseOrderDao.updateStatus(payReverseOrder.getId(),PayReverseOrder.STATUS.INIT,reverseResponse.getErrcode());
        if(count<=0) {
            throw new BizException("更新交易单失败");
        }

        if(reverseResponse.getErrcode() != 0) {
            return;
        }

        PayTrdOrder payTrdOrder = payTrdOrderDao.selectByOrderNo(payReverseOrder.getOutTradeNo());
        if(payTrdOrder.getStatus()!=10 && payTrdOrder.getStatus() != 4) {
            throw new BizException("交易单状态异常");
        }
        count = payTrdOrderDao.updateStatusById(payTrdOrder.getId(),payTrdOrder.getStatus(),PayTrdOrder.STATUS.REVERSE);
        if(count<=0) {
            throw new BizException("更新交易单失败");
        }

        accountService.reverse(reverseResponse);
    }
}
