package com.sonic.website.core.common.support;

import java.util.function.Consumer;

/**
 * gof.6:Prototype<br>
 * @since jse version >=1.8
 * @author BAO
 */
public interface CloneableSupport<T extends CloneableSupport<?>> extends Cloneable{
	/**对克隆的对象进行操作,推荐*/
	public default T createClone(Consumer<T> consumer){
		T t = createClone();
		consumer.accept(t);
		return t;
	}
	/**推荐*/
	@SuppressWarnings("unchecked")
	public default T createClone(){
		T t = null;
		try {
			t = (T)cloneThis();
			t.afterInit();
		} catch (CloneNotSupportedException e) {
			LogCore.BASE.error("class {} clone err:",this.getClass().getSimpleName(), e);
		}
		return t;
	}
	/** 子类填充{@code clone()}即可<br/>不需要外部调用*/
	Object cloneThis() throws CloneNotSupportedException;
	/** {@link Object#clone}是浅拷贝 ,可以在在这里写拷贝细节*/
	T afterInit();
}

