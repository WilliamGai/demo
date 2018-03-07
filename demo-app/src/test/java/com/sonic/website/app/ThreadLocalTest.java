package com.sonic.website.app;

public class ThreadLocalTest {
	
	 /*1通过匿名内部类覆盖ThreadLocal的initialValue()方法，指定初始值  */
    private static ThreadLocal<Integer> seqNum = new ThreadLocal<Integer>() {  
        public Integer initialValue() {  
            return 0;  
        }  
    };  
  
    /*2获取下一个序列值  */
    public int getNextNum() {  
        seqNum.set(seqNum.get() + 1);  
        return seqNum.get();  
    }  
	public static void main(String args[]){
		System.out.println("ThreadLocalTest start");
		ThreadLocalTest sn = new ThreadLocalTest();  
        /*3个线程共享sn，各自产生序列号  */
        TestClient t1 = new TestClient(sn);  
        TestClient t2 = new TestClient(sn);  
        TestClient t3 = new TestClient(sn);  
        t1.start();  
        t2.start();  
        t3.start(); 
	}
	private static class TestClient extends Thread {  
        private ThreadLocalTest sn;  
  
        public TestClient(ThreadLocalTest sn) {  
            this.sn = sn;  
        }  
  
        public void run() {  
            for (int i = 0; i < 3; i++) {  
                /*4每个线程打出3个序列值  */
                System.out.println("thread[" + Thread.currentThread().getName() + "] --> sn["  
                         + sn.getNextNum() + "]");  
            }  
        }  
    }  
}
