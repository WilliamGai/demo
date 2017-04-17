package com.sincetimes.website.core.common.support;

import java.util.function.Consumer;

/**
 * 已经被CloneableSupport代替
 * */
@Deprecated
public interface CloneableInterface<T extends CloneableInterface<?>> extends Cloneable{
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
		} catch (CloneNotSupportedException e) {
			LogCore.BASE.error("class {} clone err:",this.getClass().getSimpleName(), e);
		}
		return t;
	}
	/**子类填充{@code clone()}<br/>不要外部调用*/
	Object cloneThis() throws CloneNotSupportedException;
}

