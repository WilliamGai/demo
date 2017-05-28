$(document).ready(function() {
    $(".ld_li").click(function() {
        var dNum = $(this).attr("dd");
        $(".ld_div").removeClass("slider_div");
        $(this).next().addClass("slider_div");
        $(".ld_div").eq(dNum - 1).find("img").addClass("ld_img_slider");
        if (dNum == 1) {
            $(".ld_img_1").attr("src", "imgs/001.jpg");
            $(".ld_img_2").attr("src", "imgs/02.jpg");
            $(".ld_img_3").attr("src", "imgs/03.jpg");
            $(".ld_img_4").attr("src", "imgs/04.jpg");
            $(".ld_img_5").attr("src", "imgs/05.jpg");
        } else if (dNum == 2) {
            $(".ld_img_2").attr("src", "imgs/002.jpg");
            $(".ld_img_1").attr("src", "imgs/01.jpg");
            $(".ld_img_3").attr("src", "imgs/03.jpg");
            $(".ld_img_4").attr("src", "imgs/04.jpg");
            $(".ld_img_5").attr("src", "imgs/05.jpg");
        } else if (dNum == 3) {
            $(".ld_img_3").attr("src", "imgs/003.jpg");
            $(".ld_img_1").attr("src", "imgs/01.jpg");
            $(".ld_img_2").attr("src", "imgs/02.jpg");
            $(".ld_img_4").attr("src", "imgs/04.jpg");
            $(".ld_img_5").attr("src", "imgs/05.jpg");
        } else if (dNum == 4) {
            $(".ld_img_4").attr("src", "imgs/004.jpg");
            $(".ld_img_1").attr("src", "imgs/01.jpg");
            $(".ld_img_2").attr("src", "imgs/02.jpg");
            $(".ld_img_3").attr("src", "imgs/03.jpg");
            $(".ld_img_5").attr("src", "imgs/05.jpg");
        } else if (dNum == 5) {
            $(".ld_img_5").attr("src", "imgs/005.jpg");
            $(".ld_img_2").attr("src", "imgs/02.jpg");
            $(".ld_img_3").attr("src", "imgs/03.jpg");
            $(".ld_img_4").attr("src", "imgs/04.jpg");
            $(".ld_img_1").attr("src", "imgs/01.jpg");
        }
    });
    // 微信
    $(".gf_wx").click(function() {
        $('.gfwx_div').fadeToggle();
    });
    $('.wx_C').mouseenter(function() {
        $('.wx_box').fadeIn();
    });
    $('.wx_C').mouseleave(function() {
        $('.wx_box').fadeOut();
    });
    var a = 0;
    $('.s_o_c').click(function() {
        if (a == 0) {
            $(".slider_box").addClass('slider_close');
            a = 1;
        } else if (a == 1) {
            $(".slider_box").removeClass('slider_close');
            a = 0;
        }
    });
    // video
    var video;
    $(".video_start").click(function() {
        $('.video_bg').fadeIn();
        $(".video_C").fadeIn();
        $(document).find("video")[0].play();
    });
    $(".close").click(function() {
        $('.video_bg').fadeOut();
        $(".video_C").fadeOut();
        video = $("#video")
        $(document).find("video")[0].pause();
    });
     // 礼包领取
    $(".lq_close").click(function() { //关闭各种礼包框
        $(".lq_slider").hide();
        $('.slier_bg').hide();
        $(".lqcg").hide();
        $(".cflq").hide();
    });
    $(".lqlb").click(function() {
        $('.slier_bg').show();
        $(".lq_slider").show();
    });
    $(".lq_btn").click(function() {
        var phone = $(".num_input").val();
        if (!(/^1(3|4|5|7|8)\d{9}$/.test(phone))) {
            alert("请输入正确手机号码!");
        } else {
            $.ajax({
                url: "http://jztpy.sincetimes.com/get_a_code?sn=ztpy_home_20170208",
                dataType: "json",
                data: {
                    who: phone,
                },
                type: "POST",
                success: function(data) {
                    var data = eval(data);
                    console.log(data);
                    if (data.result == 1) {//领取成功
                        $(".lq_slider").hide();
                        $('.slier_bg').show();
                        $(".lqcg").show();
                        $(".lqcg_jhm").html(data.code);
                    } else if (data.result == 2) {//领取重复
                        $(".lq_slider").hide();
                        $('.slier_bg').show();
                        $(".cflq").show();
                        $(".lqcg_jhm").html(data.code);
                    } else if (data.result == 0) {
                        alert("礼包被抢完了，下次再来哦!");
                    }else if(data.result == -1){
                        alert("活动时间还未到，敬请期待~")
                    }else if(data.result == -2){
                        alert("活动已经结束了~")
                    }else if(data.result == -3){
                        alert("额，好像出了点问题~")
                    }
                },
                error: function() {
                    alert("领取失败，请稍后再试!")
                }
            })
        };
    });
})