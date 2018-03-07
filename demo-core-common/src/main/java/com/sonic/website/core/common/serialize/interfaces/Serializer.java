package com.sonic.website.core.common.serialize.interfaces;

import com.sonic.website.core.common.serialize.exception.SerializeException;

public interface Serializer<T> {

    byte[] serialize(T data) throws SerializeException;

    T deserialize(byte[] bytes) throws SerializeException;

}