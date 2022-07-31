package com.sonic.website.core.common.core.interfaces;

import com.sonic.website.core.common.core.Port;
/**
 * 主要代替PortPulseBase, 用来指向没有上下文的函数,这样书写简便
 */
@FunctionalInterface
public interface IPortPulse {
    /** 任务执行 */
    public abstract void execute(Port port);
}