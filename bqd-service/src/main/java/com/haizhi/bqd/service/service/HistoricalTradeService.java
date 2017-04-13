package com.haizhi.bqd.service.service;

import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.support.HistoricalTradeReq;

import java.util.List;
import java.util.Map;

/**
 * Created by chenbo on 17/4/13.
 */
public interface HistoricalTradeService {
    DataItem search(HistoricalTradeReq req);
}
