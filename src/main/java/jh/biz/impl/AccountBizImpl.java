package jh.biz.impl;

import jh.biz.AccountBiz;
import jh.dao.local.AccountOprLogDao;
import jh.dao.local.UserInfoDao;
import jh.model.po.AccountOprLog;
import jh.model.po.UserInfo;
import jh.model.dto.AccountOprQueryRequest;
import jh.model.dto.AccountOprQueryResponse;
import jh.model.enums.Channel;
import jh.model.enums.OprType;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/11/4.
 */
@Service
public class AccountBizImpl implements AccountBiz {
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public List<AccountOprQueryResponse> getAccountOprLogs(AccountOprQueryRequest request) {
        List<AccountOprQueryResponse> result = new ArrayList<>();

        Map<String,Object> condtions = Utils.buildMap("userId",request.getUserId(),"outTradeNo",request.getOutTradeNo(),"startDate",request.getStartDate(),"channel",request.getChannel(),"oprType",request.getOprType());

        List<AccountOprLog> list = accountOprLogDao.select(condtions);
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(request.getUserId());

        list.stream().forEach(accountOprLog -> result.add(build(accountOprLog,userInfo)));
        return result;
    }

    private AccountOprQueryResponse build(AccountOprLog accountOprLog,UserInfo userInfo) {
        AccountOprQueryResponse accountOprQueryResponse = new AccountOprQueryResponse();
        accountOprQueryResponse.setUserId(accountOprLog.getUserId());

        accountOprQueryResponse.setUsername(userInfo.getName());
        accountOprQueryResponse.setRemark(accountOprLog.getRemark());
        accountOprQueryResponse.setChangeTime(DateFormatUtils.format(accountOprLog.getUpdateTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()) );
        accountOprQueryResponse.setChannel(Channel.parse(accountOprLog.getChannelType()).getDesc());
        accountOprQueryResponse.setOprType(OprType.parse(accountOprLog.getOprType()).getDesc());
        accountOprQueryResponse.setOrderId(accountOprLog.getTrdOrderId());
        accountOprQueryResponse.setOutTradeNo(accountOprLog.getOutTradeNo());
        accountOprQueryResponse.setPrice(accountOprLog.getPrice());
        accountOprQueryResponse.setPriceIn(accountOprLog.getPriceIn());
        accountOprQueryResponse.setStatus(AccountOprLog.OprStatus.parse(accountOprLog.getStatus()).getDesc());
        return accountOprQueryResponse;
    }
}
