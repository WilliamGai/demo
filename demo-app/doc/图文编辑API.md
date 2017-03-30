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

## 3 获取文章详情
  
  ### url：
    
    (http://demo.williamy.xin/article?id=122)
  
  ### 参数说明：
  ``` xml
  {
"article": {
"id": 122,
"title": "行业动态测试5",
"short_title": "",
"read_num": 2,
"type_id": "hangyedongtai",
"type_name": "行业动态",
"time": 1490841569964,
"content": "手把手教你来选船<br />
《舰指太平洋》中的船还是比较多的，光5星橙船就有30多条，新手看到这些都会比较疑惑，毕竟游戏玩到后来只要培养9条就足够，什么船值得培养一定是所有人要考虑的一件事。本篇主要是针对游戏中后期有能力将橙船进阶的时候，哪些船值得培养进行分析。<br />
<p>
	首先游戏安设了一个评级属性，可以理解为评分高的船属性也会高。也就意味着评分是大家在选船时的一个重要指标。除了评分外，玩到中后期，那些没有直接显示在改造面板上的二级属性，影响也十分明显。什么是二级属性呢，目前游戏里分别是闪避，命中，暴击，韧性，暴击伤害。韧性在其他游戏中比较少见，应该就是抗暴击。也就是说这个游戏中闪避和命中相对，暴击和韧性相对。游戏到了后期，当闪避堆高时，就会在战斗中看到满屏的miss，暴击堆高了就会看到一堆的红色暴击数字，场面非常可观。但是有一点就是想要堆高暴击要记得先堆命中，因为如果命中不高，会被闪避高的闪掉，暴击的价值就会降低。推荐大家优先选择那些自带二级属性特别是闪避和暴击较高的船，同时如果这个船的评分比较高就更好了。
</p>
<p>
	<img src="/upload/image/2017_01_12/11_55_18_925.png" alt="" /> 
</p>
图1 点放大镜按钮，可以查看船的详情，里面有评级显示<br />
<p>
	如何看游戏中船自带的二级属性，首先看船的被动技能，也就是第3个技能是加什么的属性。一条加闪避的被动技能，在升满后可以提供10%的闪避。特别值得一提的就是舰船的被动技能，不仅会对自己生效，也会对阵容中其他船生效。其次看船的进阶加成属性，就是+1，+2的那些额外附加的属性。这里需要提的是最好是看+4或者+3时的属性，因为这两次进阶额外带的二级属性值也会比较高。
</p>
图2 大和号非常厉害，技能养满后暴击和闪避可加20%，并且附送5%的命中<br />
图3闪避神船加贺号，技能满级的话相当于自带30%的闪避，10%的暴击也非常好<br />
建议大家量力而行，不要盲目培养大和，衣阿华等，因为很难得到图纸，如果不是高V，前期只有靠抽船才能获得他们的图纸，而且抽的概率也相对较低，要不就要等解锁后面的科技才能兑换，需要的时间较长。而二线评级为14的船，依然有不少值得培养。那么这里推荐几个评级14的船：<br />
图4 赤城号，评级14，自带暴击20%，主要是因为兑换要求条件相对较低 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<br />
图5 IXD2型，潜艇，评级14，自带闪避30%<br />
图6 兴登堡号，战列舰，评级14，自带30%闪避 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<br />
图7 &nbsp;齐柏林伯爵号航母，评级14，自带20%暴击，10%闪避。<br />
再给大家提示一下自带二级属性比较高的船。<br />
闪避高的船：大和、加贺、IXD2型、兴登堡、光辉、丁鲷、伊400、北卡、苍龙<br />
暴击高的船：大和、衣阿华、翔鹤、赤城、狮级、齐柏林、丁鲷、云龙、纳尔逊、 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;<br />
最后提一下阵容搭配：其实无脑搭配一套全闪避或者全暴击的阵容已经是比较强了，但是毕竟其中会有一些船不是那么合适。<br />
<br />
从进攻角度上讲，建议选择火力集中的阵容，比如潜艇流，因为潜艇的技能都是打前排的，可以迅速推掉敌人的前排肉盾。或者选几个技能是打后排的战列舰或者航母，这里推荐大和，北卡，埃塞克斯，皇家方舟，能够集中秒掉敌方的后排输出。其中埃塞克斯虽然自带暴击属性不高，好在命中有保障，再加上自身属性很高，输出依然爆炸。<br />
<br />
从防守角度讲，前排肉盾推荐适合闪避的或者血量高的船，比如前卫号，虽然没什么闪避，但是血很多，依旧能抗。后排也推荐放一个肉盾或者闪避高的船，避免后排被远程攻击的迅速秒掉，然后中排受到威胁。而其他的后排选手则选命中暴击高的输出船，如果对方没有攻击后排的船，他们就可以肆意输出啦。<br />"
}
}
  ```
