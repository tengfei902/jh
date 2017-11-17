package jh.dao.local;

import jh.model.po.PayTrdOrder;
import org.apache.ibatis.annotations.Param;

public interface PayTrdOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayTrdOrder record);

    int insertSelective(PayTrdOrder record);

    PayTrdOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayTrdOrder record);

    int updateByPrimaryKey(PayTrdOrder record);

    int updateStatusById(@Param("id") Long id,@Param("fromStatus") Integer fromStatus,@Param("targetStatus")Integer targetStatus);

    PayTrdOrder selectByOrderNo(@Param("outTradeNo") String outTradeNo);
}