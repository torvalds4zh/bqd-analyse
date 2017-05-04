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
    public DataItem accountDetails(String card, String trbr, String trctype, String trBeginDate, String trEndDate,
                                   Integer from, Integer size) {
        List<Map<String, Object>> result = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(card)) {
            queryBuilder.must(QueryBuilders.termQuery("EI1CARD", card));
        }
        if (!Strings.isNullOrEmpty(trbr)) {
            queryBuilder.must(QueryBuilders.termQuery("TRBR", trbr));
        }

        if (!Strings.isNullOrEmpty(trctype)) {
            queryBuilder.must(QueryBuilders.termQuery("TRCTYP", trctype));
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
    public DataItem ddhistSearch(String card) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(card)) {
            queryBuilder.must(QueryBuilders.termQuery("ei1card", card));
        }

        //先查与该卡号相关的流水账号
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.POC_SIBS_EBCRDI)
                .queryBuilder(queryBuilder)
                .build();
        SearchResponse response = queryer.actionGet();

        log.info(" query {} took {} ms.", queryBuilder, response.getTook().getMillis());
        List<String> traccs = Lists.newArrayList();
        if (response != null && response.getHits() != null) {
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSource();
                if (source.get("ei1acn") != null && !Strings.isNullOrEmpty(source.get("ei1acn").toString())) {
                    traccs.add(source.get("ei1acn").toString());
                }
            }
        }

        //再查这些流水账号的交易记录
        if (traccs.size() > 0) {
            List<Map<String, Object>> result = Lists.newArrayList();

            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for (String tracc : traccs) {
                if (!Strings.isNullOrEmpty(tracc)) {
                    query.should(QueryBuilders.termQuery("tracct", tracc));
                }
            }

            ESQueryer esQueryer = ESQueryer.builder()
                    .client(client)
                    .indice(CustomerIndice.POC_SIBS_DDDHIS)
                    .queryBuilder(query)
                    .build();
            SearchResponse searchResponse = esQueryer.actionGet();
            log.info(" query {} took {} ms.", query, response.getTook().getMillis());

            if (searchResponse != null && searchResponse.getHits() != null) {
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    result.add(hit.getSource());
                }
            }
            return DataItem.builder().data(result).total(response.getHits().getTotalHits()).build();
        } else {
            return new DataItem();
        }
    }

    @Override
    public DataItem union(String tracct, String trctype, String trsobr,
                          String txDtBegin, String txDtEnd, Integer from, Integer size) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!Strings.isNullOrEmpty(tracct)) {
            queryBuilder.must(QueryBuilders.termQuery("tracct", tracct));
        }
        if (!Strings.isNullOrEmpty(trctype)) {
            queryBuilder.must(QueryBuilders.termQuery("trctyp", trctype));
        }
        if (!Strings.isNullOrEmpty(trsobr)) {
            queryBuilder.must(QueryBuilders.termQuery("trsobr", trsobr));
        }
        if (!Strings.isNullOrEmpty(txDtBegin) && !Strings.isNullOrEmpty(txDtEnd)) {
            queryBuilder.must(QueryBuilders.rangeQuery("tx_dt").from(txDtBegin).to(txDtEnd));
        }

        //先查与该卡号相关的流水账号
        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.POC_SIBS_TOTAL)
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
