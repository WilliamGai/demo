package com.sincetimes.website.core.common.extension;

import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
/**
 * java api的确没有提供ConcurrentHashSet,Set在java的实现也的确是Map。
 * <br>
 * 而ConcurrentSkipListSet(有序,提供pollFirst),的是根据ConcurrentSkipListMap来实现的,但是我们可能只想要一个单一的线程安全的Set。
 * <br>
 * 那如何得到一个大的,线程安全的Set?
 * <br>
 * 其他两种方式:
 * <br>
 * 1.java1.8版本直接使用Set<String> words = ConcurrentHashMap.<String> newKeySet();(推荐)
 * <br>
 * 它其实是对ConcurrentHashMap<K,Boolean>对象的封装，所有的映射的值都是Boolean.TRUE
 * <br>
 * 2.谷歌guava也提供了Sets.newConcurrentHashSet()获得
 * @See {@link Sets#newConcurrentHashSet()}
 * @See {@link ConcurrentHashMap#newKeySet()}
 * @author BAO
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, java.io.Serializable {
	/**
	 * 2017年3月10日15:33:10
	 */
	private static final long serialVersionUID = -1541620009370526955L;

	private static final Object PRESENT = new Object();

	private final ConcurrentHashMap<E, Object> map;
	
	public ConcurrentHashSet(){
	    map = new ConcurrentHashMap<E, Object>();
	}

    public ConcurrentHashSet(int initialCapacity){
        map = new ConcurrentHashMap<E, Object>(initialCapacity);
    }

	/**
	 * Returns an iterator over the elements in this set. The elements are
	 * returned in no particular order.
	 * 
	 * @return an Iterator over the elements in this set
	 * @see ConcurrentModificationException
	 */
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 * 
	 * @return the number of elements in this set (its cardinality)
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 * 
	 * @return <tt>true</tt> if this set contains no elements
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element. More
	 * formally, returns <tt>true</tt> if and only if this set contains an
	 * element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested
	 * @return <tt>true</tt> if this set contains the specified element
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present. More
	 * formally, adds the specified element <tt>e</tt> to this set if this set
	 * contains no element <tt>e2</tt> such that
	 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this
	 * set already contains the element, the call leaves the set unchanged and
	 * returns <tt>false</tt>.
	 * 
	 * @param e
	 *            element to be added to this set
	 * @return <tt>true</tt> if this set did not already contain the specified
	 *         element
	 */
	public boolean add(E e) {
		return map.put(e, PRESENT) == null;
	}

	/**
	 * Removes the specified element from this set if it is present. More
	 * formally, removes an element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if this
	 * set contains such an element. Returns <tt>true</tt> if this set contained
	 * the element (or equivalently, if this set changed as a result of the
	 * call). (This set will not contain the element once the call returns.)
	 * 
	 * @param o
	 *            object to be removed from this set, if present
	 * @return <tt>true</tt> if the set contained the specified element
	 */
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after
	 * this call returns.
	 */
	public void clear() {
		map.clear();
	}

}