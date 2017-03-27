package com.sincetimes.website.core.common.core.function;

public class CommonFunction {
	private int paramCount = 0;
	private Object function;
	public CommonFunction(Object function, int paramCount){
		this.paramCount = paramCount;
		this.function = function;
	}
	public int getParamCount(){
		return this.paramCount;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void apply(Object ... m){
		assert(m.length == this.paramCount);
		Object f = this.function;
		switch (m.length) {
			case 0: ((GofFunction0) f).apply(); break;
			case 1: ((GofFunction1) f).apply(m[0]); break;
			case 2: ((GofFunction2) f).apply(m[0], m[1]); break;
			case 3: ((GofFunction3) f).apply(m[0], m[1], m[2]); break;
			case 4: ((GofFunction4) f).apply(m[0], m[1], m[2], m[3]); break;
			case 5: ((GofFunction5) f).apply(m[0], m[1], m[2], m[3], m[4]); break;
			case 6: ((GofFunction6) f).apply(m[0], m[1], m[2], m[3], m[4], m[5]); break;
			case 7: ((GofFunction7) f).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6]); break;
			case 8: ((GofFunction8) f).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7]); break;
			case 9: ((GofFunction9) f).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8]); break;
			default: break;
		}
	}
}
