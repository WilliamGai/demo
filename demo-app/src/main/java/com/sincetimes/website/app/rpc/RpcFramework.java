package com.sincetimes.website.app.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.threadpool.ThreadPoolTool;

public class RpcFramework {
    /**
     * 暴露服务
     * 
     * @param service 服务实现
     * @param port 服务端口
     * @throws Exception 2017年2月22日13:33:02 TODO: 线程池
     */
    public static void export(final Object service, int port) throws Exception {
	if (service == null)
	    throw new IllegalArgumentException("service instance == null");
	if (port <= 0 || port > 65535)
	    throw new IllegalArgumentException("Invalid port " + port);
	LogCore.RPC.info("Export service={}, port={}", service.getClass().getName(), port);
	@SuppressWarnings("resource")
	ServerSocket server = new ServerSocket(port);
	while (true) {// for (;;)
	    /*
	     * 阻塞,直到有个链接连过来,这里连上后又阻塞等待链接,所以是个1对n的服务。 调用这方法的函数会被阻塞,这个方法里的线程是为了实现同时1对多,也因此server.accept()不能放在try-resource块里,那样会提前关闭socket
	     */
	    Socket socket = server.accept();
	    try {
		ThreadPoolTool.execute(() -> {
		    try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
			String methodName = input.readUTF();
			Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
			Object[] arguments = (Object[]) input.readObject();
			try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());) {
			    Method method = service.getClass().getMethod(methodName, parameterTypes);
			    Object result = method.invoke(service, arguments);
			    output.writeObject(result);
			} catch (Throwable t) {
			    LogCore.RPC.error("rpc framework output err", t);
			}
		    } catch (Throwable t) {
			LogCore.RPC.error("rpc framework err", t);
		    }
		});
		// thread.setName("RpcFramework export service:" + service.getClass().getSimpleName());
		// thread.start();
	    } catch (Exception e) {
		LogCore.RPC.error("rpc framework socket err", e);
	    }
	}
    }

    /**
     * 引用服务
     * 
     * @param <T> 接口泛型
     * @param interfaceClass 接口类型
     * @param host 服务器主机名
     * @param port 服务器端口
     * @return 远程服务
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) {
	if (interfaceClass == null)
	    throw new IllegalArgumentException("Interface class == null");
	if (!interfaceClass.isInterface())
	    throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
	if (host == null || host.length() == 0)
	    throw new IllegalArgumentException("Host == null!");
	if (port <= 0 || port > 65535)
	    throw new IllegalArgumentException("Invalid port " + port);
	LogCore.RPC.info("Get remote service {} from server {}:{}", interfaceClass.getName(), host, port);
	return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
		new InvocationHandler() {
		    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
			try (Socket socket = new Socket(host, port)) {
			    try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
				output.writeUTF(method.getName());
				output.writeObject(method.getParameterTypes());
				output.writeObject(arguments);
				try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
				    Object result = input.readObject();
				    if (result instanceof Throwable) {
					throw (Throwable) result;
				    }
				    return result;
				}
			    }
			}
		    }
		});
    }

}