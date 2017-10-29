package jh.dao.local;

import jh.model.AccountOprLog;
import org.apache.ibatis.annotations.Param;

public interface AccountOprLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(AccountOprLog record);

    int insertSelective(AccountOprLog record);

    AccountOprLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountOprLog record);

    int updateByPrimaryKey(AccountOprLog record);

    AccountOprLog selectByTrdOrderId(@Param("trdOrderId") Long trdOrderId,@Param("oprType")Integer oprType);

    int updateStatusById(@Param("id")Long id , @Param("fromStatus") Integer fromStatus,@Param("toStatus") Integer toStatus);
}