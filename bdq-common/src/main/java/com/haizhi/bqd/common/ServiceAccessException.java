package com.haizhi.bqd.common;

import lombok.Data;

@Data
public class ServiceAccessException extends RuntimeException implements WrapperProvider {

    private Wrapper wrapper;

    public ServiceAccessException(int status, String msg) {
        this(Wrapper.builder().status(status).msg(msg).build());
    }

    public ServiceAccessException(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public ServiceAccessException(WrapperProvider provider) {
        this.wrapper = provider.get();
    }

    @Override
    public Wrapper get() {
        return wrapper;
    }
}