package jh.biz.service.impl;

import hf.base.exceptions.BizFailException;
import jh.biz.service.AccountService;
import jh.dao.local.*;
import jh.model.dto.RefundResponse;
import jh.model.dto.ReverseResponse;
import jh.model.enums.OprStatus;
import jh.model.po.Account;
import jh.model.po.AccountOprLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;

    @Override
    public void refund(RefundResponse refundResponse) {

    }

    @Override
    public void reverse(ReverseResponse reverseResponse) {

    }

    @Transactional
    @Override
    public void promote(AccountOprLog log) {
        int count = accountOprLogDao.updateStatusById(log.getId(), OprStatus.PAY_SUCCESS.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update opr log status from 1 to 10 failed,%s",log.getId()));
        }
        Account account = accountDao.selectByPrimaryKey(log.getAccountId());
        count = accountDao.addAmount(account.getId(),log.getAmount(),account.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update account amount failed.opr id:%s",log.getId()));
        }
    }
}
