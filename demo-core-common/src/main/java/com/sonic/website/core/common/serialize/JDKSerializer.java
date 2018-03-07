package com.sonic.website.core.common.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sonic.website.core.common.serialize.exception.SerializeException;
import com.sonic.website.core.common.serialize.interfaces.Serializer;


public class JDKSerializer implements Serializer<Object>{
    public Object deserialize(byte[] bytes) throws SerializeException {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object object = inputStream.readObject();
            return object;
        } catch (ClassNotFoundException e) {
            throw new SerializeException("Unable to find object class.", e);
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public byte[] serialize(Object serializable) throws SerializeException {
        try {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteArrayOS);
            stream.writeObject(serializable);
            stream.close();//objectOutputStream.flush();如果接口不是返回byte[]而是参数为 OutputStream只需要flush
            return byteArrayOS.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }
}
