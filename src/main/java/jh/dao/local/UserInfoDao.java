package jh.dao.local;

import jh.model.UserInfo;
import jh.model.dto.UserInfoRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserInfoDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    List<UserInfo> selectByGroupId(@Param("groupId") Long groupId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByLoginId(@Param("loginId")String loginId,@Param("password")String password);

    UserInfo checkLogin(@Param("loginId")String loginId,@Param("password")String password);

    UserInfo checkUser(@Param("id")Long id,@Param("password")String password);

    UserInfo selectByMerchantNo(@Param("merchantNo") String merchantNo);

    int udpate(UserInfo userInfo);

    int updatePassword(@Param("id")Long id,@Param("newPassword")String newPassword,@Param("password")String password);

    List<UserInfo> select(UserInfoRequest request);

    int resetPassword(Map<String,Object> params);
}