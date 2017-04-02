package com.sincetimes.website.app.blog;

import java.io.File;
import java.io.Serializable;

import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.vo.JsonVOBase;

public class FileInfo extends JsonVOBase implements Serializable{
	private static final long serialVersionUID = -234490286367021892L;
	private String name;
	private String type;
	private String uri;
	private boolean isDir;
	private long size;//文件大小
	private transient String lastModifiedTimeStr;
	private long lastModifiedTime;
	
	public FileInfo(File f){
		if(f.isDirectory()){
			isDir = true;
			return;
		}
		name = f.getName();
		type = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
		size = f.length();
		lastModifiedTime = f.lastModified();
		lastModifiedTimeStr = TimeTool.getLocalTime();
		uri = f.getPath();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getLastModifiedTimeStr() {
		return lastModifiedTimeStr;
	}

	public void setLastModifiedTimeStr(String lastModifiedTimeStr) {
		this.lastModifiedTimeStr = lastModifiedTimeStr;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
