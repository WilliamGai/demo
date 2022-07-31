package com.sonic.website.core.common.extension.threadqueue;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadQueueInngerClassTest {

    private static AtomicInteger COUNT = new AtomicInteger();
    
    class ThreadA extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
        }
        
    }
    
    public void test() throws InterruptedException{
        LockThread thread = new LockThread("test", true);
        thread.start();
        
        int count = 200;
        for(int i=0; i<count; i++){
            final int _i =i;
            thread.execute(()->{System.out.println(_i); COUNT.getAndIncrement();});
            thread.execute(this::test2);
        }
        Thread.sleep(1000L);
        Thread.sleep(1000L);
        System.out.println("==="+thread.queues.size());
        System.out.println("==="+thread.queues.size());
        System.out.println("==="+COUNT);
        
    }
    
    public void test2(){
        System.out.println("===test2");
    }
    
    public static void main(String args[]) throws InterruptedException{
        new ThreadQueueInngerClassTest().test();
    }
}
