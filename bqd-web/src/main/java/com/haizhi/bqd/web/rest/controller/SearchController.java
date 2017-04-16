package com.haizhi.bqd.web.rest.controller;

import com.haizhi.bqd.common.Wrapper;
import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.service.HistoricalTradeService;
import com.haizhi.bqd.service.support.HistoricalTradeReq;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chenbo on 17/4/13.
 */
@Controller
@RequestMapping("/search")
public class SearchController {
    @Setter
    @Autowired
    HistoricalTradeService historicalTradeService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Wrapper search(@RequestParam(value = "tracct", required = false) String tracct,
                          @RequestParam(value = "tratype", required = false) String tratype,
                          @RequestParam(value = "trctype", required = false) String trctype,
                          @RequestParam(value = "tr_begin_date", required = false) String trBeginDate,
                          @RequestParam(value = "tr_end_date", required = false) String trEndDate,
                          @RequestParam(value = "seq", required = false) String seq,
                          @RequestParam(value = "offset") Integer offset,
                          @RequestParam(value = "count") Integer count) {
        HistoricalTradeReq req = HistoricalTradeReq.builder()
                .tracct(tracct)
                .tratype(tratype)
                .trctype(trctype)
                .trBeginDate(trBeginDate)
                .trEndDate(trEndDate)
                .seq(seq)
                .from(offset)
                .size(count)
                .build();
        return Wrapper.OKBuilder.data(historicalTradeService.search(req)).build();
    }

    @RequestMapping(value = "/coa", method = RequestMethod.GET)
    @ResponseBody
    public Wrapper search(@RequestParam(value = "acc_id", required = false) String accId,
                          @RequestParam(value = "offset") Integer offset,
                          @RequestParam(value = "count") Integer count) {
        DataItem data = historicalTradeService.coaSearch(accId, offset, count);
        return Wrapper.OKBuilder.data(data).build();
    }

    @RequestMapping(value = "/acc_detail", method = RequestMethod.GET)
    @ResponseBody
    public Wrapper search(@RequestParam(value = "account_id") String accountId,
                          @RequestParam(value = "entity_id", required = false) String entityId,
                          @RequestParam(value = "curr_id", required = false) String currId,
                          @RequestParam(value = "offset") Integer offset,
                          @RequestParam(value = "count") Integer count) {
        DataItem data = historicalTradeService.accountDetails(accountId, entityId, currId, offset, count);
        return Wrapper.OKBuilder.data(data).build();
    }

}
