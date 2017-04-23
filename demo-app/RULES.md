# RULES

## 1
### 1.1 转义
  
  
显示结果|描述|实体名称|实体编号
---|---|---|---
&nbsp;| 空格 | `&nbsp;` | `&#160;`
&lt; | 小于号 | `&lt;` | `&#60;`
&gt; | 大于号 | `&gt;` | `&#62;`
&amp; | 和号 | `&amp;` | `&#38;`
&quot; | 引号 | `&quot;` |`&#34;`
&apos;| 撇号 | `&apos; (IE不支持)`	`&#39;`
timestamp和timedate都是时间
``` 


时间|消耗时间占比|描述|文件大小  
---|---|---|---
14005|  006%|  mysql读取|24M
01664|  001%|  serialize |51.5M
05975|  002%|  deserialize|51.5M
93765|  037%|  jse serialize|85.7M
128668|  051%|  jse deserialize|85.7M
02094|  001%|  json |53.1M
07720|  003%|  json |53.1M
```
## 2 thymeleaf 
`#lists.isEmpty(list)`  
  
``` xml
 <li th:each="i,iterStat : ${#numbers.sequence( 1, #lists.size(lunbo.lunbos))}"  th:attr="data-slide-to=${iterStat.index}" data-target="#carousel-786045" th:class="${iterStat.index==0}? 'active'">
</li>

```
``` xml
<h3 th:if="${#lists.isEmpty(list)}">无</h3>
			<td th:onclick="${'delete_article('''+item.id+''')'}"><a href="javascript:;">删除</a></td>
<input type="file" th:id="${'pic'+item.key}" name="pic" style="display: none" th:attr="onchange=${'showImg(this,'+'''_pic_show'+item.key+''')'}"/>
				<img height="60" th:id="${'_pic_show'+item.key}" name="_pic_show" width="60" th:onclick="${'_open_pic('+'''pic'+item.key+''')'}" th:src="${pic}" onerror="this.onerror='';src='/mg/images/default.png'"/>


```

## 3 排序
``` java
List<ArticleVO> list = articles.stream()
	    		.filter(typeFilter)
	    		.sorted(Comparator.comparing(Article::getId).reversed())
	    		.skip(skip_num)
	    		.limit(_page_size)
	    		.map(ArticleManager::initArticle)
	    		.map(ArticleVO::new)
	    		.collect(Collectors.toList());
```
``` java
		List<ArticleType> types = articleService.getAllArticleTypes();//取出所有
		types.sort(Comparator.comparing(ArticleType::getCreate_time));//根据创建时间排序
```
``` java
			Collections.sort(fileList, DIR_COMPARAOR_FUNC.thenComparing(SIZE_COMPARAOR_FUNC));

```
### Stream
``` java 
 		Set<String> set = new HashSet<>();
		set.add("1");
		set.add("3");
		set.add("9");
		set.add("12");
		set.add("10");
		set.add("11");
		set.add("81");
		set.add("82");
		Optional<Integer> max = set.stream().map(Integer::parseInt).collect(Collectors.toList()).stream().max(Integer::compareTo);
		OptionalInt max2 = set.stream().mapToInt(Integer::parseInt).max();
		Optional<Integer> max3 = set.stream().map(Integer::parseInt).max(Integer::compareTo);
 
```
### debug
``` java
			String prettyStr = JSON.toJSONString(json,SerializerFeature.PrettyFormat,SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);

```
list和map的转换
``` java
		ARTICLE_TYPE_MAP = types.stream().collect(Collectors.toMap(ArticleType::getId, Function.identity(), (oldValue, newValue)->newValue, LinkedHashMap::new));
		
						.collect(Collectors.toMap(UserVO::getName, Function.identity()));
		

```

2017年4月20日17:16:09
thymleaf中的${}获取的不是对象的快照，而是真实的java对象,判断的时候切忌不要误使用=以及其他操作符！