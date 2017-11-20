package jh.dao.local;

import jh.model.po.PayRequest;
import org.apache.ibatis.annotations.Param;

public interface PayRequestDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayRequest record);

    int insertSelective(PayRequest record);

    PayRequest selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayRequest record);

    int updateByPrimaryKey(PayRequest record);

    PayRequest selectByTradeNo(@Param("tradeNo") String tradeNo);
}