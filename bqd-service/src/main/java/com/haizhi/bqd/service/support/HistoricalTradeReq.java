package com.haizhi.bqd.service.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chenbo on 17/4/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricalTradeReq {
    /**
     * 账号
     */
    String tracct;

    /**
     * 交易类型 结算（ D ）/储蓄（ S ）
     */
    String tratype;

    /**
     * 支行号/机构
     */
    String trbr;

    /**
     * 币种
     */
    String trctype;

    /**
     * 交易日期端(start -> end)
     */
    String trBeginDate;

    String trEndDate;

    /**
     * 顺序号
     */
    String seq;

    Integer from;

    Integer size;
}
