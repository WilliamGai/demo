package com.sincetimes.website.core.common.serialize.interfaces;

import com.sincetimes.website.core.common.serialize.exception.SerializeException;

public interface Serializer<T> {

    byte[] serialize(T data) throws SerializeException;

    T deserialize(byte[] bytes) throws SerializeException;

}