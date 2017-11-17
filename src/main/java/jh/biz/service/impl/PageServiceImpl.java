package jh.biz.service.impl;

import jh.biz.service.PageService;
import jh.dao.local.PageLayOutDao;
import jh.model.po.PageLayOut;
import jh.model.dto.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tengfei on 2017/10/29.
 */
@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private PageLayOutDao pageLayOutDao;

    @Override
    public List<PageInfo> getPageList() {
        List<PageInfo> pageList = new ArrayList<>();

        List<PageLayOut> list =  pageLayOutDao.selectBySuperId(0L);
        for(PageLayOut pageLayOut:list) {
            PageInfo pageInfo = new PageInfo();

            pageList.add(pageInfo);

            pageInfo.setPageLayOut(pageLayOut);

            List<PageLayOut> subList = pageLayOutDao.selectBySuperId(pageLayOut.getId());

            pageInfo.setSubList(subList);

        }

        return pageList;
    }
}
