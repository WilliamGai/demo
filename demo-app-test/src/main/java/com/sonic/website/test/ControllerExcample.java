package com.sonic.website.test;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.website.core.common.support.JSONBuilder;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.threadpool.LimitedThreadPool;
import com.sonic.website.core.common.threadpool.ThreadUtil;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.core.spring.interfaces.AccessSupport;
import com.sonic.website.core.spring.manger.SpringManager;

/**
 * 参考示例
 */
@RestController
@Order(value = 5)
public class ControllerExcample implements AccessSupport {
	@Autowired
	private Task task;
	@Autowired
	private TaskAsy taskasy;

	public static class LoginRequestVO implements Serializable {
		private static final long serialVersionUID = 1L;
		public String openid;
		public String sign;
	}

	@RequestMapping("/tstbody")
	public Object tstBody(@RequestBody LoginRequestVO test) {
		LogCore.BASE.info("testbody param={}", Util.prettyJsonStr(test));
		LogCore.BASE.info("result={}", JSONBuilder.creatJsonString("result", 1, "data", test));
		return JSONBuilder.creatJsonString("result", 1, "data", test);
	}

//	@Async
	@RequestMapping("/tst")
	public Object get_value(HttpServletRequest req) {
		Map<String, String> map = HttpHeadUtil.getParamsMapLimit(req);
		ThreadUtil.sleep(10);
        LogCore.BASE.info("controller Uri= {},{},{}, {}, reqId= {}", req.getRequestURI(), Thread.currentThread().getName(), Thread.currentThread().getId(), req.getAttribute("p_req_id"), HttpHeadUtil.getParamsMapLimit(req));
        return map;
	}
	@RequestMapping("/tst2")
	public Callable<String>  tst2(HttpServletRequest req) {
		return new Callable<String>() {

			@Override
			public String call() throws Exception {
				ThreadUtil.sleep(10);
				LogCore.BASE.info("controller Uri= {},{},{}, {}, reqId= {}", req.getRequestURI(), Thread.currentThread().getName(), Thread.currentThread().getId(), req.getAttribute("p_req_id"), HttpHeadUtil.getParamsMapLimit(req));
				return "enen";
			}
			
		};
		
	}

	@RequestMapping("/lookenv")
	Object lookenv(HttpServletRequest req, HttpServletResponse resp) {
		return SpringManager.inst().getEnvironmentJsonStr();
	}

	@RequestMapping("/tstsync")
	public Object tstasy(HttpServletRequest req) {
//		Map<String, String> map = HttpHeadUtil.getParamsMapLimit(req);
		final AtomicLong sum = new AtomicLong();
		CountDownLatch latch = new CountDownLatch(3);
		long start = System.currentTimeMillis();
		LimitedThreadPool.execute(() -> {
			sum.addAndGet(task.doTaskOne());
			latch.countDown();
		});// 为什么lambda表达式必须得是final的， 为什么必须在里面try cath
		LimitedThreadPool.execute(() -> {
			sum.addAndGet(task.doTaskTwo());
			latch.countDown();
		});// 为什么lambda表达式必须得是final的， 为什么必须在里面try cath
		LimitedThreadPool.execute(() -> {
			sum.addAndGet(task.doTaskThree());
			latch.countDown();
		});// 为什么lambda表达式必须得是final的， 为什么必须在里面try cath
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return sum + "," + (System.currentTimeMillis() - start);
	}

	@RequestMapping("/tstasy")
	public Object tstasy1(HttpServletRequest req) {
//		Map<String, String> map = HttpHeadUtil.getParamsMapLimit(req);
		Long sum = -1L;
		try {
			Future<Long> task1 = taskasy.doTaskOne();
			Future<Long> task2 = taskasy.doTaskTwo();
			Future<Long> task3 = taskasy.doTaskThree();
//			task3.get(timeout, unit)
			return task1.get() + task2.get() + task3.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sum;
	}

}
