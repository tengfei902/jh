package jh.dao.local;

import jh.model.po.AccountOprLog;

import java.util.List;
import java.util.Map;

public interface AccountOprLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(AccountOprLog record);

    int insertSelective(AccountOprLog record);

    AccountOprLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountOprLog record);

    int updateByPrimaryKey(AccountOprLog record);

    List<AccountOprLog> select(Map<String,Object> conditions);
}