package com.sincetimes.website.app.file.function;

import org.springframework.web.multipart.MultipartFile;
/**
 * 处理上传文件, 此接口与Consumer一样
 */
@FunctionalInterface
public interface FileConsumer {
	void consumeFile(MultipartFile m);
}