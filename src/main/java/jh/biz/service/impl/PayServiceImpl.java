package jh.biz.service.impl;

import com.google.gson.Gson;
import hf.base.exceptions.BizFailException;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.model.dto.*;
import jh.model.enums.OprType;
import jh.model.po.*;
import jh.utils.BdUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
}
