package jh.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PayApi {

    public @ResponseBody String pay(@RequestBody Map<String,Object> params) {
        return null;
    }
}
