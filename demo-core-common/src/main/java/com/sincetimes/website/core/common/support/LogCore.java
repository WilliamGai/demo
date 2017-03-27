package com.sincetimes.website.core.common.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCore{
	public static Logger  BASE = LoggerFactory.getLogger(LogCore.class);
	public static Logger  USER = LoggerFactory.getLogger("USER");
	public static Logger  RECORD = LoggerFactory.getLogger("RECORD");
	public static Logger  CORE = LoggerFactory.getLogger("CORE");
	public static Logger  RPC = LoggerFactory.getLogger("RPC");
}
