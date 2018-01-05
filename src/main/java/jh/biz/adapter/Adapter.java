package jh.biz.adapter;

import jh.model.dto.trade.IEntity;

import java.util.Map;

public interface Adapter<M extends IEntity> {
    M adpat(Map<String,Object> request);
}
