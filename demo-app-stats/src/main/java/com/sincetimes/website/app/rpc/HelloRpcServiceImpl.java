package com.sincetimes.website.app.rpc;

import com.sincetimes.website.core.common.support.Util;

public class HelloRpcServiceImpl implements HelloRpcService {
    public String hello(String name) {  
    	return Util.format("os={},path={},msg={}", System.getProperty("os.name"), System.getProperty("user.dir"), name);
    }  
}  