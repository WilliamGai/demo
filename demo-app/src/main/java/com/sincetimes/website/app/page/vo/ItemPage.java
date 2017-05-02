package com.sincetimes.website.app.page.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.sincetimes.website.core.common.support.CloneableSupport;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.common.vo.VOBase;
/**
 * 模板以及模板生成的页面
 * 都是从json获取
 * 对应的redis的结构:
 * <pre>{@code
 * --------------最简单的存储-----------------------------------------
 * 
 *       使用hmget(id, 'page', 'visits'); 时间复杂度O(2):
 *       dict<id, hash>
 *              /  |
 *             /  ItemPage
 *            /  
 *           /   
 *        visits
 *      
 * ---------------要实现可以单独查看页面的某几个属性的存储来节省带宽------------------
 *  
 *    使用hgetall时间复杂度O(N):
 *           dict<id, hash>
 *                     /
 *                    /
 *              ItemPage(hash)
 *            /    /    |   \  \  
 *    page.name   |     |    |  ...
 *           page.visits|   page.items.key1
 *                      |
 *                 page.items.key2
 *}              
 *</pre>
 * @see ItemPageProvider#saveOrUpdateItemPage
 */
public class ItemPage extends VOBase implements CloneableSupport<ItemPage>{
	private String id;		//用户指定,unique
	private String name;	//用户指定
	private transient long visits;   //阅读次数,hash的一个值,ItemPage反序列化后恢复
	private Map<String, Item> items = new HashMap<>();
	 
	public ItemPage() {
		super();
	}
	public ItemPage(String id, String name) {
		if(Util.isEmpty(id)){
			return;
		}
		this.id = id;
		this.name = Objects.toString(name, "undifined");
	}
	public ItemPage(Map<String, Item> items) {
		if(Util.isEmpty(items)){
			return;
		}
		ItemPage page = new ItemPage();
		page.items = items;
	}
	/*增加或覆盖*/
	public ItemPage putItems(String key, Item item) {
		items.put(key, item);
		return this;
	}
	/*不存在才可以增加,如果返回null说明增加成功,反之返回已经存在的值*/
	public ItemPage putIfAbsent(String key, Item item) {
		items.putIfAbsent(key, item);
		return this;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getVisits() {
		return visits;
	}

	public void setVisits(long visits) {
		this.visits = visits;
	}

	public Map<String, Item> getItems() {
		return items;
	}
	
	public Item getItem(String key) {
		return items.get(key);
	}
	
	public void setItems(Map<String, Item> items) {
		this.items = items;
	}

	public Item removeItem(String key) {
		return items.remove(key);
	}
	public Map<String, String> createItemsStringMap() {
		if(Util.isEmpty(items)){
			return new HashMap<>();
		}
		return items.values().stream().collect(Collectors.toMap(Item::getKey, Item::toJSONString));
	}
	
	@Override
	public Object cloneThis() throws CloneNotSupportedException {
		return clone();
	}
	@Override
	public ItemPage afterInit() {
		items = new HashMap<>(items);
		return this;
	}
}
