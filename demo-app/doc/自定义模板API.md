# 自定义模板API
## 1 创建模板和创建页面
### 1.1 登陆后点击`自定义模板`
 ![icon](imgs/page_guide.PNG)
### 1.1 进入模板编辑页面选择`查看页面列表`
 ![icon](imgs/page_guide2.PNG)
### 1.1 点击`新建`增加一个页面
 ![icon](imgs/page_guide3.PNG)
## 2 获取某个页面
 url:
(http://demo.williamy.xin/page_rst?template_id=products&id=3)  
  
参数说明：
template_id：文章类型ID,创建模板指定的编号
id:自动生成的ID,随着新页面创建自增
 return:
``` json
{
    "id": "3",
    "name": "游戏列表",
    "visits": 1,
    "items": {
        "features": {
            "key": "features",
            "name": "游戏特色",
            "type": "TEXT_AREA_LIST",
            "data": null,
            "createTime": 1492149150492,
            "createTimeStr": null
        },
        "brif": {
            "key": "brif",
            "name": "游戏名称",
            "type": "TEXT_AREA",
            "data": {
                "data": "测试文本",
                "name": "测试文本"
            },
            "createTime": 1492148963083,
            "createTimeStr": null
        },
        "icon": {
            "key": "icon",
            "name": "图标",
            "type": "PICTURE",
            "data": "/upload/pages/2017_04_14/14_26_00_864.png",
            "createTime": 1492148981859,
            "createTimeStr": null
        },
        "downloadurl": {
            "key": "downloadurl",
            "name": "下载链接",
            "type": "TEXT",
            "data": null,
            "createTime": 1492149055272,
            "createTimeStr": null
        },
        "imagelist": {
            "key": "imagelist",
            "name": "图片列表",
            "type": "PICTURE_LIST",
            "data": null,
            "createTime": 1492149089194,
            "createTimeStr": null
        },
        "platforms": {
            "key": "platforms",
            "name": "平台",
            "type": "TEXT",
            "data": null,
            "createTime": 1492149025353,
            "createTimeStr": null
        }
    }
}
```
## 3 文章列表  
- url:(http://demo.williamy.xin/pages_rst?template_id=products)  
 
  
参数说明：
``` xml
template_id：文章类型ID,创建模板指定的编号
```
return:

 ![icon](imgs/template_pages.PNG)

