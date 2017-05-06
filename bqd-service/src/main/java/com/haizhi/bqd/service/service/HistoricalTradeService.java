package com.haizhi.bqd.service.service;

import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.support.HistoricalTradeReq;

import java.util.List;
import java.util.Map;

/**
 * Created by chenbo on 17/4/13.
 */
public interface HistoricalTradeService {
    /**
     * 历史交易查询(活期账户)
     * @return
     */
    DataItem search(String tracct, String trbr, String trctyp, String trBeginDate, String trEndDate,
                    Integer from, Integer size);

    /**
     * 账目详情查询
     * @return
     */
    DataItem accountDetails(String card, String trbr, String trctyp, String trBeginDate, String trEndDate,
                            Integer from, Integer size);

    /**
     * 按照总账科目模糊查询,主要为了拿到账号
     * @Param accId 模糊账号
     * @return
     */
    DataItem coaSearch(String accId, Integer from, Integer size);

    /**
     * 定期交易查询(定期账户)
     * @param card 账号
     * @Param branchId 机构
     * @Param currId 币种
     * @return
     */
    DataItem ddhistSearch(String card, String branchId, String currId);

    DataItem union(String tracct, String trctyp, String trsobr, String txDtStart, String txDtEnd, Integer from, Integer size);
}
