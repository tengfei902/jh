package jh.biz;

import jh.model.po.AdminBankCard;
import jh.model.po.UserBankCard;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import jh.model.dto.UserGroupDto;
import jh.model.dto.UserGroupRequest;
import jh.model.dto.UserInfoDto;
import jh.model.dto.UserInfoRequest;

import java.util.List;

/**
 * Created by tengfei on 2017/10/29.
 */
public interface UserBiz {
    void register(String loginId,String password,String inviteCode);

    boolean login(String loginId,String password);

    void edit(UserInfo userInfo);

    void edit(UserGroup userGroup);

    List<UserInfoDto> getUserList(UserInfoRequest request);

    List<UserGroupDto> getUserGroupList(UserGroupRequest request);

    void submit(Long userId,Long groupId);

    void saveBankCard(UserBankCard userBankCard);

    void saveAdminBankCard(AdminBankCard adminBankCard);

    void userTurnBack(Long groupId,String remark);

    void userPass(Long groupId);
}
