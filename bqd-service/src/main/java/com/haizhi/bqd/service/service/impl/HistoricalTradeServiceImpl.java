package com.haizhi.bqd.service.service.impl;

import com.google.common.collect.Lists;
import com.haizhi.bqd.service.model.DataItem;
import com.haizhi.bqd.service.service.HistoricalTradeService;
import com.haizhi.bqd.service.support.CustomerIndice;
import com.haizhi.bqd.service.support.ESQueryer;
import com.haizhi.bqd.service.support.HistoricalTradeReq;
import com.mysql.jdbc.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
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
        if(!StringUtils.isNullOrEmpty(req.getTracct())){
            queryBuilder.must(QueryBuilders.termQuery("TRACCT", req.getTracct()));
        }

        if(!StringUtils.isNullOrEmpty(req.getTratype())){
            queryBuilder.must(QueryBuilders.termQuery("TRATYP", req.getTratype()));
        }

        if(!StringUtils.isNullOrEmpty(req.getTrctype())){
            queryBuilder.must(QueryBuilders.termQuery("TRCTYPE", req.getTrctype()));
        }

        if(!(StringUtils.isNullOrEmpty(req.getTrBeginDate()) && StringUtils.isNullOrEmpty(req.getTrEndDate()))){
            queryBuilder.must(QueryBuilders.rangeQuery("TRDATE").from(req.getTrBeginDate()).to(req.getTrEndDate()));
        }

        if(!StringUtils.isNullOrEmpty(req.getSeq())){
            queryBuilder.must(QueryBuilders.termQuery("SEQ", req.getSeq()));
        }

        ESQueryer queryer = ESQueryer.builder()
                .client(client)
                .indice(CustomerIndice.POC)
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
}
