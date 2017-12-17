package jh.dao.local;

import jh.model.po.UserChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserChannelDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserChannel record);

    int insertSelective(UserChannel record);

    UserChannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserChannel record);

    int updateByPrimaryKey(UserChannel record);

    List<UserChannel> selectByGroupId(@Param("groupId") Long groupId);

    UserChannel selectByGroupChannel(@Param("groupId") Long groupId,@Param("channelId") Long channelId);

    UserChannel selectByMchId(@Param("mchId")String mchId,@Param("channelCode") String channelCode);

    List<UserChannel> selectByGroupIdList(@Param("channelId") Long channelId,@Param("groupIds")List<Long> groupIds);
}