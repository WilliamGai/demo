package com.sincetimes.website.core.common.core;

import com.sincetimes.website.core.common.core.interfaces.IPortPulse;
import com.sincetimes.website.core.common.support.Param;
/**
 * 此类增加了上下文
 */
public abstract class PortPulseBase implements IPortPulse{
		//上下文环境
		public final Param param;
		
		/** 构造函数*/
		public PortPulseBase() {
			this.param = new Param();
		}
		
		/** 构造函数 可传递上下文参数 */
		public PortPulseBase(Object...params) {
			this.param = new Param(params);
		}
}