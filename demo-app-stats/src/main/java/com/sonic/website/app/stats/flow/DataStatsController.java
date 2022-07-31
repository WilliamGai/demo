package com.sonic.website.app.stats.flow;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonic.website.app.security.interfaces.SecureControllerInterface;
@Controller
@Order(value = 5)
public class DataStatsController implements SecureControllerInterface {
    @RequestMapping("/tables")
    @ResponseBody
    public Object tables() {
        return DataStatsManager.inst().tables();
    }
    @RequestMapping("/dbs")
    @ResponseBody
    public Object dbs() {
        return DataStatsManager.inst().databases();
    }
    @RequestMapping("/tst")
    @ResponseBody
    public Object tstByTableName(String tableName) {
        return DataStatsManager.inst().tstByTableName(tableName);
    }
    @RequestMapping("/tstbyid")
    @ResponseBody
    public Object tst(String id) {
        return DataStatsManager.inst().tst(id);
    }
    
}
