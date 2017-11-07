package jh.api;

import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.model.enums.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by tengfei on 2017/10/31.
 */
@Controller
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private UserInfoDao userInfoDao;

    @RequestMapping(value="/{module}/{page}")
    public ModelAndView dispatch(HttpServletRequest request, @PathVariable("module")String module, @PathVariable("page") String page) {

        System.out.println("module:"+module);
        System.out.println("page:"+page);
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName(String.format("/%s/%s",module,page));

        if(StringUtils.equals("user_account_profile",page)) {
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(1L);
            modelAndView.addObject("userInfo",userInfo);
        }

        if(StringUtils.equals("user_account_authorized",page)) {
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(1L);
            modelAndView.addObject("desc", Auth.parse(userInfo.getStatus()).getDesc());
        }

        return modelAndView;
    }

    @RequestMapping(value="/{page}")
    public ModelAndView main(@PathVariable("page")String page) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName(String.format("/%s",page));
        return modelAndView;
    }
}
