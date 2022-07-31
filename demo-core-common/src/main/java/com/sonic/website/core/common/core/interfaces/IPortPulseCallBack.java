package com.sonic.website.core.common.core.interfaces;

import com.sonic.website.core.common.core.Port;
/** 不适用队列的处理   */
@FunctionalInterface
@Deprecated
public interface IPortPulseCallBack<T> {
    T doInPort(Port port);
}