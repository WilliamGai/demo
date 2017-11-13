package proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
/***
 * 借用Spring自带的cgliib
 * @author bao
 * @date 2017年8月13日 下午11:27:01
 */
public class MethodInterceptorImpl implements MethodInterceptor {

	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("MethodInterceptorImpl:" + method.getName());
		return methodProxy.invokeSuper(o, objects);
	}
}