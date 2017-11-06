package jh.api;

import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Created by tengfei on 2017/10/29.
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserInfoDao userInfoDao;

    @RequestMapping("/doLogin")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        password = password.trim().toLowerCase();

        UserInfo userInfo = userInfoDao.checkLogin(loginId,password);

        ModelAndView mv = new ModelAndView();

        if(Objects.isNull(userInfo) || Objects.isNull(userInfo.getId())) {
            UserDto userDto = new UserDto();
            userDto.setMsg("用户名或密码错误");
            request.getSession().setAttribute("user_login",userDto);
            mv.setViewName("redirect:/login.jsp");
            return mv;
        }

        if(userInfo.getStatus()!=0) {
            UserDto userDto = new UserDto();
            userDto.setMsg("用户暂不可用");
            request.getSession().setAttribute("user_login",userDto);
            mv.setViewName("redirect:/login.jsp");
            return mv;
        }

        UserDto userDto = new UserDto(userInfo);

        if(userDto.getUserType() == 0) {
            mv.setViewName("redirect:/main.jsp");
        } else {
            mv.setViewName("redirect:/main.jsp");
        }
        return mv;
    }

    public void register() {

    }
}
