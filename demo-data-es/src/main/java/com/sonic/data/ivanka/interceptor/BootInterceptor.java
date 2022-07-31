package com.sonic.data.ivanka.interceptor;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sonic.data.ivanka.stats.StatsManager;
import com.sonic.website.core.common.port.PortInstance;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.spring.HttpHeadUtil;

/***
 * 拦截器,单例
 */
public class BootInterceptor implements HandlerInterceptor {
    private static final String BEGIN_NAO_TIME_TAG = "begin_nao_time";
    private final AtomicLong requestId = new AtomicLong();// 计数器

    // 1
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2) {
        long beginNaoTime = System.nanoTime();
        if (req.getRequestURI().contains("error")) {
            LogCore.BASE.error("{}-------------------get one error request! {},arg2={}, referer={}", req.getRequestURI(), arg2, req.getHeader("referer"));
            return true;
        }
//        if (LogCore.BASE.isDebugEnabled()) {
//            LogCore.BASE.debug("{}----------------begin,ip={},req params:{},Origin={},all headers={}", req.getRequestURI(),
//                    req.getRemotePort(), HttpHeadUtil.getParamsMapLimit(req), req.getHeader("Origin"), HttpHeadUtil.getReqHeads(req));
//        }
        req.setAttribute(BEGIN_NAO_TIME_TAG, beginNaoTime);
        LogCore.BASE.debug("{}--------------begin req,Uri= {}", this.hashCode(), req.getRequestURI());
        return true;
    }

    /*
     * 3 会加上下面三个response头 Content-Type=application/json;charset=UTF-8,
     * Content-Length=30, Date=Thu, 02 Jun 2016 07:42:33 GMT}
     */
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object arg2, Exception arg3)
            throws Exception {
        String uri = req.getRequestURI();
        Object beginNaoTimeStr = req.getAttribute(BEGIN_NAO_TIME_TAG);
        if(null == beginNaoTimeStr){
            LogCore.BASE.warn("{} get null,referer:{}, the uri looks useless " ,uri, req.getHeader("referer"));
            return;
        }
        long beginNaoTime = (Long)beginNaoTimeStr ;
         long interval = System.nanoTime() - beginNaoTime;
        
        LogCore.BASE.debug("{}:  {}==========={}=========end,id={},params:{}, from:{},e:{}",this.hashCode(), uri,
                interval / 1000000, requestId.getAndIncrement(), HttpHeadUtil.getParamsMapLimit(req), req.getRemotePort(), arg3);
        statEvent(uri, interval);
    }

    /* 2 */
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2, ModelAndView arg3)
            throws Exception {
    }
    
    /** 记录时间消耗,放入队列处理*/
    private void statEvent(String uri, long interval) {
        PortInstance.inst().addQueue((p)->{
            StatsManager.inst().statsUri(uri, interval);
            StatsManager.inst().statsUriByDb(uri, interval);
        });
    }
}
