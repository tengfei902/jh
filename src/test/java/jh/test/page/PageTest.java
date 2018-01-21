package jh.test.page;

import com.google.gson.Gson;
import hf.base.utils.Utils;
import jh.biz.service.PageService;
import jh.model.dto.PageInfo;
import jh.test.BaseTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by tengfei on 2017/10/29.
 */
public class PageTest extends BaseTestCase {
    @Autowired
    private PageService pageService;

    @Test
    public void getPageInfo() {
        List<PageInfo> pageInfoList = pageService.getPageList();

        System.out.println(new Gson().toJson(pageInfoList));

    }

    @Test
    public void testGetCipherCode() {
        System.out.println(Utils.getRandomString(8));
    }
}
