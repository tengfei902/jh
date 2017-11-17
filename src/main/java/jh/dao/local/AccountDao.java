package jh.dao.local;

import jh.model.po.Account;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface AccountDao {
    int deleteByPrimaryKey(Long id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);

    Account selectByUserId(@Param("userId") Long userId);

    int addAmount(@Param("id") Long id, @Param("amount")BigDecimal amount,@Param("version") Integer version);
}