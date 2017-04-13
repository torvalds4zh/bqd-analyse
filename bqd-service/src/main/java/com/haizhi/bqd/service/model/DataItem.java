package com.haizhi.bqd.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chenbo on 17/4/13.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataItem {
    Object data;

    Long total;
}
