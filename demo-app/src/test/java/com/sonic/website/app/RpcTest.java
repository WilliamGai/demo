package com.sonic.website.app;

import com.sonic.website.app.rpc.HelloRpcService;
import com.sonic.website.app.rpc.HelloRpcServiceImpl;
import com.sonic.website.app.rpc.RpcFramework;

public class RpcTest {  
    public static void main(String args[]){ 
        
        new Thread(()->testServer()).start();
        testClient();  
    }

    public static void testClient(){
        try {
            HelloRpcService service = RpcFramework.refer(HelloRpcService.class, "127.0.0.1", 1234);
         
            for (int i = 0; i < Integer.MAX_VALUE; i ++) {  
                String hello = service.hello("World" + i);  
                System.out.println(hello);  
                Thread.sleep(1000);  
            }
        }catch (Exception e) {
            e.printStackTrace();
        }  
    }

    public static void testServer(){
        HelloRpcService service = new HelloRpcServiceImpl();  
        try {
            RpcFramework.export(service, 1234);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}