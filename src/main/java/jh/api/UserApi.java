package jh.api;

import jh.biz.UserBiz;
import jh.dao.local.ChannelDao;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.constant.CodeManager;
import jh.model.po.Channel;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import jh.model.constant.Constants;
import jh.model.dto.*;
import jh.utils.Utils;
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
}
