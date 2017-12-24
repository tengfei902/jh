package jh.biz.service.impl;

import hf.base.exceptions.BizFailException;
import jh.biz.service.ArchService;
import jh.dao.local.TblSequenceDao;
import jh.model.po.TblSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArchServiceImpl implements ArchService {
    @Autowired
    private TblSequenceDao tblSequenceDao;

    @Override
    public synchronized String getId() {
        TblSequence tblSequence = tblSequenceDao.selectByPrimaryKey("seq_no");
        Integer nextId = tblSequence.getCurrentVal()+1;
        int count = tblSequenceDao.updateCurrentVal("seq_no",tblSequence.getCurrentVal(),nextId);
        if(count<=0) {
            throw new BizFailException("get id failed");
        }
        return String.valueOf(nextId);
    }
}
