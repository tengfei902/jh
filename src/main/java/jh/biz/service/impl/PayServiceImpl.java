package jh.biz.service.impl;

import com.google.gson.Gson;
import hf.base.enums.OprStatus;
import hf.base.enums.OprType;
import hf.base.enums.PayRequestStatus;
import hf.base.exceptions.BizFailException;
import hf.base.utils.BdUtils;
import jh.biz.service.AccountService;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private AdminAccountDao adminAccountDao;

    @Override
    @Transactional
    public void saveOprLog(PayRequest payRequest) {
        Channel channel = channelDao.selectByCode(payRequest.getService());
        UserGroup userGroup = userGroupDao.selectByGroupNo(payRequest.getMchId());
        UserChannel userChannel = userChannelDao.selectByGroupChannel(userGroup.getId(),channel.getId());

        List<UserGroup> groups = getGroupChain(userGroup);
        Collections.reverse(groups);

        List<Long> groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
        List<Account> accounts = accountDao.selectByGroupIds(groupIds);

        if(accounts.size() != groups.size()) {
            throw new BizFailException(String.format("account group not match,%s",new Gson().toJson(groupIds)));
        }

        Map<Long,Account> groupAccountMap = accounts.parallelStream().collect(Collectors.toMap(Account::getGroupId, Function.identity()));

        List<UserChannel> userChannels = userChannelDao.selectByGroupIdList(channel.getId(),groupIds);
        if(userChannels.size() != groups.size()) {
            throw new BizFailException(String.format("userChannel group not match,%s",new Gson().toJson(groupIds)));
        }
        Map<Long,UserChannel> userChannelMap = userChannels.parallelStream().collect(Collectors.toMap(UserChannel::getGroupId,Function.identity()));

        //金额
        BigDecimal amount = new BigDecimal(payRequest.getTotalFee());
        BigDecimal standardFee = amount.multiply(channel.getFeeRate()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);

        UserGroup adminGroup = groups.get(0);

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(adminGroup.getId());

        AdminAccountOprLog adminAccountOprLog = new AdminAccountOprLog();
        adminAccountOprLog.setAdminAccountId(adminAccount.getId());
        adminAccountOprLog.setAmount(amount.subtract(standardFee));
        adminAccountOprLog.setGroupId(adminGroup.getId());
        adminAccountOprLog.setOutTradeNo(payRequest.getOutTradeNo());
        adminAccountOprLog.setRemark(String.format("管理员入账:%s,费率:%s",payRequest.getTotalFee(),channel.getFeeRate()));
        adminAccountOprLog.setType(OprType.PAY.getValue());

        adminAccountOprLogDao.insertSelective(adminAccountOprLog);

        BigDecimal totalFee = userChannel.getFeeRate().multiply(amount).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal tempFee = channel.getFeeRate().multiply(amount).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);

        Map<Long,BigDecimal> feeMap = new HashMap<>();

        for(int i=0;i<groups.size();i++) {
            UserGroup group = groups.get(i);

            if(i==groups.size()-1) {
                feeMap.put(group.getId(),totalFee.subtract(tempFee));
                continue;
            }

            UserChannel groupChannel = userChannelMap.get(group.getId());

            BigDecimal groupTotalFee = groupChannel.getFeeRate().multiply(amount).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
            BigDecimal groupFee = groupTotalFee.subtract(tempFee);
            groupFee = BdUtils.max(groupFee,new BigDecimal("0"));
            feeMap.put(group.getId(),groupFee);

            tempFee = BdUtils.max(tempFee,groupTotalFee);
        }

        List<AccountOprLog> logs = new ArrayList<>();
        for(UserGroup group:groups) {
            AccountOprLog accountOprLog = new AccountOprLog();
            logs.add(accountOprLog);
            accountOprLog.setRemark(String.format("支付金额:%s,费率:%s",payRequest.getTotalFee(),userChannelMap.get(group.getId()).getFeeRate()));
            accountOprLog.setType(OprType.PAY.getValue());
            accountOprLog.setGroupId(group.getId());
            accountOprLog.setAmount(feeMap.get(group.getId()));
            accountOprLog.setOutTradeNo(payRequest.getOutTradeNo());
            Account account = groupAccountMap.get(group.getId());
            accountOprLog.setAccountId(account.getId());
        }

        int count = accountOprLogDao.batchInsert(logs);
        if(count != logs.size()){
            throw new BizFailException("batch insert logs failed");
        }

        count = payRequestDao.updateStatusById(payRequest.getId(), PayRequestStatus.NEW.getValue(),PayRequestStatus.OPR_GENERATED.getValue());
        if(count<=0) {
            throw new BizFailException("update pay request status failed");
        }
    }

    public List<UserGroup> getGroupChain(UserGroup leafGroup) {
        List<UserGroup> list = new ArrayList<>();
        list.add(leafGroup);
        if(leafGroup.getId().compareTo(leafGroup.getSubGroupId())==0) {
            return list;
        }
        Long tempGroupId = leafGroup.getSubGroupId();
        while(tempGroupId!=0L) {
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(tempGroupId);
            if(Objects.isNull(userGroup)) {
                break;
            }
            list.add(userGroup);
            if(userGroup.getId().compareTo(userGroup.getSubGroupId()) == 0) {
                break;
            }
            tempGroupId = userGroup.getSubGroupId();
        }
        return list;
    }

    @Transactional
    @Override
    public void paySuccess(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.REMOTE_CALL_FINISHED.getValue(),PayRequestStatus.PAY_SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update pay request status from 2 to 5 failed,%s",outTradeNo));
        }

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        count = adminAccountOprLogDao.updateStatusById(adminAccountOprLog.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update admin opr log status from 0 to 1 failed,log id:%s",adminAccountOprLog.getId()));
        }

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        for(AccountOprLog log:accountOprLogs) {
            count = accountOprLogDao.updateStatusById(log.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_SUCCESS.getValue());
            if(count<=0) {
                throw new BizFailException(String.format("update opr log status from 0 to 1 failed,logid:%s",log.getId()));
            }
        }
    }

    @Transactional
    @Override
    public void payPromote(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.PAY_SUCCESS.getValue(),PayRequestStatus.OPR_SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update payrequest status from 5 to 10 failed,tradeNo:%s",outTradeNo));
        }

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        count = adminAccountOprLogDao.updateStatusById(adminAccountOprLog.getId(),OprStatus.PAY_SUCCESS.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update account opr status failed,adminLogId:%s",adminAccountOprLog.getId()));
        }

        AdminAccount adminAccount = adminAccountDao.selectByPrimaryKey(adminAccountOprLog.getAdminAccountId());
        count = adminAccountDao.addAmount(adminAccount.getId(),adminAccountOprLog.getAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update admin Account amount failed,id:%s,amount:%s",adminAccount.getId(),adminAccountOprLog.getAmount()));
        }

        List<AccountOprLog> logs = accountOprLogDao.selectByTradeNo(outTradeNo);
        logs.stream().forEach(log -> accountService.promote(log));
    }
}
