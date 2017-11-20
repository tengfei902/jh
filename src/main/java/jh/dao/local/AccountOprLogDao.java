package jh.dao.local;

import jh.model.po.AccountOprLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountOprLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(AccountOprLog record);

    int insertSelective(AccountOprLog record);

    AccountOprLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountOprLog record);

    int updateByPrimaryKey(AccountOprLog record);

    int batchInsert(List<AccountOprLog> logs);

    List<AccountOprLog> selectByTradeNo(@Param("outTradeNo") String outTradeNo);
}