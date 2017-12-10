package jh.biz;

import hf.base.model.*;
import hf.base.utils.Pagenation;
import jh.model.dto.AccountOprQueryRequest;
import jh.model.dto.AccountOprQueryResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tengfei on 2017/11/4.
 */
public interface AccountBiz {
    List<AccountOprQueryResponse> getAccountOprLogs(AccountOprQueryRequest request);
    Pagenation<AccountPageInfo> getAccountPage(AccountRequest accountRequest);
    Pagenation<AdminAccountPageInfo> getAdminAccountPage(AccountRequest accountRequest);
    Pagenation<AccountOprInfo> getAccountOprPage(AccountOprRequest accountOprRequest);
    BigDecimal getLockedAmount(Long groupId);
}
