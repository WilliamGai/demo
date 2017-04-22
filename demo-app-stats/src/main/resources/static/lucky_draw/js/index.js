String.prototype.getQuery = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = this.substr(this.indexOf("\?") + 1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
var s = window.location.href;
var openid = s.getQuery("openid");
var dt = new Date();
var f_dt = dt.getFullYear() + "-" + (dt.getUTCMonth() + 1) + "-" + dt.getDate() + "-" + dt.getHours();
if (!openid) {
    openid = localStorage.getItem(f_dt);
}
if (!openid) {
        window.location = "http://service.sincetimes.com/signon?back_url=" + "http://jztpy.sincetimes.com/lucky_draw/index.html";
    } else {
    localStorage.setItem(f_dt, openid);
    window.history.replaceState({}, 0, 'http://jztpy.sincetimes.com/lucky_draw/index.html');
    $(document).ready(function () {

        document.onreadystatechange = subSomething;
        function subSomething() {
            if (document.readyState == "complete") { //当页面加载状态为完全结束时隐藏加载动画
                $(".loading_gif").hide();
            }
            ;
        };
        //加载时间过长关闭加载动画
        if (setT) {
            clearTimeout(setT);
        }
        ;
        var setT = setTimeout(function () {
            $(".loading_gif").hide();
        }, 6000);
        //抽奖地址
        var cjUrl = "http://jztpy.sincetimes.com/get_lucky_draw?sn=lucky_draw_01";
        //获奖记录
        var hjUrl = "http://jztpy.sincetimes.com/get_lucky_draw_result_by_sns?sn=lucky_draw_01__lucky_draw_02";
        // 分享后抽奖
        var fxUrl = "http://jztpy.sincetimes.com/get_lucky_draw?sn=lucky_draw_02";
        // 浮层滚动
        var Swidth = $(".zj_container").width();
        var Sleft = 0;

        var Set = setInterval(function () {
            Sleft--;
            $(".zj_container").css("left", "" + Sleft + "px");
            if (Sleft == -2100) {
                Sleft = 0;
            }
        }, 10);
        // 转盘抽奖开始
        var index = 0,
            num = 0,
            index1 = 1,
            num1 = 0,
            st = 0;
        $(".zp_start").click(function () {
            $(".loading_gif").show();
            var strStoreDate = window.localStorage ? localStorage.getItem("already") : Cookie.read("already");
            if (strStoreDate == 1) {
                $.ajax({
                    url: fxUrl,
                    type: "POST",
                    dataType: 'json',
                    data: {
                        who: openid
                    },
                    success: function (data) {
                        var data = eval(data);
                        if (data.result == 1) { //第一次抽奖
                            rotate(data);
                        } else if (data.result == 2) { //已经抽过
                            $(".already_use").show();
                            $(".black_bg").show();
                        } else if (data.result == -1) { //时间没到
                            alert("活动时间还未到哦~请看活动规则~")
                        } else if (data.result == -2) { //活动时间过了
                            alert("活动已结束~");
                        };
                        // 移除本地存储
                        if (window.localStorage) {
                            localStorage.removeItem("already", 1);
                        } else {
                            Cookie.write("already", -1);
                        };
                        $(".loading_gif").hide();
                    },
                    error: function () {
                        alert("转盘起飞失败，请刷新再试~");
                        $(".loading_gif").hide();
                    }
                })
            } else {
                $.ajax({
                    url: cjUrl,
                    dataType: 'json',
                    type: 'POST',
                    data: {
                        who: openid,
                    },
                    success: function (data) {
                        var data = eval(data);
                        if (data.result == 1) { //第一次抽奖
                            rotate(data);
                        } else if (data.result == 2) { //已经抽过
                            $(".already_use").show();
                            $(".black_bg").show();
                        } else if (data.result == -1) { //时间没到
                            alert("活动时间还未到哦~请看活动规则~")
                        } else if (data.result == -2) { //活动时间过了
                            alert("活动已结束~");
                        };
                        $(".loading_gif").hide();
                    },
                    error: function () {
                        alert("活动开始失败，请刷新再试~");
                        $(".loading_gif").hide();
                    }
                });
            };
        });
        //转盘转动抽奖
        function rotate(data) {
            console.log(data.data.ordinal)
            //速度较快转动
            var set = setInterval(function () {
                index++;
                num++;
                $(".light_" + index + "").addClass("light_zp");
                if (index == 8) {
                    index = 0;
                }
                ;
                if (num == 42) {
                    clearInterval(clear);
                    clearInterval(set);
                    index = 0;
                    num = 0;
                }
                ;
            }, 100);
            //重置状态
            var clear = setInterval(function () {
                $(".light").removeClass("light_zp");
            }, 850);
            //速度较慢转动准备出结果
            setTimeout(function () {
                var timeR = setInterval(function () {
                    index1++;
                    num1++;
                    $(".light_" + index1 + "").addClass("light_zp");
                    if (index1 == 8) {
                        index1 = 0;
                    };
                    //钻石
                    if (data.data.ordinal == 0 && num1 == 16) {
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_1").addClass("zj_get");
                        showSlider(data);
                        num1 = 0;
                    } else if (data.data.ordinal == 1 && num1 == 17) { //能量包
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_2").addClass("zj_get");
                        showSlider(data);
                        num1 = 0;
                    } else if (data.data.ordinal == 3 && num1 == 19) { //特工令
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_4").addClass("zj_get");
                        showSlider(data);
                        num1 = 0;
                    } else if (data.data.ordinal == 4 && num1 == 20) { //储油罐
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_4").addClass("zj_get");
                        showSlider(data);
                        num1 = 0;
                    } else if (data.data.ordinal == 6 && num1 == 22) { //谢谢参与
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_7").addClass("zj_get");
                        $(".haveno").show();
                        $(".black_bg").show();
                        num1 = 0;
                    } else if (data.data.ordinal == 7 && num1 == 23) { //合金
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_8").addClass("zj_get");
                        showSlider(data);
                        num1 = 0;
                    } else if (data.data.ordinal == 8 && num1 == 22) { //谢谢参与
                        clearInterval(timeR);
                        clearInterval(clearT);
                        $(".light").removeClass("light_zp");
                        $(".light_7").addClass("zj_get");
                        $(".haveno").show();
                        $(".black_bg").show();
                        num1 = 0;
                    };
                }, 200);
                var clearT = setInterval(function () {
                    $(".light").removeClass("light_zp");
                }, 1650);
            }, 3000);
        };
        // 中奖窗口
        function showSlider(data) {
            $(".zj_C").show();
            $(".black_bg").show();
            $(".r_sp_1").html(data.data.code);
            if (data.data.ordinal == 0) { //钻石
                $(".zj_jp").removeClass("zj_jp_show");
                $(".zj_jp").eq(1).addClass("zj_jp_show");
            } else if (data.data.ordinal == 1) { //能量包
                $(".zj_jp").removeClass("zj_jp_show");
                $(".zj_jp").eq(2).addClass("zj_jp_show");
            } else if (data.data.ordinal == 3) { //特工令
                $(".zj_jp").removeClass("zj_jp_show");
                $(".zj_jp").eq(4).addClass("zj_jp_show");
            } else if (data.data.ordinal == 4) { //储油罐
                $(".zj_jp").removeClass("zj_jp_show");
                $(".zj_jp").eq(3).addClass("zj_jp_show");
            } else if (data.data.ordinal == 7) { //合金
                $(".zj_jp").removeClass("zj_jp_show");
                $(".zj_jp").eq(0).addClass("zj_jp_show");
            }
            ;
        };
        // 窗口关闭
        $(".close_slider").click(function () {
            $(".recored_slider").hide();
            $('.haveno').hide();
            $(".already_use").hide();
            $(".zj_C").hide();
            $(".black_bg").hide();
            $(".recored_C").children().remove();
        });
        //获奖记录查看
        $(".recored").click(function () {
            $(".loading_gif").show();
            $(".recored_slider").show();
            $(".black_bg").show();
            $.ajax({
                type: "POST",
                dataType: "json",
                url: hjUrl,
                data: {
                    who: openid,
                },
                success: function (data) {
                    var data = eval(data);
                    if (data.data.length == 0) {
                        $(".recored_C").append("<b class='recored_N'>你还未抽奖！</b>");
                    };
                    var str = "";
                    for (var i = 0; i < data.data.length; i++) {
                        str += "<ul code = "+data.data[i].code+">" +
                            "<p>获得领取码：</p>" +
                            "<li>" +
                            "<span class='r_sp_3'>" + data.data[i].code + "</span>" +
                            "<span class='r_sp_2'>复制粘贴</span>" +
                            "</li>" +
                            "</ul>";
                    };
                    $(".recored_C").append(str);
                    for(var k=0;k < data.data.length;k++){
                        if($(".recored_C").find("ul").eq(k).attr("code")=="xxcy"){
                            $(".recored_C").find("ul").eq(k).remove();
                        }
                    };
                    $(".loading_gif").hide();
                },
                error: function () {
                    alert("获取失败，请刷新再试`");
                    $(".loading_gif").hide();
                }
            })
        });
        //按钮点击效果
        $(".down_load").bind("touchstart", function () {
            $(this).css({
                "transform": "scale(0.8)",
                "-webkit-transform": "scale(0.8)",
                "-moz-transform": "scale(0.8)",
                "-ms-transform": "scale(0.8)"
            });
            $(this).bind("touchend", function () {
                $(this).css({
                    "transform": "scale(1)",
                    "-webkit-transform": "scale(1)",
                    "-moz-transform": "scale(1)",
                    "-ms-transform": "scale(1)"
                });
            });
        });
        // 分享界面
        function isWeiXin() {
            var ua = window.navigator.userAgent.toLowerCase();
            if (ua.match(/MicroMessenger/i) == 'micromessenger') {
                return true;
            } else {
                return false;
            }
        };
        // 分享点击按钮设置抽奖属性
        var clickTimes = 1;
        $(".invat_btn").click(function () {
            if (clickTimes == 1) {
                var invate = 1;
                if (window.localStorage) {
                    localStorage.setItem("already", invate);
                } else {
                    Cookie.write("already", invate);
                }
                ;
                clickTimes = 0;
            };
          
            if (isWeiXin() == true) {
                $(".share").show();
                $(".black_bg_2").show();
            };
        });
        $(".share").click(function (event) {
            event.stopPropagation();
            $(this).hide();
            $(".black_bg_2").hide();
        });
    });
}

