package jh.dao.local;

import jh.model.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo checkLogin(@Param("loginId")String loginId,@Param("password")String password);

    UserInfo selectByMerchantNo(@Param("merchantNo") String merchantNo);

    UserInfo selectByLoginId(@Param("loginId") String loginId);
}