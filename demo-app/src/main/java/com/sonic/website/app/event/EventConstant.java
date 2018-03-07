package com.sonic.website.app.event;

import java.nio.charset.Charset;

public interface EventConstant {
	static final Charset UTF_8 = Charset.forName("UTF-8");
	static final byte[] EVENT_FIELD = "event: ".getBytes(UTF_8);
	static final byte[] DATA_FIELD = "data: ".getBytes(UTF_8);
	static final byte[] COMMENT_FIELD = ": ".getBytes(UTF_8);
	static final byte[] CRLF = new byte[] { '\r', '\n' };
}
