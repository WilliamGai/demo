package com.sonic.website.app.rpc.dubbo;
/*package com.sincetimes.website.app.rpc.dubbo;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerOrder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.manger.SpringManager;
*//**
 * dubbo消费示例
 *//*
@ManangerOrder(value=Integer.MAX_VALUE/2+1)
@Component
public class DubboManagerTest extends ManagerBase {
	
	public static DubboManagerTest inst() {
		return ManagerBase.inst(DubboManagerTest.class);
	}
	
	@Reference(version="1.0.1")
	IHelloDubboService helloService;
	
	
	@Override
	public void init() {
		Map<String, Object> remoteServices = SpringManager.inst().getApplicationContext().getBeansWithAnnotation(Reference.class);
		Map<String, Object> orderManagers = SpringManager.inst().getApplicationContext().getBeansWithAnnotation(ManangerOrder.class);
		LogCore.RPC.info("dubbo services:{}", remoteServices);
		LogCore.RPC.info("orderManagers :{}", orderManagers);
	    LogCore.RPC.info("dubbo helloService:{},test:{}", helloService, helloService.sayHello());
	}
	public Map<String, Object> getRemoteServices(){
		Map<String, Object> remoteServices = SpringManager.inst().getApplicationContext().getBeansWithAnnotation(Reference.class);
		Map<String, Object> orderManagers = SpringManager.inst().getApplicationContext().getBeansWithAnnotation(ManangerOrder.class);
		LogCore.RPC.info("dubbo services:{}", remoteServices);
		LogCore.RPC.info("orderManagers :{}", orderManagers);
	    LogCore.RPC.info("dubbo helloService:{},test:{}", helloService, helloService.sayHello());
	    return remoteServices;
	}
}
*/