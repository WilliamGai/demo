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

### 1.1.2 禁止使用JSE的默认序列化  

时间|消耗时间占比|描述|文件大小
---|---|---|---
12750|  005%|  mysql 读取 |25.1M
05127|  002%|  serialize| 51M
00124|  000%|  serialize 写入文件|51M
00242|  000%|  deserialize 从文件读取|51M
05591|  002%| deserialize|51M
87367|  037%|  jse serialize and save file|85M
114670|  048%|  jse read file and deserialize|85M
03455|  001%|  make Object to json|83M
00212|  000%|  json save file|83M
01631|  001%|  json read|83M
07627|  003%|  json parse to Object|83M  

时间|消耗时间占比|描述
---|---|---
11969|  005%|  mysql 读取
01940|  001%|  serialize
00174|  000%|  serialize 写入文件
00103|  000%|  deserialize 从文件读取
12095|  005%|  deserialize
91090|  037%|  jse serialize and save file
120165|  048%|  jse read file and deserialize
01992|  001%|  make Object to json
00261|  000%|  json save file
01704|  001%|  json read
08002  003%  json parse to Object

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


构造函数
```
	String descs[] = columnItems.stream().map(ColumnItem::getDesc).toArray(String[]::new);
	<A> A[] toArray(IntFunction<A[]> generator); 可以看到new String[2];的表达式为String[]::new, 为IntFunction<A[]> generator
	
```
这也是个表达式,只有一个newz执行
```
Builder<?> downloadBuilder = excel.isPresent()?new ExcelBuilder():new TextBuilder();
```