package com.sonic.website.app.file.function;

import org.springframework.web.multipart.MultipartFile;
/** 推荐！ */
@FunctionalInterface
public interface FileCallBack<T> {
	T doInFile(MultipartFile m);
}