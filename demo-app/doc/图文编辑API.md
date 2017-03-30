# 前端访问API
## 1 幻灯片
 url:
(http://demo.williamy.xin/get_lunbo?lunbo_group_no=hq_lunbo_01)
 return:
``` json
{
"group_no": "hq_lunbo_01",
"desc": "XXX轮播图",
"lunbos": [
{
"img_url": "/upload/lunbotuhq_lunbo_01/2017_03_29/20_44_37_179.png",
"desc": "描述测试1",
"img_url_cdn": null,
"link_url": "http://demo.williamy.xin/mg/article_yulan.html?id=126",
"status": 0
},
{
"img_url": "/upload/lunbotuhq_lunbo_01/2017_03_29/18_57_14_569.png",
"desc": "描述文件测试2",
"img_url_cdn": null,
"link_url": "http://demo.williamy.xin/mg/article_yulan.html?id=126",
"status": 0
}
],
"time": 1489653384420,
"info": "hq_lunbo_01"
}
```
## 2 文章列表
 url:
(http://demo.williamy.xin/articles?type_ids=xinwengonggao_hangyedongtai&page_no=0&page_size=5)
参数说明：
``` xml
type_ids：文章类型ID,多个文章类型用下划线“_”连接
page_no:第几页，从0开始
page_size:每页的大小
```
return:
``` json
{
"pages_num": 1,
"sum": 4,
"page_no": 0,
"list": [
{
"id": 126,
"title": "《舰指太平洋》盛邀军事博主“科罗廖夫”点评二战海战体系",
"short_title": "",
"read_num": 3,
"type_id": "xinwengonggao",
"type_name": "新闻公告",
"time": 1488871649416
},
{
"id": 91,
"title": "扬帆起航 《舰指太平洋》特色玩法介绍",
"short_title": "",
"read_num": 0,
"type_id": "xinwengonggao",
"type_name": "新闻公告",
"time": 1481611590968
},
{
"id": 90,
"title": "无畏冲锋 《舰指太平洋》12月9日双平台测试开启",
"short_title": "",
"read_num": 0,
"type_id": "xinwengonggao",
"type_name": "新闻公告",
"time": 1482999262198
},
{
"id": 89,
"title": "舰指海上霸主！《舰指太平洋》高清CG曝光",
"short_title": "",
"read_num": 0,
"type_id": "xinwengonggao",
"type_name": "新闻公告",
"time": 1481622160183
}
],
"page_nos": [
0
],
"page_size": 5
}
```

## 3 文章详情

