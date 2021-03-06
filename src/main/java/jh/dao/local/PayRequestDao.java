package jh.dao.local;

import jh.model.po.PayRequest;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PayRequestDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayRequest record);

    int insertSelective(PayRequest record);

    PayRequest selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayRequest record);

    int updateByPrimaryKey(PayRequest record);

    PayRequest selectByTradeNo(@Param("tradeNo") String tradeNo);

    int updateStatusById(@Param("id") Long id,@Param("fromStatus") int fromStatus,@Param("toStatus") int toStatus);

    List<PayRequest> selectUnfinishedList(@Param("currentId")Long currentId, @Param("createTime") Date createTime);

    List<PayRequest> selectWaitingPromote();

    List<PayRequest> select(Map<String,Object> params);

    int selectCount(Map<String,Object> params);

    int updateFailed(@Param("id") Long id,@Param("fromStatus") int fromStatus,@Param("remark")String remark);

    int updatePayResult(@Param("id")Long id,@Param("payResult")String payResult,@Param("version") int version);

    int updateActualAmount(@Param("id")Long id, @Param("actualAmount")BigDecimal actualAmount,@Param("fee") BigDecimal fee,@Param("version")int version);

    int updateNoticeStatus(@Param("id")Long id);

    int updateNoticeRetryTime(@Param("id")Long id);
}