package jh.api;

import hf.base.contants.CodeManager;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.UserBiz;
import jh.dao.local.ChannelDao;
import jh.dao.local.UserBankCardDao;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.UserBankCard;
import jh.model.po.Channel;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import jh.model.dto.*;
import jh.utils.TypeConverter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserApi {
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserBankCardDao userBankCardDao;

    @RequestMapping(value = "/get_user_list",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<List<UserInfoDto>> getUserList(@RequestBody UserInfoRequest request) {
        List<UserInfoDto> list = userBiz.getUserList(request);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_user_group_list",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<List<UserGroupDto>> getUserGroupList(@RequestBody UserGroupRequest request) {
        List<UserGroupDto> list = userBiz.getUserGroupList(request);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_user_info",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<UserInfo> getUserInfo(@RequestBody Map<String,Object> params) {
        if(MapUtils.isEmpty(params) || Objects.isNull(params.get("loginId")) || Objects.isNull(params.get("password")) || Objects.isNull(params.get("userType"))) {
            return ResponseResult.failed(CodeManager.PARAM_INVALID,"param invalid",new UserInfo());
        }

        String loginId = String.valueOf(params.get("loginId"));
        String password = String.valueOf(params.get("password"));
        int userType = new BigDecimal(params.get("userType").toString()).intValue();
        UserInfo userInfo = userInfoDao.selectByLoginId(loginId,Utils.convertPassword(password));

        if(Objects.isNull(userInfo)) {
            return ResponseResult.failed(CodeManager.GET_USER_FAILED,"用户不存在",new UserInfo());
        }

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(userInfo.getGroupId());

        if(userGroup.getType() != userType) {
            if(userType==3 && userGroup.getType() == 10) {
                return ResponseResult.success(userInfo);
            }
            return ResponseResult.failed(CodeManager.GET_USER_FAILED,"用户类型错误",new UserInfo());
        }

        return ResponseResult.success(userInfo);
    }

    @RequestMapping(value = "/edit_password",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Boolean> editPassword(@RequestBody Map<String,Object> params) {
        Map<String,Object> map = Utils.buildMap("userId",params.get("userId"),
                "ypassword",Utils.convertPassword(String.valueOf(params.get("ypassword"))),
                "newpassword",Utils.convertPassword(String.valueOf(params.get("newpassword"))),
                "newpasswordok",Utils.convertPassword(String.valueOf(params.get("newpasswordok"))));

        int count = userInfoDao.resetPassword(map);

        if(count == 1) {
            return ResponseResult.success(Boolean.TRUE);
        }

        return ResponseResult.failed(CodeManager.RESET_PASSWORD_FAILED,"update password failed",Boolean.FALSE);
    }

    @RequestMapping(value = "/get_channel_list",method = RequestMethod.GET ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<List<Channel>> getChannelList() {
        List<Channel> list = channelDao.selectForList();
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_channel_list",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ResponseResult<Boolean> addChannel(@RequestBody Channel channel) {
        try {
            channelDao.insertSelective(channel);
        } catch (Exception e) {
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/customer_register",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ResponseResult<String> customerRegister(@RequestBody HashMap<String,String> datas) {
        String loginId = datas.get("loginId");
        String password = datas.get("password");
        String inviteCode = datas.get("inviteCode");

        if(StringUtils.isEmpty(loginId) || StringUtils.isEmpty(password)) {
            return ResponseResult.failed(CodeManager.PARAM_INVALID,"param invalid","FAILED");
        }

        try {
            userBiz.register(loginId,password,inviteCode);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),"FAIELD");
        }

        return ResponseResult.success("SUCCESS");
    }

    @RequestMapping(value = "/edit_user_info",method = RequestMethod.POST)
    public ResponseResult<UserInfo> editUserInfo(@RequestBody Map<String,Object> data) {
        try {
            UserInfo userInfo = TypeConverter.convert(data,UserInfo.class);
            userBiz.edit(userInfo);

            userInfo = userInfoDao.selectByPrimaryKey(userInfo.getId());
            return ResponseResult.success(userInfo);

        } catch (Exception e) {
            Long id = Long.parseLong(data.get("id").toString());
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(id);
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),userInfo);
        }
    }

    @RequestMapping(value = "/edit_user_group",method = RequestMethod.POST)
    public ResponseResult<UserGroup> editUserGroup(@RequestBody Map<String,Object> data) {
        try {
            UserGroup userGroup = TypeConverter.convert(data, UserGroup.class);
            userBiz.edit(userGroup);

            userGroup = userGroupDao.selectByPrimaryKey(userGroup.getId());

            return ResponseResult.success(userGroup);
        } catch (Exception e) {
            Long id = Long.parseLong(String.valueOf(data.get("id")));
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(id);
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),userGroup);
        }
    }

    @RequestMapping(value = "/get_user_bank_card",method = RequestMethod.POST)
    public ResponseResult<List<UserBankCard>> getUserBankCards(Long groupId) {
        List<UserBankCard> list = userBankCardDao.selectByUser(groupId);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/save_user_card",method = RequestMethod.POST)
    public ResponseResult<List<UserBankCard>> saveBankCard(@RequestBody Map<String,Object> data) {
        UserBankCard userBankCard ;
        try {
            userBankCard = TypeConverter.convert(data, UserBankCard.class);
        }catch (Exception e) {
            throw new BizFailException("解析参数失败");
        }
        userBankCardDao.insertSelective(userBankCard);

        Long groupId = userBankCard.getGroupId();
        List<UserBankCard> list = userBankCardDao.selectByUser(groupId);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/submit_user_to_admin",method = RequestMethod.POST)
    public ResponseResult<String> submitUserToAdmin(@RequestBody Map<String,Long> ids) {
        Long userId = ids.get("userId");
        Long groupId = ids.get("groupId");

        if(Objects.isNull(userId) || Objects.isNull(groupId)) {
            throw new BizFailException("userId or groupId cannot be null");
        }

        userBiz.submit(userId,groupId);

        return ResponseResult.success("SUCCESS");
    }

    @RequestMapping(value = "/get_user_auth_status",method = RequestMethod.GET)
    public String getUserAuthStatus (Long userId) {
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
        switch (userInfo.getStatus()) {
            case 0:
                return "请补充用户信息";
            case 1:
                return "审核中";
            case 2:
                return "您已成功认证!";
            case 3:
                return "请求已被驳回";
            default:
                return "您已成功认证!";
        }
    }
}
