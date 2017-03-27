package com.sincetimes.website.app.stats;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.Util;

public class Record {
	protected String sn;
	protected long time;//登记时间戳

	public Record() {
	}
	public Record(String sn, long time) {
		this.sn = sn;
		this.time = time;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public static Record getRecord(byte[] data) {
		return JSON.parseObject(Util.getUtf(data), Record.class);
	}
}

