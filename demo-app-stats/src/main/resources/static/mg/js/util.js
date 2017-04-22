var util = function () {
    return {
        on:function (obj, type, handler) {
            var types = this.isArray(type) ? type : [type],
                k = types.length,
                d;
            if (!obj.addEventListener) {
                //绑定obj 为this
                d = function (evt) {
                    evt = evt || window.event;
                    var el = evt.srcElement;
                    return handler.call(el, evt);
                };
                handler._d = d;
            }
            if (k) while (k--) {
                type = types[k];
                if (obj.addEventListener) {
                    obj.addEventListener(type, handler, false);
                } else {
                    obj.attachEvent('on' + type, d);
                }
            }
            obj = null;
        },
        un:function (obj, type, handler) {
            var types = this.isArray(type) ? type : [type],
                k = types.length;
            if (k) while (k--) {
                type = types[k];
                if (obj.removeEventListener) {
                    obj.removeEventListener(type, handler, false);
                } else {
                    obj.detachEvent('on' + type, handler._d || handler);
                }
            }
        },
        isEmpty:function (data) {
            return data.replace(/[ ]/g, "") != "" ? data : "无";
        },
        getEvent:function (event) {
            return event ? event : window.event;
        },
        getTarget:function (event) {
            return event.target || event.srcElement;
        },
        setInnerText:function (element, text) {
            if (typeof element.textContent == "string")
                element.textContent = text;
            else
                element.innerText = text;
        },
        $G:function (id) {
            return document.getElementById(id)
        },
        getFirstNode:function (ele) {
            return ele.firstChild.nodeType == 1 ? ele.firstChild : ele.firstElementChild;
        },
        getElementsByClassName:function (clsName) {
            var doc = document;
            if (!doc.getElementsByClassName) {
                var clsArr = [];
                var reg = new RegExp("\\b" + clsName + "\\b");
                var eleArr = doc.getElementsByTagName("*");
                for (var i = 0, eleobj; eleobj = eleArr[i++];) {
                    if (reg.test(eleobj.className))
                        clsArr.push(eleobj);
                }
                return clsArr;
            }
            else {
                return doc.getElementsByClassName(clsName);
            }
        },
        getCharCode:function (event) {
            return event.keyCode || event.which || event.charCode;
        },
        getStyleValue:function(ele,attr){
            var doc=document;
            var style=ele.currentStyle||doc.defaultView.getComputedStyle(ele,null);
            return parseInt(style[attr].replace(/px/g,""));
        },
        getBrowerVersion:function(){
            var agent = navigator.userAgent.toLowerCase(),
                opera = window.opera,
                browser = {
                    ie		: !!window.ActiveXObject,
                    webkit	: ( agent.indexOf( ' applewebkit/' ) > -1 ),
                    quirks : ( document.compatMode == 'BackCompat' ),
                    opera	: ( !!opera && opera.version )
                };
            if ( browser.ie ){
                browser.version = parseFloat( agent.match( /msie (\d+)/ )[1] );
            }
            browser.gecko = ( navigator.product == 'Gecko' && !browser.webkit && !browser.opera );
            return browser;
        },
        isArray:function (obj) {
            return Object.prototype.toString.call(obj) === '[object Array]';
        },
        request:function (option) {
            var ajaxRequest = creatAjaxRequest();
            if (ajaxRequest == null) {
                alert("您的浏览器不支持AJAX！");
                return;
            }
            ajaxRequest.onreadystatechange = function () {
                if (ajaxRequest.readyState == 4) {
                    if (ajaxRequest.status >= 200 && ajaxRequest.status < 300 || ajaxRequest.status == 304) {
                        option.onSuccess(ajaxRequest.responseText);
                    }
                }
                else {
                    if (option.hasLoading)
                        util.$G(option.loading_Id).innerHTML = "<div class='hook_con'><img class='loading_pic' src='images/loading.gif'/></div>";
                }
            };
            ajaxRequest.open("post", option.url, true);
            ajaxRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            ajaxRequest.send(option.param);
        }
    };

    /**
     * 创建一个ajaxRequest对象
     */
    function creatAjaxRequest() {
        var xmlHttp = null;
        if (window.XMLHttpRequest) {
            xmlHttp = new XMLHttpRequest();
        } else {
            try {
                xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (e) {
                }
            }
        }
        return xmlHttp;
    }
}();