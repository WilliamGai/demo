package com.sincetimes.website.app.rpc;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;

import com.sincetimes.website.app.rpc.dubbo.DubboManagerTest;
import com.sincetimes.website.core.common.manager.annotation.ManangerOrder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.controller.ControllerInterface;
@RestController
@Order(value = 5)
public class RpcControllerTest implements ControllerInterface{
	public static final AtomicInteger count = new AtomicInteger(0);
	@RequestMapping("/rpc_test")
	Object  testRpc(HttpServletRequest req, HttpServletResponse resp
			,
			@RequestParam Optional<String> ip,
			@RequestParam Optional<Integer> port){
		try {
			HelloRpcService service = RpcManagerTest.inst().getService(HelloRpcService.class, ip.orElse("127.0.0.1"), port.orElse(10086));
		 
			return service.hello("World" + count.incrementAndGet());  
		}catch (Exception e) {
			LogCore.RPC.error("request rpc err", e);
			return e.getMessage();
		}  
	}
	@RequestMapping("/dubbo_test")
	Object  testDubbo(HttpServletRequest req, HttpServletResponse resp){
		return DubboManagerTest.inst().getRemoteServices();
	}
	
	@Autowired
	ApplicationContext context;
	@RequestMapping("/ctx_test")
	Object  testContext(HttpServletRequest req, HttpServletResponse resp){
		LogCore.BASE.info("context={}", context);
		Map<String, Object> orderManagers = context.getBeansWithAnnotation(ManangerOrder.class);
		return orderManagers;
	}
	@RequestMapping("/ctx_test2")
	Object  testContext2(HttpServletRequest req, HttpServletResponse resp){
		ApplicationContext context= ContextLoader.getCurrentWebApplicationContext();
		Map<String, Object> orderManagers = context.getBeansWithAnnotation(ManangerOrder.class);
		return orderManagers;
	}
}
