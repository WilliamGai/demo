package com.sonic.website.core.common.serialize.exception;

import com.sonic.website.core.common.support.Util;

public class SerializeException extends RuntimeException {

    private static final long serialVersionUID = -1794106024265600424L;

    public SerializeException(String str) {
    super(str);
    }

    public SerializeException(Throwable e) {
    super(e);
    }

    public SerializeException(Throwable e, String str) {
    super(str, e);
    }

    public SerializeException(String str, Object... params) {
    super(Util.format(str, params));
    }

    public SerializeException(Throwable e, String str, Object... params) {
    super(Util.format(str, params), e);
    }
}
