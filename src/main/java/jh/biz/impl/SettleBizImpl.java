package jh.biz.impl;

import hf.base.contants.Constants;
import hf.base.enums.OprStatus;
import hf.base.enums.OprType;
import hf.base.enums.SettleStatus;
import hf.base.exceptions.BizFailException;
import jh.biz.SettleBiz;
import jh.biz.service.AccountService;
import jh.biz.service.CacheService;
import jh.dao.local.*;
import jh.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SettleBizImpl implements SettleBiz {
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private AdminBankCardDao adminBankCardDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;

    @Override
    public void saveSettle(SettleTask settleTask) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(settleTask.getGroupId());
        Account account = accountDao.selectByGroupId(settleTask.getGroupId());

        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(userGroup.getCompanyId());
        AdminAccount payAccount = adminAccountDao.selectByGroupId(subUserGroup.getId());

        AdminBankCard payBankCard = adminBankCardDao.selectByCompanyId(userGroup.getCompanyId(),userGroup.getId());
        settleTask.setPayAccountId(payAccount.getId());
        BigDecimal feeRate = new BigDecimal(cacheService.getProp(Constants.SETTLE_FEE_RATE,"5"));
        BigDecimal fee = settleTask.getSettleAmount().multiply(feeRate).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal payAmount = settleTask.getSettleAmount().subtract(fee);

        settleTask.setAccountId(account.getId());
        settleTask.setFee(fee);
        settleTask.setFeeRate(feeRate);
        settleTask.setPayAmount(payAmount);
        settleTask.setPayBankCard(payBankCard.getId());
        settleTask.setPayGroupId(userGroup.getCompanyId());

        settleTaskDao.insertSelective(settleTask);

        accountService.settle(settleTask.getId());
    }

    @Transactional
    @Override
    public void finishSettle(Long id) {
        SettleTask settleTask = settleTaskDao.selectByPrimaryKey(id);
        //支付金额
        AccountOprLog withdrawAccountLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.WITHDRAW.getValue());
        int count = accountOprLogDao.updateStatusById(withdrawAccountLog.getId(), OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update oprLog status failed");
        }

        Account withdrawAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        count = accountDao.finishWithDraw(withdrawAccount.getId(),withdrawAccountLog.getAmount(),withdrawAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("finish withdraw failed");
        }

        //手续费
        AccountOprLog feeLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.TAX.getValue());
        count = accountOprLogDao.updateStatusById(feeLog.getId(),OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update feelog status failed");
        }

        withdrawAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        count = accountDao.finishTax(withdrawAccount.getId(),feeLog.getAmount(),withdrawAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("update fee account failed");
        }

        //admin log
        AdminAccountOprLog adminLog = adminAccountOprLogDao.selectByNo(String.valueOf(settleTask.getId()));
        if(adminLog.getType() != OprType.WITHDRAW.getValue()) {
            throw new BizFailException("opr log type not match");
        }

        count = adminAccountOprLogDao.updateStatusById(adminLog.getId(),OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update admin Opr log status failed");
        }

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        count = adminAccountDao.finishPay(adminAccount.getId(),adminLog.getAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("update admin amount failed");
        }

        //admin true pay log
        AccountOprLog payLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getPayGroupId(),OprType.FEE.getValue());
        count = accountOprLogDao.updateStatusById(payLog.getId(),OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update opr log failed");
        }

        Account payAccount = accountDao.selectByGroupId(settleTask.getPayGroupId());
        count = accountDao.addAmount(payAccount.getId(),payLog.getAmount(),payAccount.getVersion());

        if(count<=0) {
            throw new BizFailException("update pay Account failed");
        }

        count = settleTaskDao.updateStatusById(settleTask.getId(), SettleStatus.PROCESSING.getValue(),SettleStatus.SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException("update settle status failed");
        }
    }
}
