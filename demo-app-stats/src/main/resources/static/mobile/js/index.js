$(document).ready(function() {
    var FrameRates = {
        film: 24,
        NTSC: 29.97,
        NTSC_Film: 23.98,
        NTSC_HD: 59.94,
        PAL: 25,
        PAL_HD: 50,
        web: 30,
        high: 60
    };
    debug = true;
      var mySwiper = new Swiper('.swiper_two', {
        slidesPerView : 3,
        spaceBetween : 20,
        autoplay : 2000,
        // 如果需要前进后退按钮
        nextButton: '.swiper-button-next',
        prevButton: '.swiper-button-prev',
    });
    var sliderUrl = "/get_lunbo?lunbo_group_no=lunbo_1";
    Slider(sliderUrl);
    // 轮播图
    function Slider(url){
        $.ajax({
            url:url,
            dataType:"json",
            type:"GET",
            data:{

            },
            success:function(data){
                var data =eval(data);
                showSlider(data);
            }
        }) 
    };
    function showSlider(data){
        var str="";
        for(var i =0;i<data.lunbos.length;i++){
            str+="<div class='swiper-slide'>"+
                "<a class='lunbotu1' href='newsDetail.html?id="+data.lunbos[i].link_url+"'>"+
                    "<img class='sw_img' src='"+data.lunbos[i].img_url+"' alt=''>"+
                "</a>"+
                "<p class='swiper_P'>"+data.lunbos[i].desc+"</p>"+
            "</div>";
        };
        $('.addin_slider').append(str);
            var mySwiper = new Swiper('.swiper_one', {
            // direction: 'vertical',
            loop: true,
            autoplay : 2000,
            // 如果需要分页器
            pagination: '.swiper-pagination',
        });
    };
    // 滚动
    $.fn.scrollTo =function(options){
        var defaults = {
            toT : 0,    //滚动目标位置
            durTime : 500,  //过渡动画时间
            delay : 30,     //定时器时间
            callback:null   //回调函数
        };
        var opts = $.extend(defaults,options),
            timer = null,
            _this = this,
            curTop = _this.scrollTop(),//滚动条当前的位置
            subTop = opts.toT - curTop,    //滚动条目标位置和当前位置的差值
            index = 0,
            dur = Math.round(opts.durTime / opts.delay),
            smoothScroll = function(t){
                index++;
                var per = Math.round(subTop/dur);
                if(index >= dur){
                    _this.scrollTop(t);
                    window.clearInterval(timer);
                    if(opts.callback && typeof opts.callback == 'function'){
                        opts.callback();
                    }
                    return;
                }else{
                    _this.scrollTop(curTop + index*per);
                }
            };
        timer = window.setInterval(function(){
            smoothScroll(opts.toT);
        }, opts.delay);
        return _this;
    };
    $('.up_1').click(function(){
        var row_1 = $(".up_m_1").offset().top-200;
        $("body").scrollTo({toT:row_1});
    });
    $(".up_2").click(function(){
        var row_1 = $(".up_m_2").offset().top-200;
        $("body").scrollTo({toT:row_1});
    });
    // 新闻切换
    $('.news_Li').click(function() {
        $(this).siblings(".news_Li").removeClass("news_a_li");
        $(this).addClass("news_a_li");
        $(this).siblings(".news_Li").find('.triangle-down').removeClass("arow_down");
        $(this).find('.triangle-down').addClass("arow_down");
        var index = $(this).index();
        $(".news_div").removeClass("news_div_show");
        $(".news_div").eq(index).addClass("news_div_show");
    });
    // 战舰介绍切换
    $('.zj_li').click(function() {
        var index = $(this).index();
        $('.zj_li').removeClass("zj_active");
        $(this).addClass("zj_active");
        $(".zj_img").removeClass("zj_img_show");
        $(".zj_img").eq(index).addClass("zj_img_show");
        $(".zj_t").removeClass("zj_t_active");
        $(".zj_t").eq(index).addClass("zj_t_active");
    });
    // 视频部分
    $('.v_img').click(function(){
        var num = $(this).attr("num");
        $(".game_v_div").removeClass("game_v_show");
        $(".game_v_div").eq(num).addClass("game_v_show");
    });
    //微博
    $('.wb_l').bind("touchstart",function(){
        $(this).find("img").attr("src","imgs/wb_i_2.png");
        $(this).bind("touchend",function(){
            $(this).find("img").attr("src","imgs/wb_i_1.png");
        })
    });
    // qq联系
    var b =0;
    $(".qq_l").click(function(){
        $(".qq_contack").toggle();
         if (b == 0) {
            $(this).find("img").attr("src","imgs/qq_i_2.png");
            b = 1;
        }else if (b==1) {
             $(this).find("img").attr("src","imgs/qq_i_1.png");
            b = 0;
        }
    });
     $('.qq_l').bind("touchstart",function(){
        $(this).find("img").attr("src","imgs/qq_i_2.png");
        $(this).bind("touchend",function(){
            $(this).find("img").attr("src","imgs/qq_i_1.png");
        })
    });
    // 微信联系
    var a = 0;
    $(".wx_l").click(function(){
        $(".wx_contack").toggle();
        if (a == 0) {
            $(".wx_img").attr("src","imgs/wx_i_2.png");
            a = 1;
        }else if (a==1) {
            $(".wx_img").attr("src","imgs/wx_i_1.png");
            a = 0;
        }
    });
     $('.wx_l').bind("touchstart",function(){
        $(".wx_img").attr("src","imgs/wx_i_2.png");
        $(this).bind("touchend",function(){
            $(".wx_img").attr("src","imgs/wx_i_1.png");
        })
    });
     // 百度
      $('.bd_l').bind("touchstart",function(){
        $(this).find("img").attr("src","imgs/bd_i_2.png");
        $(this).bind("touchend",function(){
            $(this).find("img").attr("src","imgs/bd_i_1.png");
        })
    });
      // cg
      $(".index_video").click(function(){
        $('.video_bg').show();
        $(".video_container").show();
        var src = $(this).attr("url");
            $('#frame').show();
            $('#frame').attr("src",""+src+"");
      });
    // 打开视频
    $(".video_start").click(function(){
        $('.video_bg').show();
        $(".video_container").show();
        var videoSrc = $(this).attr("url");
        $("#frame").attr("src",""+videoSrc+"");
    });
    // 关闭视频
    $(".close_video").click(function(){
        $('.video_bg').hide();
        $(".video_container").hide();
        $("#frame").attr("src","");
    });
    // 礼包领取
    $('.gift').click(function(){
        $.ajax({
            url:"/if_code_fit?sn=jztpy_home",
            dataType:"json",
            type:"GET",
            data:{

            },
            success:function(data){
                var data = eval(data);
                console.log('/if_code_fit?sn=jztpy_home',data);
                if (data.result==1) {
                    $('.lblq').show();
                    $('.slider_bg').show();
                }else if (data.result==-1){
                    $('.jq_qd').show();
                    $('.slider_bg').show();
                }else if (data.result==-2) {
                    alert("礼包活动结束，下次再来哦!")
                }
            }
        })
    });
    $(".lq_btn").bind("touchstart",function(){
        $(this).css({
            "tranform":"scale(0.8)",
            "-webkit-transform":"scale(0.8)",
            "-moz-transform":"scale(0.8)",
            "-ms-transform":"scale(0.8)"
        });
        $(this).bind("touchend",function(){
             $(this).css({
                "tranform":"scale(1)",
                "-webkit-transform":"scale(1)",
                "-moz-transform":"scale(1)",
                "-ms-transform":"scale(1)"
            });
        });
    });
    $('.lq_btn').click(function(){
        console.log('s')
        var phone = $('.phone_input').val();
        if (!(/^1(3|4|5|7|8)\d{9}$/.test(phone))) {
            alert("请输入正确手机号码!");
        }else{
            $.ajax({
                url:"/get_a_code?sn=ztpy_home_20170208",
                dataType:"json",
                data:{
                    who:phone,
                },
                type:"POST",
                success:function(data){
                    var data = eval(data);
                    console.log("get code result", data);
                    if (data.result==1) {
                        $(".lblq ").hide();
                        $('.code').html(data.code);
                        $(".lq_cg").show();
                    }else if (data.result == 2) {
                        $(".lblq ").hide();
                        $('.code').html(data.code);
                        $(".cflq").show();
                    }else if (data.result == 0) {
                        $(".lblq ").hide();
                        alert("礼包被抢完了，下次再来哦!");
                    }
                },
                error:function(){
                    alert("领取失败，请稍后再试!")
                }
            })
        };
    });
    // 关闭礼包窗口
    $('.gift_close').click(function(){
        $(".gift_slider").hide();
        $('.slider_bg').hide();
    });
    //微博跳转地址
    $(".wb_l").click(function(){
        window.location="http://weibo.com/jztpy";
    });
       //百度贴吧跳转地址
    $(".bd_l").click(function(){
        window.location="http://tieba.baidu.com/f?ie=utf-8&kw=%E8%88%B0%E6%8C%87%E5%A4%AA%E5%B9%B3%E6%B4%8B&fr=search";
    });
});