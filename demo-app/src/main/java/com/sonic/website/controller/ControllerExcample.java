package com.sonic.website.controller;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.website.app.stats.StatsManager;
import com.sonic.website.core.common.support.JSONBuilder;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.threadpool.ThreadUtil;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.core.spring.interfaces.AccessSupport;
import com.sonic.website.core.spring.manger.SpringManager;
import com.sonic.website.manager.DataManager;
/**
 * 参考示例
 */
@RestController
@Order(value = 5)
public class ControllerExcample implements AccessSupport {
    public static class LoginRequestVO implements Serializable{
        private static final long serialVersionUID = 1L;
        public String openid;
        public String sign;
    }
    
    @ResponseBody
    @RequestMapping("    esthttps")
    String testhttps(HttpServletRequest req, HttpServletResponse resp){
        ThreadUtil.sleep(10000000L);
        return "abc";
    }
    @ResponseBody
    @RequestMapping("    esthttps2")
    String testhttps2(HttpServletRequest req, HttpServletResponse resp){
        return "abc"+TimeUtil.getLocalTimeFull();
    }
    
    @RequestMapping("    stbody")
    public Object tstBody(@RequestBody LoginRequestVO test) {
        LogCore.BASE.info("testbody param={}", Util.prettyJsonStr(test));
        LogCore.BASE.info("result={}", JSONBuilder.creatJsonString("result", 1,"data", test));
        return JSONBuilder.creatJsonString("result", 1,"data", test);
    }
    @RequestMapping("    st")
    public Object get_value(HttpServletRequest req) {
        Map<String, String> map = HttpHeadUtil.getParamsMapLimit(req);
        return map;
    }
    
    @RequestMapping("/get_value")
    public Object get_value(String name) {
        String v = DataManager.inst().get(name);
        return JSONBuilder.creatJsonString("result", 1,"data", v);
    }
    
    @RequestMapping("/get_bivalue")
    public Object get_bivalue(String key) {
        String[] ss =  DataManager.inst().getBiValue(key);
        return JSONBuilder.creatJsonString("result", 1,"key",ss[0], "name",ss[1], "value",ss[2]);
    }

    @RequestMapping("/downloadhits/{os}")
    public String download(HttpServletRequest req, @PathVariable String os,String openid, String ip) {
        int real_ip = req.getRemotePort();
        LogCore.BASE.info("downloadhits openid={},realIp={},os={}",openid, real_ip, os);
        StatsManager.inst().incr(os);
        return os;
    }
    
    @ResponseBody
    @RequestMapping("/lookenv")
    Object lookenv(HttpServletRequest req, HttpServletResponse resp){
        return SpringManager.inst().getEnvironmentJsonStr();
    }
    @ResponseBody
    @RequestMapping("/lookproperty")
    Object lookenv(HttpServletRequest req, HttpServletResponse resp, String key){
        redirect(resp, "lookenv");
//        forward(req, resp, "lookenv");
        return null;
    }
}
