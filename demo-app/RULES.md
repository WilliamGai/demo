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
### debug
``` java
			String prettyStr = JSON.toJSONString(json,SerializerFeature.PrettyFormat,SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);

```