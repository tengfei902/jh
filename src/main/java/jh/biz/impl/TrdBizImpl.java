package jh.biz.impl;

import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.model.TradeRequest;
import hf.base.model.TradeRequestDto;
import hf.base.utils.MapUtils;
import hf.base.utils.Pagenation;
import jh.biz.TrdBiz;
import jh.biz.service.CacheService;
import jh.biz.service.UserService;
import jh.dao.local.PayRequestDao;
import jh.dao.local.UserGroupDao;
import jh.model.po.PayRequest;
import jh.model.po.UserGroup;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        list.parallelStream().forEach(payRequest -> result.add(buildPayRequest(payRequest,mchMap)));

        Integer totalSize = payRequestDao.selectCount(params);

        return new Pagenation<TradeRequestDto>(result,totalSize,request.getCurrentPage(),request.getPageSize());
    }

    private TradeRequestDto buildPayRequest(PayRequest payRequest,Map<String,UserGroup> map) {
        TradeRequestDto tradeRequestDto = new TradeRequestDto();
        tradeRequestDto.setCreateTime(payRequest.getCreateTime());
        tradeRequestDto.setFee(payRequest.getFee().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setGroupName(map.get(payRequest.getMchId()).getName());
        tradeRequestDto.setId(payRequest.getId());
        tradeRequestDto.setMchId(payRequest.getMchId());
        tradeRequestDto.setOutTradeNo(payRequest.getOutTradeNo());
        tradeRequestDto.setType(payRequest.getTradeType());
        tradeRequestDto.setActualAmount(payRequest.getActualAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setAmoun(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setSuccessTime(payRequest.getUpdateTime());
        tradeRequestDto.setTypeDesc(TradeType.parse(payRequest.getTradeType()).getDesc());
        tradeRequestDto.setService(payRequest.getService());
        tradeRequestDto.setStatus(payRequest.getStatus());
        tradeRequestDto.setStatusDesc(PayRequestStatus.parse(payRequest.getStatus()).getDesc());
        return tradeRequestDto;
    }
}
