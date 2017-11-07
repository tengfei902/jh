package jh.api;

import com.google.gson.Gson;
import jh.biz.UserBiz;
import jh.dao.local.UserBankCardDao;
import jh.dao.local.UserInfoDao;
import jh.model.UserBankCard;
import jh.model.UserInfo;
import jh.model.constant.Constants;
import jh.model.dto.ResponseResult;
import jh.model.dto.UserDto;
import jh.model.enums.Auth;
import jh.utils.ParameterRequestWrapper;
import jh.utils.TypeConverter;
import jh.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by tengfei on 2017/10/28.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private UserBankCardDao userBankCardDao;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<String> register(HttpServletRequest request, HttpServletResponse response) {

        String username  = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmpassword = request.getParameter("confirmpassword");
        String subUserId = StringUtils.isEmpty(request.getParameter("subUserId")) || !NumberUtils.isNumber(request.getParameter("subUserId")) ? "1" : request.getParameter("subUserId");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmpassword) || ! StringUtils.equals(password,confirmpassword)) {
            return ResponseResult.failed("9999999","注册失败","param failed");
        }

        try {
            Long userId = userBiz.register(username,password,subUserId);
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);

            UserDto userDto = new UserDto(userInfo);
            request.getSession().setAttribute(Constants.SESSION_USER_INFO,userDto);

            return null;
        } catch (Exception e) {
            return ResponseResult.failed("9999999","注册失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/check_user", produces = "application/json;charset=UTF-8")
    public @ResponseBody String checkUser(@RequestParam String username) {
        UserInfo userInfo = userInfoDao.selectByLoginId(username);
        Map<String,Object> result = Utils.buildMap("valid",Objects.isNull(userInfo));
        return new Gson().toJson(result);
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {

        String loginId = request.getParameter("username");
        String password = request.getParameter("password");

        ModelAndView modelAndView = new ModelAndView();

        if(StringUtils.isEmpty(loginId) || StringUtils.isEmpty(password)) {
            modelAndView.setViewName("redirect:/shouye.html");
            return modelAndView;
        }

        if(!userBiz.login(loginId,password)) {
            modelAndView.setViewName("redirect:/shouye.html");
            return modelAndView;
        }

        UserInfo userInfo = userInfoDao.selectByLoginId(loginId);
        UserDto userDto = new UserDto(userInfo);
        request.getSession().setAttribute(Constants.SESSION_USER_INFO,userDto);

        modelAndView.setViewName("redirect:/User_Login_checklogin.html");
        return modelAndView;
    }

    @RequestMapping(value = "/submit",method = RequestMethod.POST)
    public void submit(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String bank = request.getParameter("bank");
        String deposit = request.getParameter("deposit");
        String cardNo = request.getParameter("cardNo");
    }

    @RequestMapping(value="/hello")
    public ModelAndView printWelcome(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView mav= new ModelAndView();
        mav.addObject("city","test");
        mav.setViewName("hello");
        return mav;
    }

    @RequestMapping(value="/redirect/{pathStr}")
    public ModelAndView redirect(@PathVariable("pathStr") String pathStr) {
        ModelAndView mav= new ModelAndView();
        mav.setViewName("user/"+pathStr);
        return mav;
    }

    @RequestMapping(value="/getFrame/{page}")
    public ModelAndView getFrame(@PathVariable("page") String page) {
        ModelAndView mav= new ModelAndView();
        mav.addObject("page",page);
        mav.setViewName("hello");
        return mav;
    }

    @RequestMapping(value = "/edit_user_info",method = RequestMethod.POST)
    public ModelAndView editUserInfo(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(request);
        requestWrapper.addParameter("userId","1");
        requestWrapper.addParameter("status",1);

        try {
            //todo

            UserInfo userInfo = TypeConverter.convert(requestWrapper,UserInfo.class);
            userBiz.edit(userInfo);

            modelAndView.setViewName("/user/user_account_authorized");
            modelAndView.addObject("desc", Auth.parse(1).getDesc());

            return modelAndView;

        } catch (Exception e) {
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(Long.parseLong(requestWrapper.getParameter("userId")));
            modelAndView.addObject("userInfo",userInfo);
            modelAndView.setViewName("/user/user_account_profile");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/get_user_bank",method = RequestMethod.GET)
    public ModelAndView getBankCards(Long userId) {
        List<UserBankCard> list = userBankCardDao.selectByUser(userId);
        return null;
    }

    @RequestMapping(value = "/save_user_card",method = RequestMethod.POST)
    public void saveBankCard(HttpServletRequest request,HttpServletResponse response) {
        try {
            UserBankCard userBankCard = TypeConverter.convert((ParameterRequestWrapper) request,UserBankCard.class);

            userBankCardDao.insertSelective(userBankCard);
        } catch (Exception e) {

        }
    }

    @RequestMapping(value = "/get_user_auth_status",method = RequestMethod.GET)
    public String getUserAuthStatus (Long userId) {
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
        switch (userInfo.getStatus()) {
            case UserInfo.STATUS.INIT:
                return "请补充用户信息";
            case UserInfo.STATUS.PROCESSING:
                return "审核中";
            case UserInfo.STATUS.AVAILABLE:
                return "您已成功认证!";
            case UserInfo.STATUS.FAILED:
                return "请求已被驳回";
            default:
                return "您已成功认证!";
        }
    }

    @RequestMapping(value = "/reset_password",method = RequestMethod.POST)
    public String resetPassword(HttpServletRequest request,HttpServletResponse response) {
        Long userId = Long.parseLong(request.getParameter("userId"));
        String password = request.getParameter("password");
        String newPassword = request.getParameter("newPassword");

        if(StringUtils.isEmpty(newPassword) || newPassword.length()>20) {
            return "密码不符合规范";
        }

        UserInfo userInfo = userInfoDao.checkUser(userId, Utils.convertPassword(password));
        if(Objects.isNull(userInfo) || userInfo.getId() == null) {
            return "密码错误";
        }

        int count = userInfoDao.updatePassword(userId,Utils.convertPassword(newPassword),userInfo.getPassword());
        if(count <=0) {
            return "密码修改失败";
        }

        return "修改成功";
    }
}
