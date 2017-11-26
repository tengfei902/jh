package jh.dao.local;

import jh.model.po.SettleTask;
import org.apache.ibatis.annotations.Param;

public interface SettleTaskDao {
    int deleteByPrimaryKey(Long id);

    int insert(SettleTask record);

    int insertSelective(SettleTask record);

    SettleTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SettleTask record);

    int updateByPrimaryKey(SettleTask record);

    int updateStatusById(@Param("id")Long id,@Param("fromStatus")Integer fromStatus,@Param("toStatus")Integer toStatus);
}