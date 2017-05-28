var back_url = "http://jztpy.sincetimes.com/qiandao/exchange.html";
var base_url = "http://jztpy.sincetimes.com/jztpy_shop";

var s = window.location.href;
var openid;
String.prototype.getQuery = function(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = this.substr(this.indexOf("\?") + 1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
openid = s.getQuery("openid");
console.log("openid =", openid);
var dt = new Date();
var f_dt = dt.getFullYear() + "-" + (dt.getUTCMonth() + 1) + "-" + dt.getDate() + "-" + dt.getHours();
if (openid) {
    //window.history.replaceState({},0,'/qiandao/exchange.html');
    localStorage.setItem(f_dt, openid);
} else {
    if (localStorage.getItem(f_dt)) {
        openid = localStorage.getItem(f_dt);
    } else {
        window.location = "http://service.sincetimes.com/signon?back_url=" + back_url;
    }
}
$(document).ready(function() {
    //商品列表
    var listUrl = base_url + "/get_shop_infos",
        //兑换地址
        Durl = base_url + "/buy",
        //查看积分
        Hurl = base_url + "/look_jifen",

        //openid = "oSSKNwL2ckTrOtptEwLHeYRs9eIc",
        // 兑换记录地址
        Aurl = base_url + "/buy_history",
        page_no = 0;
    //加载商品列表
    Ajax(listUrl, page_no);

    function Ajax(url, page) {
        $.ajax({
            url: url,
            dataType: "json",
            type: "GET",
            data: {
                openid: openid,
                shop_sn: 'jztpy_shop',
                page_no: page,
            },
            success: function(data) {
                var data = eval(data);
                if (data.result == 1) {
                    GoodList(data, page_no);
                };
            },
            error: function() {
                alert("加载商品列表失败，请刷新再试~");
            }
        });
    }
    // 加载商品列表
    function GoodList(data, page_no) {
        if (page_no < data.data_other.pages_num) {
            var str = "",
                da = data.data_other.goods_list;
            for (var i = 0; i < da.length; i++) {
                str += " <div class='jp_C'>" +
                    "<ul class='jp_ul'>" +
                    "<img class='jp_img' src=" + da[i].pic_url + " alt=''>" +
                    "<li class='jp_li_1'>" + da[i].goods_name + "</li>" +
                    "<li class='jp_li_2'>兑换积分：<span>" + da[i].points_need + "</span>分 剩余数量：<span>" + da[i].left_codes_num + "</span></li>" +
                    "<li class='jp_li_3'>" +
                    "<button status=" + da[i].status + " left=" + da[i].left_codes_num + " Dnum=" + da[i].sn + " class='jp_btn_1' type='button'>我要兑换</button>" +
                    "<button num=" + i + " class='jp_btn_2' type='button'>查看详细</button>" +
                    "</li>" +
                    "</ul>" +
                    "</div>";
            };
            var loadM = "<div class='load_more'>" +
                        "<img src='imgs/loadmore.jpg' alt=''>" +
                        "</div>";
            $(".center_C").append(str);
            $(".load_M").append(loadM);
            
            for(var k = 0;k<$(".center_C").find(".jp_btn_1").length;k++){
                var btn = $(".center_C").find(".jp_btn_1").eq(k).attr("left");
                if (btn == 0) {
                    $(".center_C").find(".jp_btn_1").eq(k).css("background","#909090");
                }
            };
            //详细按钮点击
            $(".jp_btn_2").live("click", function() {
                var index = $(this).attr("num");
                $(".slider_bg").show();
                $(".slider_4").show();
                var text = da[index].goods_desc.replace(/#/g, "<br>")
                $(".sd4_p").html(text);
            });
        } else {
            $(".load_more").hide();
            alert("没有了~");
        }
    };
    //兑换
    $(".jp_btn_1").live("click", function() {
        var num = $(this).attr("Dnum"),
            left = $(this).attr("left"),
            status = $(this).attr("status");
        if (left == 0) {
            alert("没有库存了~");
        } else if (status == 1) {
            alert("您已兑换过了~");
        } else if (status == 0 && left!=0) {
            $.ajax({
                type: "GET",
                dataType: "json",
                url: Durl,
                data: {
                    openid: openid,
                    goods_sn: num,
                },
                success: function(data) {
                    if (data.result == 1) {
                        alert("兑换成功！");
                        GoodList(data, page_no);
                    } else if (data.result == 2) {
                        alert("积分不足！")
                    } else if (data.result == 3) {
                        alert("没有库存啦~")
                    }else if (data.result == 4) {
                        alert("你已经兑换过次商品！")
                    };
                },
                error: function() {
                    alert("兑换失败，请稍后再试~")
                }
            })
        };
    });
    // 加载更多
    $(".load_more").live("click", function() {
        page_no++;
        Ajax(listUrl, page_no);
        $(this).hide();
    });
    // 兑换说明弹窗
    $(".link_1").click(function() {
        $(".slider_bg").show();
        $(".slider_1").show();
    });
    //关闭各种弹窗
    $(".close").click(function() {
        $(".slider_bg").hide();
        $(".slider_1").hide();
        $(".slider_2").hide();
        $(".slider_3").hide();
        $(".slider_4").hide();
        $(".sd2_table").find(".sd_tr").remove();
    });
    // 查看积分弹窗
    $(".link_2").click(function() {
        $(".slider_bg").show();
        $(".slider_3").show();
        $.ajax({
            url: Hurl,
            dataType: "json",
            type: "GET",
            data: {
                openid: openid,
            },
            success: function(data) {
                var data = eval(data);
                $(".head_img").attr("src", "" + data.data[1] + "");
                $(".sd3_p_1").html(data.data[0]);
                $(".jifen").html(data.data_other);
            }
        })
    });
    //兑换记录弹窗
    $(".link_3").click(function() {
        $(".slider_bg").show();
        $(".slider_2").show();
        $.ajax({
            url: Aurl,
            dataType: "json",
            type: "GET",
            data: {
                openid: openid
            },
            success: function(data) {
                if (data.result == 1) {
                    var str = "";
                    for (var i = 0; i < data.data.length; i++) {
                        str += "<tr class='sd_tr'>" +
                            "<td>" + data.data[i].goods_name + "</td>" +
                            "<td>" + data.data[i].code + "</td>" +
                            "<td>" + Time(data.data[i].time) + "</td>" +
                            "</tr>";
                    }
                    $(".sd2_table").append(str);
                }
            },
            error: function() {
                alert("加载兑换记录失败，请刷新再试~")
            }
        });
    });

    function Time(time) {
        var dt = new Date(time);
        var f_dt = dt.getFullYear() + "-" + (dt.getUTCMonth() + 1) + "-" + dt.getDate();
        return f_dt;
    };
});