package jh.dao.local;

import jh.model.po.Channel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChannelDao {
    int deleteByPrimaryKey(Long id);

    int insert(Channel record);

    int insertSelective(Channel record);

    Channel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Channel record);

    int updateByPrimaryKey(Channel record);

    List<Channel> selectForList();

    List<Channel> selectForAvaList();

    Channel selectByCode(@Param("channelCode") String channelCode);
}