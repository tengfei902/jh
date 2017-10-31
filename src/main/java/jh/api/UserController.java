package jh.api;

import jh.biz.UserBiz;
import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.model.constant.Constants;
import jh.model.dto.UserDto;
import jh.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        String username  = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmpassword = request.getParameter("confirmpassword");
        String email = request.getParameter("email");

        ModelAndView modelAndView = new ModelAndView();

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmpassword) || StringUtils.isEmpty(email) || ! StringUtils.equals(password,confirmpassword)) {
            modelAndView.setViewName("User_Login_register.html");
            return modelAndView;
        }

        Long userId = userBiz.register(username,password,email);

        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);

        UserDto userDto = new UserDto(userInfo);

        request.getSession().setAttribute(Constants.SESSION_USER_INFO,userDto);
        modelAndView.setViewName("");
        return modelAndView;
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
}
