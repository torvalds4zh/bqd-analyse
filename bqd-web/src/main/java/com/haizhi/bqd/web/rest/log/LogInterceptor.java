package com.haizhi.bqd.web.rest.log;

import com.haizhi.bqd.web.rest.security.UserDetails;
import com.haizhi.bqd.web.rest.util.RequestUtil;
import com.haizhi.bqd.web.rest.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

/**
 * Created by chenbo on 17/4/6.
 */
@Slf4j
public class LogInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("_startTime", startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //记录方法耗时
        long startTime = (Long) request.getAttribute("_startTime");
        UserDetails currentUser = SecurityUtil.getCurrentUser();
        if(currentUser == null){
            return;
        }
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        //userId:{0},username:{1},url:{2},method:{3},consume:{4},time:{5},ip:{6},agent:{7}
        String agent = request.getHeader("User-Agent");
        String ip = RequestUtil.getRemoteHost(request);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestMapping classMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
        RequestMapping methodMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        String template = "{0}:{1}:{2}:{3}:{4}:{5}:{6}:{7}";
        boolean v_c = classMapping != null && classMapping.value() != null && classMapping.value().length > 0;
        boolean v_m = methodMapping.value() != null && methodMapping.value().length > 0;
        boolean m_m = methodMapping.method() != null && methodMapping.method().length > 0;
        if (v_m && m_m && v_c) {
            String url = classMapping.value()[0].toString() + "/" + methodMapping.value()[0];
            String method = methodMapping.method()[0].toString();
            Object[] param = new Object[]{currentUser.getId(), currentUser.getUsername(), url, method, executeTime + "", startTime + "", ip, agent};
            String msg = MessageFormat.format(template, param);
            log.info(msg);
        }
    }
}
