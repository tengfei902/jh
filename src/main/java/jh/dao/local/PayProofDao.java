package jh.dao.local;

import jh.model.po.PayProof;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface PayProofDao {
    int insert(PayProof record);

    int insertSelective(PayProof record);

    PayProof selectByTrdNo(@Param("outTradeNo") String trdNo);

    int update(Map<String,Object> params);
}