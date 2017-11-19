package jh.api;

import hf.base.contants.CodeManager;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.ChannelBiz;
import jh.biz.UserBiz;
import jh.dao.local.*;
import jh.model.po.*;
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
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelBiz channelBiz;
    @Autowired
    private AdminBankCardDao adminBankCardDao;

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

    @RequestMapping(value = "/get_user_info_by_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<UserInfo> getUserInfoById(@RequestBody Map<String,Object> data) {
        Long id = new BigDecimal(data.get("id").toString()).longValue();
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(id);
        return ResponseResult.success(userInfo);
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

    @RequestMapping(value = "/get_user_group_by_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<UserGroup> getUserGroupById(@RequestBody Map<String,Object> data) {
        Long groupId = new BigDecimal(data.get("id").toString()).longValue();
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        return ResponseResult.success(userGroup);
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

    @RequestMapping(value = "/get_ava_channel_list",method = RequestMethod.GET ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<List<Channel>> getAvaChannelList() {
        List<Channel> list = channelDao.selectForAvaList();
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/add_channel",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
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
    public @ResponseBody ResponseResult<UserInfo> editUserInfo(@RequestBody Map<String,Object> data) {
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
    public @ResponseBody ResponseResult<UserGroup> editUserGroup(@RequestBody Map<String,Object> data) {
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
    public @ResponseBody ResponseResult<List<UserBankCard>> getUserBankCards(@RequestBody Map<String,String> params) {
        Long groupId = new BigDecimal(params.get("groupId")).longValue();
        List<UserBankCard> list = userBankCardDao.selectByUser(groupId);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/save_user_card",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> saveBankCard(@RequestBody Map<String,Object> data) {
        UserBankCard userBankCard ;
        try {
            userBankCard = TypeConverter.convert(data, UserBankCard.class);
            userBiz.saveBankCard(userBankCard);
        }catch (Exception e) {
            throw new BizFailException("解析参数失败");
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/submit_user_to_admin",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> submitUserToAdmin(@RequestBody Map<String,String> ids) {
        Long userId = new BigDecimal(ids.get("userId")).longValue();
        Long groupId = new BigDecimal(ids.get("groupId")).longValue();

        if(Objects.isNull(userId) || Objects.isNull(groupId)) {
            throw new BizFailException("userId or groupId cannot be null");
        }

        userBiz.submit(userId,groupId);

        return ResponseResult.success(Boolean.TRUE);
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

    @RequestMapping(value = "/get_user_list_by_groupId",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<List<UserInfo>> getUserListByGroupId(@RequestBody Map<String,Object> params) {
        Long groupId = Long.parseLong(params.get("groupId").toString());
        List<UserInfo> list = userInfoDao.selectByGroupId(groupId);
        return ResponseResult.success(list);
    }



    @RequestMapping(value = "/get_user_channel_list",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<List<UserChannel>> getUserChannelList(@RequestBody Map<String,String> params) {
        Long groupId = new BigDecimal(params.get("groupId")).longValue();
        List<UserChannel> userChannels = userChannelDao.selectByGroupId(groupId);
        return ResponseResult.success(userChannels);
    }

    @RequestMapping(value = "/save_channel",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> saveChannel(@RequestBody Map<String,Object> data) {
        try {
            Channel channel = TypeConverter.convert(data, Channel.class);
            channelDao.insertSelective(channel);
            return ResponseResult.success(true);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @RequestMapping(value = "/save_user_channel",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> saveUserChannel(@RequestBody Map<String,Object> data) {
        try {
            UserChannel userChannel = TypeConverter.convert(data,UserChannel.class);
            channelBiz.saveUserChannel(userChannel);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @RequestMapping(value = "/get_admin_bank_card",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<AdminBankCard> getAdminBankCard(@RequestBody Map<String,String> data) {
        Long groupId = new BigDecimal(data.get("groupId")).longValue();
        Long companyId = new BigDecimal(data.get("companyId")).longValue();
        AdminBankCard adminBankCard = adminBankCardDao.selectByCompanyId(companyId,groupId);
        return ResponseResult.success(adminBankCard);
    }

    @RequestMapping(value = "/save_admin_bank_card",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> saveAdminBankCard(@RequestBody Map<String,Object> data) {
        try {
            AdminBankCard adminBankCard = TypeConverter.convert(data,AdminBankCard.class);
            userBiz.saveAdminBankCard(adminBankCard);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @RequestMapping(value = "/user_turn_back",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> userTurnBack(@RequestBody Map<String,Object> data) {
        Long groupId = Long.parseLong(data.get("id").toString());
        userBiz.userTurnBack(groupId,String.format("%s,%s turn back",data.get("groupId"),data.get("userId")));
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/user_pass",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> userPass(@RequestBody Map<String,String> data) {
        Long groupId = new BigDecimal(data.get("id")).longValue();
        try {
            userBiz.userPass(groupId);
            return ResponseResult.success(Boolean.TRUE);
        } catch (BizFailException e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }

    }
}
