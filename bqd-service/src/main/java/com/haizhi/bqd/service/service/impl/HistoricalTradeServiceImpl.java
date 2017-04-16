package com.haizhi.bqd.service.service.impl;

import com.google.common.collect.Lists;
import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.service.HistoricalTradeService;
import com.haizhi.bqd.service.support.CustomerIndice;
import com.haizhi.bqd.service.support.ESQueryer;
import com.haizhi.bqd.service.support.HistoricalTradeReq;
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
    public DataItem search(HistoricalTradeReq req) {
        List<Map<String, Object>> result = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(req.getTracct())) {
            queryBuilder.must(QueryBuilders.termQuery("TRACCT", req.getTracct()));
        }

        if (!Strings.isNullOrEmpty(req.getTratype())) {
            queryBuilder.must(QueryBuilders.termQuery("TRATYP", req.getTratype()));
        }

        if (!Strings.isNullOrEmpty(req.getTrctype())) {
            queryBuilder.must(QueryBuilders.termQuery("TRCTYPE", req.getTrctype()));
        }

        if (!(Strings.isNullOrEmpty(req.getTrBeginDate()) && Strings.isNullOrEmpty(req.getTrEndDate()))) {
            queryBuilder.must(QueryBuilders.rangeQuery("TRDATE").from(req.getTrBeginDate()).to(req.getTrEndDate()));
        }

        if (!Strings.isNullOrEmpty(req.getSeq())) {
            queryBuilder.must(QueryBuilders.termQuery("SEQ", req.getSeq()));
        }

        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.POC_DDHIST)
                .from(req.getFrom())
                .size(req.getSize())
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query[{}] took {} ms.", req, response.getTook().getMillis());
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }
        return new DataItem();
    }

    @Override
    public DataItem coaSearch(String accId, Integer from, Integer size) {
        List<Map<String, Object>> result = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(accId)) {
            queryBuilder.must(QueryBuilders.wildcardQuery("COA_ID", String.format("*%s*", accId)));
        }
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.POC_PCOA)
                .from(from)
                .size(size)
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query accId {} took {} ms.", accId, response.getTook().getMillis());
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }
        return new DataItem();
    }

    @Override
    public DataItem accountDetails(String accountId, String entityId, String currId, Integer from, Integer size) {
        List<Map<String, Object>> result = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(accountId)) {
            queryBuilder.must(QueryBuilders.termQuery("GLAcc_ID", accountId));
        }
        if(!Strings.isNullOrEmpty(entityId)){
            queryBuilder.must(QueryBuilders.termQuery("Entity_ID", entityId));
        }

        if(!Strings.isNullOrEmpty(entityId)){
            queryBuilder.must(QueryBuilders.termQuery("Curr_ID", currId));
        }
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.POC_EBS_S1GL)
                .from(from)
                .size(size)
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query accountId {} took {} ms.", accountId, response.getTook().getMillis());
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(hit.getSource());
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        }

        return null;
    }
}
