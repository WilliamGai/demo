package com.sincetimes.website.core.common.core.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sincetimes.website.core.common.support.SerializeTool;


/**
 * 分布式序列化接口
 * 除了基本类型 其余数据需要分布式传输必须实现本接口
 * 注：实现本接口的类必须有无参构造函数!
 * @see SerializeTool#write(Object, java.io.DataOutputStream)
 * @see SerializeTool#read(java.io.DataInputStream)
 */
public interface ISerilizable {
	public void writeTo(OutputStream out) throws IOException;
	public void readFrom(InputStream in) throws IOException;
}