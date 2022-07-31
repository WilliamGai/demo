package com.sonic.website.support;

/**
 * ①子类不能覆盖父类的方法 不可以把父类的protected改为private但是可以把父类的protected改为public
 * ②子类覆盖父类的方法不能扩大父类方法抛出的异常但是可以细化
 * @author bao
 * @date 2017年11月7日 下午5:23:59
 */
public class TestB extends TestA{
    @Override
    public void doSth() throws RuntimeException{
        return;
    }
}
