package com.haizhi.bqd.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chenbo on 16/6/15.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Wrapper {

    public static final WrapperBuilder OKBuilder = Wrapper.builder().status(0).msg("OK");
    public static final Wrapper OK = Wrapper.builder().status(0).msg("OK").build();

    public static final Wrapper ERROR = Wrapper.builder().status(-1).msg("ERROR").build();
    public static final WrapperBuilder ERRORBuilder = Wrapper.builder().status(-1);

    private int status;

    private String msg;

    private Object data;

    public Wrapper(int status, String msg) {
        this(status, msg, null);
    }

}