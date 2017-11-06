package jh.biz;

import jh.model.dto.AccountOprQueryRequest;
import jh.model.dto.AccountOprQueryResponse;
import java.util.List;

/**
 * Created by tengfei on 2017/11/4.
 */
public interface AccountBiz {

    List<AccountOprQueryResponse> getAccountOprLogs(AccountOprQueryRequest request);
}
