package jh.api;

import hf.base.contants.CodeManager;
import hf.base.enums.GroupStatus;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import hf.base.utils.ResponseResult;
import jh.biz.UserBiz;
import jh.dao.local.UserGroupDao;
import jh.model.po.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class AdminApi {
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserBiz userBiz;

    @RequestMapping(value = "/get_authorized_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserGroup>> getAuthorizedList(@RequestBody Map<String,String> params) {
        Long companyId = Long.parseLong(params.get("companyId"));
        List<UserGroup> list = userGroupDao.select(MapUtils.buildMap("status", GroupStatus.SUBMITED.getValue(),"companyId",companyId));
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/save_admin_group",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> saveAdminGroup(@RequestBody Map<String,Object> params) {
        try {
            UserGroup userGroup = hf.base.utils.TypeConverter.convert(params, UserGroup.class);
            if(Objects.isNull(userGroup.getSubGroupId())) {
                throw new BizFailException("sub group id is null");
            }
            UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(userGroup.getSubGroupId());
            if(Objects.isNull(subUserGroup)) {
                throw new BizFailException(String.format("sub group not exists,id:%s",userGroup.getSubGroupId()));
            }
            userBiz.saveAminGroup(userGroup);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }
    }


}
