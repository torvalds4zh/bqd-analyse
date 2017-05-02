package com.haizhi.bqd.service.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Created by chenbo on 17/4/13.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ESQueryer {
    private Client client;

    private Indice indice;

    //结果集起始下标
    private Integer from;

    //返回结果数
    private Integer size;

    private Integer windowSize;

    private QueryBuilder queryBuilder;

    private List<AggregationBuilder> aggregationBuilders;

//    private RescoreBuilder.Rescorer rescorer;

    private List<String> fetchFields;

    private static Integer DEFAULT_RESULT_SIZE = 100;
    private static Integer DEFAULT_RESULT_FROM = 0;
    private static Integer DEFAULT_WINDOW_SIZE = 500;
    private static String DEFAULT_FETCH_FIELD = "_source";

    public SearchResponse actionGet() {

        if (indice == null) {
            throw new IllegalArgumentException("indice must be provided!");
        }

        SearchRequestBuilder requestBuilder = client.
                prepareSearch(this.indice.indice()).setTypes(this.indice.type());

        requestBuilder.setFrom(from == null ? DEFAULT_RESULT_FROM : from);
        requestBuilder.setSize(size == null ? DEFAULT_RESULT_SIZE : size);

        if (queryBuilder != null) {
            requestBuilder.setQuery(queryBuilder);
        }

        if (aggregationBuilders != null) {
            for (AggregationBuilder agg : aggregationBuilders) {
                requestBuilder.addAggregation(agg);
            }
        }

//        if (rescorer != null) {
//            if (windowSize == null) {
//                windowSize = DEFAULT_WINDOW_SIZE;
//            }
//            requestBuilder.setRescorer(rescorer, windowSize);
//        }

        if (fetchFields == null || fetchFields.size() == 0) {
            this.fetchFields = Collections.singletonList(DEFAULT_FETCH_FIELD);
        }

//        requestBuilder = requestBuilder.addHighlightedField(new HighlightBuilder.Field("*").requireFieldMatch(true))
//                .setHighlighterEncoder("html")
//                .addFields((String[]) fetchFields.toArray(new String[fetchFields.size()]));
        return requestBuilder.execute().actionGet();
    }

}
