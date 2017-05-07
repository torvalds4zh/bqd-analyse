package com.haizhi.bqd.service.service.impl;

import com.google.common.collect.Lists;
import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.service.HistoricalTradeService;
import com.haizhi.bqd.service.support.CustomerIndice;
import com.haizhi.bqd.service.support.ESQueryer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.List;
import java.util.Map;

/**
 * Created by chenbo on 17/4/13.
 */
@Slf4j
public class HistoricalTradeServiceImpl implements HistoricalTradeService {
    @Setter
    Client client;

    @Override
    public DataItem search(String tracct, String trbr, String trctyp, String trBeginDate, String trEndDate,
                           Integer from, Integer size) {
        List<Map<String, Object>> result = Lists.newArrayList();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //账号
        if (!Strings.isNullOrEmpty(tracct)) {
            queryBuilder.must(QueryBuilders.termQuery("TRACCT", tracct));
        }

        //支行号/机构
        if (!Strings.isNullOrEmpty(trbr)) {
            queryBuilder.must(QueryBuilders.termQuery("TRBR", trbr));
        }

        //币种
        if (!Strings.isNullOrEmpty(trctyp)) {
            queryBuilder.must(QueryBuilders.termQuery("TRCTYP", trctyp));
        }

        if (!(Strings.isNullOrEmpty(trBeginDate) && Strings.isNullOrEmpty(trEndDate))) {
            queryBuilder.must(QueryBuilders.rangeQuery("TRBDT7").from(trBeginDate).to(trEndDate));
        }

        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.DDDHIS_DATAS)
                .from(from)
                .size(size)
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query[{}] took {} ms.", queryBuilder, response.getTook().getMillis());
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }
        return new DataItem();
    }

    @Override
    public DataItem accountDetails(String card, String trbr, String trctyp, String trBeginDate, String trEndDate,
                                   Integer from, Integer size) {
        List<Map<String, Object>> result = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(card)) {
            queryBuilder.must(QueryBuilders.termQuery("EI1CARD", card));
        }
        if (!Strings.isNullOrEmpty(trbr)) {
            queryBuilder.must(QueryBuilders.termQuery("TRBR", trbr));
        }

        if (!Strings.isNullOrEmpty(trctyp)) {
            queryBuilder.must(QueryBuilders.termQuery("TRCTYP", trctyp));
        }

        if (!(Strings.isNullOrEmpty(trBeginDate) && Strings.isNullOrEmpty(trEndDate))) {
            queryBuilder.must(QueryBuilders.rangeQuery("TRBDT7").from(trBeginDate).to(trEndDate));
        }
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.DDDHIS_CARD_ACCOUNT)
                .from(from)
                .size(size)
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query {} took {} ms.", queryBuilder, response.getTook().getMillis());
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }

        return null;
    }

    @Override
    public DataItem coaSearch(String accId, Integer from, Integer size) {
        List<Map<String, Object>> result = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(accId)) {
            queryBuilder.must(QueryBuilders.wildcardQuery("glaccId", String.format("*%s*", accId)));
        }
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.PCOAS_ACCOUNT)
                .from(from)
                .size(size)
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query {} took {} ms.", queryBuilder, response.getTook().getMillis());
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }
        return new DataItem();
    }

    @Override
    public DataItem ddhistSearch(String card, String branchId, String currId, Integer from, Integer size) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(card)) {
            queryBuilder.must(QueryBuilders.termQuery("glaccId", card));
        }

        if (!Strings.isNullOrEmpty(branchId)) {
            queryBuilder.must(QueryBuilders.termQuery("branchId", branchId));
        }

        if (!Strings.isNullOrEmpty(currId)) {
            queryBuilder.must(QueryBuilders.termQuery("currId", currId));
        }

        //先查与该卡号相关的流水账号
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.PCOAS_ACCOUNT)
                .queryBuilder(queryBuilder)
                .from(from)
                .size(size)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query {} took {} ms.", queryBuilder, response.getTook().getMillis());
        List<Map<String, Object>> result = Lists.newArrayList();
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }
        return new DataItem();
    }

    @Override
    public DataItem union(String tracct, String trctyp, String trsobr,
                          String txDtBegin, String txDtEnd, Integer from, Integer size) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(tracct)) {
            queryBuilder.must(QueryBuilders.termQuery("TrAcct", tracct));
        }
        if (!Strings.isNullOrEmpty(trctyp)) {
            queryBuilder.must(QueryBuilders.termQuery("TrCtyp", trctyp));
        }
        if (!Strings.isNullOrEmpty(trsobr)) {
            queryBuilder.must(QueryBuilders.termQuery("TrSobr", trsobr));
        }
        if (!Strings.isNullOrEmpty(txDtBegin) && !Strings.isNullOrEmpty(txDtEnd)) {
            queryBuilder.must(QueryBuilders.rangeQuery("TrDate").from(txDtBegin).to(txDtEnd));
        }

        //先查与该卡号相关的流水账号
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.TOTAL_ACCOUNT_ACCOUNT)
                .queryBuilder(queryBuilder)
                .from(from)
                .size(size)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query {} took {} ms.", queryBuilder, response.getTook().getMillis());
        List<Map<String, Object>> traccs = Lists.newArrayList();
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSource();
                traccs.add(source);
            }
            return DataItem.builder().data(traccs).total(response.getHits().getTotalHits()).build();
        }
        return new DataItem();
    }
}
