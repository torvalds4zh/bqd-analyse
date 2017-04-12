package com.haizhi.bqd.web.rest.controller;

import com.haizhi.bqd.common.Wrapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chenbo on 17/4/6.
 */
@Controller
@RequestMapping("/ping")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Wrapper test(){
        return Wrapper.OKBuilder.data("pong").build();
    }
}
