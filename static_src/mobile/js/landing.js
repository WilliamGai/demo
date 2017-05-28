$(document).ready(function() {
  $(function() {  
        FastClick.attach(document.body);  
    });
    $('.down_btn').bind("touchstart", function() {
        $(this).css("color", "white");
        $(this).bind("touchend", function() {
            $(this).css("color", "#313131");
        });
    });
    $('.join_btn').bind("touchstart", function() {
        $(this).css("color", "white");
        $(this).bind("touchend", function() {
            $(this).css("color", "#313131");
        });
    });
    $('.mid_img').bind("touchstart", function() {
        $(this).css({
            "transform": "scale(0.9)",
            "-webkit-transform": "scale(0.9)",
            "-moz-transform": "scale(0.9)"
        });
    });
    $('.mid_img').bind("touchend", function() {
        $(this).css({
            "transform": "scale(1)",
            "-webkit-transform": "scale(1)",
            "-moz-transform": "scale(1)",
        });
    });
    // 微信联系
    var a = 0;
    $(".wx_l").click(function() {
        $(".wx_contack").toggle();
        if (a == 0) {
            $(".wx_img").attr("src", "imgs/wx_2.png");
            a = 1;
        } else if (a == 1) {
            $(".wx_img").attr("src", "imgs/wx_logo.png");
            a = 0;
        }
    });
    $('.wx_l').bind("touchstart", function() {
        $(".wx_img").attr("src", "imgs/wx_2.png");
        $(this).bind("touchend", function() {
            $(".wx_img").attr("src", "imgs/wx_logo.png");
        })
    });
    // qq联系
    var b = 0;
    $(".qq_l").click(function() {
        $(".qq_contack").toggle();
        if (b == 0) {
            $(this).find("img").attr("src", "imgs/qq_2.png");
            b = 1;
        } else if (b == 1) {
            $(this).find("img").attr("src", "imgs/qq_logo.png");
            b = 0;
        }
    });
    $('.qq_l').bind("touchstart", function() {
        $(this).find("img").attr("src", "imgs/qq_2.png");
        $(this).bind("touchend", function() {
            $(this).find("img").attr("src", "imgs/qq_logo.png");
        })
    });
    //微博
    $('.wb_l').bind("touchstart", function() {
        $(this).find("img").attr("src", "imgs/wb_2.png");
        $(this).bind("touchend", function() {
            $(this).find("img").attr("src", "imgs/wb_logo.png");
        })
    });
    // 百度
    $('.bd_l').bind("touchstart", function() {
        $(this).find("img").attr("src", "imgs/bd_2.png");
        $(this).bind("touchend", function() {
            $(this).find("img").attr("src", "imgs/bd_logo.png");
        })
    });
     //微博跳转地址
    $(".wb_l").click(function(){
        window.location="http://weibo.com/jztpy";
    });
       //百度贴吧跳转地址
    $(".bd_l").click(function(){
        window.location="http://tieba.baidu.com/f?ie=utf-8&kw=%E8%88%B0%E6%8C%87%E5%A4%AA%E5%B9%B3%E6%B4%8B&fr=search";
    });
})