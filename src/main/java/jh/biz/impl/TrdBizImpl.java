package jh.biz.impl;

import com.google.gson.Gson;
import hf.base.enums.ChannelProvider;
import hf.base.enums.OprType;
import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.model.TradeRequest;
import hf.base.model.TradeRequestDto;
import hf.base.utils.EpaySignUtil;
import hf.base.utils.MapUtils;
import hf.base.utils.Pagenation;
import hf.base.utils.Utils;
import jh.biz.TrdBiz;
import jh.biz.service.CacheService;
import jh.biz.service.UserService;
import jh.dao.local.AccountOprLogDao;
import jh.dao.local.PayRequestDao;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserGroupExtDao;
import jh.dao.remote.PayClient;
import jh.model.po.AccountOprLog;
import jh.model.po.PayRequest;
import jh.model.po.UserGroup;
import jh.model.po.UserGroupExt;
import jh.utils.CipherUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrdBizImpl implements TrdBiz {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    @Qualifier("wwClient")
    private PayClient wwClient;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

    @Override
    public Pagenation<TradeRequestDto> getTradeList(TradeRequest request) {
        Map<String,Object> params = MapUtils.beanToMap(request);
        Integer startIndex = (request.getCurrentPage()-1)*request.getPageSize();
        params.put("startIndex",startIndex);
        params.put("pageSize",request.getPageSize());
        params.put("service",request.getChannelCode());

        List<UserGroup> groups = userService.getChildMchIds(request.getGroupId());
        List<String> mchIds = groups.parallelStream().map(UserGroup::getGroupNo).collect(Collectors.toList());

        params.put("mchIds",mchIds);

        List<PayRequest> list = payRequestDao.select(params);

        if(CollectionUtils.isEmpty(list)) {
            return new Pagenation(new ArrayList(),list.size(),request.getCurrentPage(),request.getPageSize());
        }

        List<UserGroup> userGroups = userGroupDao.selectByGroupNos(list.stream().map(PayRequest::getMchId).collect(Collectors.toSet()));
        Map<String,UserGroup> mchMap = userGroups.parallelStream().collect(Collectors.toMap(UserGroup::getGroupNo,Function.identity()));

        List<TradeRequestDto> result = new ArrayList<>();
        list.forEach(payRequest -> result.add(buildPayRequest(payRequest,mchMap)));

        Integer totalSize = payRequestDao.selectCount(params);

        return new Pagenation<>(result,totalSize,request.getCurrentPage(),request.getPageSize());
    }

    private TradeRequestDto buildPayRequest(PayRequest payRequest,Map<String,UserGroup> map) {
        TradeRequestDto tradeRequestDto = new TradeRequestDto();
        tradeRequestDto.setCreateTime(payRequest.getCreateTime());
        tradeRequestDto.setGroupName(null==map.get(payRequest.getMchId())?"":map.get(payRequest.getMchId()).getName());
        tradeRequestDto.setId(payRequest.getId());
        tradeRequestDto.setMchId(payRequest.getMchId());
        tradeRequestDto.setOutTradeNo(payRequest.getOutTradeNo());
        tradeRequestDto.setType(payRequest.getTradeType());

        tradeRequestDto.setActualAmount(payRequest.getActualAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setFee(payRequest.getFee().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        if(payRequest.getActualAmount().compareTo(BigDecimal.ZERO)<=0) {
            if(null!=map.get(payRequest.getMchId())) {
                AccountOprLog accountOprLog = accountOprLogDao.selectByUnq(payRequest.getOutTradeNo(),map.get(payRequest.getMchId()).getId(), OprType.PAY.getValue());
                tradeRequestDto.setActualAmount(accountOprLog.getAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
                tradeRequestDto.setFee((new BigDecimal(payRequest.getTotalFee()).subtract(accountOprLog.getAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP) );
            }
        }

        tradeRequestDto.setAmoun(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setSuccessTime(payRequest.getUpdateTime());
        tradeRequestDto.setTypeDesc(TradeType.parse(payRequest.getTradeType()).getDesc());
        tradeRequestDto.setService(payRequest.getService());
        tradeRequestDto.setStatus(payRequest.getStatus());
        tradeRequestDto.setStatusDesc(PayRequestStatus.parse(payRequest.getStatus()).getDesc());
        return tradeRequestDto;
    }

    @Override
    public Map<String,Object> orderInfo(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        if(Objects.isNull(payRequest)) {
            return MapUtils.buildMap("msg","未生成交易单");
        }
        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.WW.getCode());
        Map<String,Object> params = new HashMap<>();
        params.put("memberCode",userGroupExt.getMerchantNo());
        params.put("orderNum",payRequest.getOutTradeNo());
        String signUrl = Utils.getEncryptStr(params);
        String signStr = EpaySignUtil.sign(CipherUtils.private_key,signUrl);
        params.put("signStr",signStr);
        Map<String,Object> result = wwClient.orderinfo(params);
        result.put("hfInfo",payRequest);

        return MapUtils.buildMap("msg",result);
    }
}
