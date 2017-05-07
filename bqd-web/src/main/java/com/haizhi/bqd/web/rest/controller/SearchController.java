package com.haizhi.bqd.web.rest.controller;

import com.haizhi.bqd.common.Wrapper;
import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.service.HistoricalTradeService;
import com.haizhi.bqd.service.support.HistoricalTradeReq;
import com.haizhi.bqd.web.exception.SearchException;
import lombok.Setter;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by chenbo on 17/4/13.
 */
@Controller
@RequestMapping("/search")
public class SearchController {
    @Setter
    @Autowired
    HistoricalTradeService historicalTradeService;

    DecimalFormat df=new DecimalFormat("000");

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Wrapper searchHisData(@RequestParam(value = "tracct", required = false) String tracct,
                                 @RequestParam(value = "trbr", required = false) String trbr,
                                 @RequestParam(value = "trctyp", required = false) String trctyp,
                                 @RequestParam(value = "tr_begin_date", required = false) Long trBeginDate,
                                 @RequestParam(value = "tr_end_date", required = false) Long trEndDate,
                                 @RequestParam(value = "offset") Integer offset,
                                 @RequestParam(value = "count") Integer count) {
        if (Strings.isNullOrEmpty(tracct)) {
            return SearchException.MISS_ACCTNO.get();
        }
        if (trBeginDate == null) {
            return SearchException.MISS_TR_BEGIN_DATE.get();
        }
        if (trEndDate == null) {
            return SearchException.MISS_TR_END_DATE.get();
        }
        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(trBeginDate);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(trEndDate);
        String trBegin = begin.get(Calendar.YEAR) + "" + df.format(begin.get(Calendar.DAY_OF_YEAR));
        String trEnd = end.get(Calendar.YEAR) + "" + df.format(end.get(Calendar.DAY_OF_YEAR));
        DataItem data = historicalTradeService.search(tracct, trbr, trctyp, trBegin, trEnd, offset, count);
        return Wrapper.OKBuilder.data(data).build();
    }

    @RequestMapping(value = "/ddhist", method = RequestMethod.GET)
    @ResponseBody
    public Wrapper searchAccount(@RequestParam(value = "card", required = false) String card,
                                 @RequestParam(value = "trbr", required = false) String trbr,
                                 @RequestParam(value = "trctyp", required = false) String trctyp,
                                 @RequestParam(value = "tr_begin_date", required = false) Long trBeginDate,
                                 @RequestParam(value = "tr_end_date", required = false) Long trEndDate,
                                 @RequestParam(value = "offset") Integer offset,
                                 @RequestParam(value = "count") Integer count) {
        if (Strings.isNullOrEmpty(card)) {
            return SearchException.MISS_CARD.get();
        }
        if (trBeginDate == null) {
            return SearchException.MISS_TR_BEGIN_DATE.get();
        }
        if (trEndDate == null) {
            return SearchException.MISS_TR_END_DATE.get();
        }

        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(trBeginDate);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(trEndDate);
        String trBegin = begin.get(Calendar.YEAR) + "" + df.format(begin.get(Calendar.DAY_OF_YEAR));
        String trEnd = end.get(Calendar.YEAR) + "" + df.format(end.get(Calendar.DAY_OF_YEAR));
        DataItem data = historicalTradeService.accountDetails(card, trbr, trctyp, trBegin, trEnd, offset, count);
        return Wrapper.OKBuilder.data(data).build();
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
    public Wrapper search(@RequestParam(value = "acc_id") String card,
                          @RequestParam(value = "branch_id", required = false) String branchId,
                          @RequestParam(value = "curr_id", required = false) String currId,
                          @RequestParam(value = "offset") Integer offset,
                          @RequestParam(value = "count") Integer count) {
        if (Strings.isNullOrEmpty(card)) {
            return SearchException.MISS_CARD.get();
        }
        DataItem data = historicalTradeService.ddhistSearch(card, branchId, currId, offset, count);
        return Wrapper.OKBuilder.data(data).build();
    }

    @RequestMapping(value = "/union", method = RequestMethod.GET)
    @ResponseBody
    public Wrapper search(@RequestParam(value = "tracct") String tracct,
                          @RequestParam(value = "trctyp", required = false) String trctyp,
                          @RequestParam(value = "trsobr", required = false) String trsobr,
                          @RequestParam(value = "tx_dt_begin", required = false) Long txDtBegin,
                          @RequestParam(value = "tx_dt_end", required = false) Long txDtEnd,
                          @RequestParam(value = "offset") Integer from,
                          @RequestParam(value = "count") Integer size) {
        if (Strings.isNullOrEmpty(tracct)) {
            return SearchException.MISS_ACCTNO.get();
        }
        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(txDtBegin);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(txDtEnd);
        String trBegin = begin.get(Calendar.YEAR) + "" + df.format(begin.get(Calendar.DAY_OF_YEAR));
        String trEnd = end.get(Calendar.YEAR) + "" + df.format(end.get(Calendar.DAY_OF_YEAR));
        DataItem data = historicalTradeService.union(tracct, trctyp, trsobr, trBegin, trEnd, from, size);
        return Wrapper.OKBuilder.data(data).build();
    }

}
