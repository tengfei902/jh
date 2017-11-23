package jh.dao.local;

import jh.model.po.AdminAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface AdminAccountDao {
    int deleteByPrimaryKey(Long id);

    int insert(AdminAccount record);

    int insertSelective(AdminAccount record);

    AdminAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AdminAccount record);

    int updateByPrimaryKey(AdminAccount record);

    AdminAccount selectByGroupId(@Param("groupId") Long groupId);

    int addAmount(@Param("id")Long id , @Param("amount")BigDecimal amount,@Param("version") Integer version);
}