package jh.dao.local;

import jh.model.PayProp;

public interface PayPropDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayProp record);

    int insertSelective(PayProp record);

    PayProp selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayProp record);

    int updateByPrimaryKey(PayProp record);
}