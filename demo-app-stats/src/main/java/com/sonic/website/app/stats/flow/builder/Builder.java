package com.sonic.website.app.stats.flow.builder;
/**
 * 并不存储文件只返回文件的二进制流
 * @param <T>
 */
public abstract class Builder<T> {
    public abstract Builder<T> setTitle(String title);
    public abstract Builder<T> setAuthor(String author);
    public abstract Builder<T> writeWords(String ...words);
    public abstract Builder<T> writeLine();
    public abstract Builder<T> writeLines(String ...lines);
    public abstract T createContent();
    public abstract byte[] getByteArray();
    public abstract String getSuffixName();
}
