package jh.api;

import hf.base.contants.CodeManager;
import hf.base.utils.TypeConverter;
import jh.biz.SettleBiz;
import jh.model.dto.ResponseResult;
import jh.model.po.SettleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/settle")
public class SettleApi {
    @Autowired
    private SettleBiz settleBiz;

    @RequestMapping(value = "/new_settle_request",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Boolean> settleRequest(@RequestBody Map<String,Object> params) {
        try {
            SettleTask settleTask = TypeConverter.convert(params,SettleTask.class);
            settleBiz.saveSettle(settleTask);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }

    }
}
