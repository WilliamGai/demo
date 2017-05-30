package com.sincetimes.website.core.spring.interfaces;

import javax.servlet.http.HttpServletRequest;

import com.sincetimes.website.core.common.support.Sys;
import com.sincetimes.website.core.spring.manger.SpringManager;

public interface FilePathSupport {
	/***
	 * 获得静态文件路径,如果是Window则取运行路径。如果是linux服务器则必须是静态文件的路径
	 */
	default String getRootFilePath(HttpServletRequest req){
		if(Sys.isLinux()){
			return SpringManager.inst().upload_path;
		}
		return req.getServletContext().getRealPath("/");
	}
}	
