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
     * @param req
     * @return
     */
    DataItem search(HistoricalTradeReq req);

    /**
     * 按照总账科目模糊查询,主要为了拿到账号
     * @Param accId 模糊账号
     * @return
     */
    DataItem coaSearch(String accId, Integer from, Integer size);

    /**
     * 账目详情查询
     * @param accountId 账户ID
     * @param entityId 实体ID
     * @param currId 币种
     * @return
     */
    DataItem accountDetails(String accountId, String entityId, String currId, Integer from, Integer size);

    /**
     * 定期交易查询(定期账户)
     * @param card
     * @return
     */
    DataItem ddhistSearch(String card);
}
