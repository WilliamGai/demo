package com.sonic.website.app.stats.flow.builder;

import java.util.Arrays;

import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;

public class TextBuilder extends Builder<String> {
	private StringBuilder sb = new StringBuilder();
	private String title;
	private String author;

	@Override
	public Builder<String> setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public Builder<String> writeWords(String ...words) {
		if(Util.isEmpty(words)){
			return this;
		}
		Arrays.stream(words).forEach(this::append);
		return this;
	}
	
	@Override
	public Builder<String> writeLines(String... lines) {
		if(Util.isEmpty(lines)){
			return this;
		}
		Arrays.stream(lines).forEach(this::appendLine);
		return this;
	}
	
	@Override
	public String createContent() {
		StringBuilder content = new StringBuilder();
		if(!Util.isEmpty(title)){
			content.append(title);
			content.append("\t\n");
		}
		if(!Util.isEmpty(author)){
			content.append(author);
			content.append("\t\n");
		}
		content.append("create time:" + TimeUtil.getLocalTime());
		content.append("\t\n");
		return content.toString() + sb.toString();
	}
	private TextBuilder append(String word) {
		sb.append(word).append("    \t ");
		return this;
	}
	private TextBuilder appendLine(String line) {
		sb.append(line).append("\t\n");
		return this;
	}
	private TextBuilder appendLine() {
		sb.append("\t\n");
		return this;
	}

	@Override
	public Builder<String> writeLine() {
		return appendLine();
	}

	@Override
	public Builder<String> setAuthor(String author) {
		this.author = author;
		return this;
	}

	@Override
	public byte[] getByteArray() {
		return createContent().getBytes();
	}

	@Override
	public String getSuffixName() {
		return "txt";
	}
}
