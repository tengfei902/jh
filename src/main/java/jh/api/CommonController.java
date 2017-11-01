package jh.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by tengfei on 2017/10/31.
 */
@Controller
@RequestMapping("/common")
public class CommonController {

    @RequestMapping(value="/{module}/{page}")
    public ModelAndView dispatch(@PathVariable("module")String module,@PathVariable("page") String page) {

        System.out.println("module:"+module);
        System.out.println("page:"+page);
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName(String.format("/%s/%s",module,page));

        return modelAndView;
    }

    @RequestMapping(value="/main")
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/index");
        return modelAndView;

    }
}
