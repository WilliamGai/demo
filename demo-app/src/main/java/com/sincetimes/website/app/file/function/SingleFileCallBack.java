package com.sincetimes.website.app.file.function;

import org.springframework.web.multipart.MultipartFile;
/** 推荐！ */
@FunctionalInterface
public interface SingleFileCallBack<T> {
	T doInFile(MultipartFile m);
}