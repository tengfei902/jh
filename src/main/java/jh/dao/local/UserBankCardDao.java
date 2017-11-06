package jh.dao.local;

import jh.model.UserBankCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserBankCardDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserBankCard record);

    int insertSelective(UserBankCard record);

    UserBankCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBankCard record);

    int updateByPrimaryKey(UserBankCard record);

    List<UserBankCard> selectByUser(@Param("userId") Long userId);
}