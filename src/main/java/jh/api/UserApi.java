package jh.api;

import com.google.gson.Gson;
import jh.biz.UserBiz;
import jh.model.dto.UserInfoDto;
import jh.model.dto.UserInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserApi {
    @Autowired
    private UserBiz userBiz;

    @RequestMapping(value = "/get_user_list",method = RequestMethod.GET)
    public @ResponseBody String getUserList(UserInfoRequest request) {
        List<UserInfoDto> list = userBiz.getUserList(request);
        return new Gson().toJson(list);
    }
}
