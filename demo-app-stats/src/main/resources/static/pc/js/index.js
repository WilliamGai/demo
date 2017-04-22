$(document).ready(function() {
  var mySwiper = new Swiper('.swiper_2 .swiper-container', {
    // direction: 'vertical',
    // loop: true,

    // 如果需要分页器
    pagination: '.swiper_2 .swiper-pagination',

    // 如果需要前进后退按钮
    nextButton: '.swiper_2 .swiper-button-next',
    prevButton: '.swiper_2 .swiper-button-prev',
    // autoplay: 2000,
    // 如果需要滚动条
    // scrollbar: '.swiper-scrollbar',
  });
  $(".news_1").mouseenter(function() {
    var index = $(this).index();
    $(".news_1").removeClass("news_active");
    $(this).addClass("news_active");
    $(".news_div").removeClass("news_div_active");
    $(".news_div").eq(index).addClass("news_div_active");
  });
  // 战舰介绍切换
  $('.js_li').click(function() {
    var index = $(this).index();
    $('.js_li').removeClass("js_action");
    $(this).addClass("js_action");
    $(".js_div").removeClass("js_div_action");
    $(".js_div").eq(index).addClass("js_div_action");
  });
  // 游戏展示部分
  $(".game_s_Li").click(function() {
    var index = $(this).index();
    $(".game_s_Li").removeClass("game_s_ac");
    $(this).addClass("game_s_ac");
    $(".g_d_div").removeClass("g_d_action");
    $(".g_d_div").eq(index).addClass("g_d_action");
    if (index == 1) {
      var mySwiper = new Swiper('.swiper_3 .swiper-container', {
        // direction: 'vertical',
        loop: true,
        autoplay: 2000,
        // 如果需要分页器
        pagination: '.swiper_3 .swiper-pagination',

        // 如果需要前进后退按钮
        nextButton: '.swiper_3 .swiper-button-next',
        prevButton: '.swiper_3 .swiper-button-prev',

        // 如果需要滚动条
        // scrollbar: '.swiper-scrollbar',
      });
    } else if (index == 2) {
      var mySwiper = new Swiper('.swiper_4 .swiper-container', {
        // direction: 'vertical',
        loop: true,
        autoplay: 2000,
        // 如果需要分页器
        pagination: '.swiper_4 .swiper-pagination',
        // 如果需要前进后退按钮
        nextButton: '.swiper_4 .swiper-button-next',
        prevButton: '.swiper_4 .swiper-button-prev',
        // 如果需要滚动条
        // scrollbar: '.swiper-scrollbar',
      });
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
  // 轮播视频1
  var v = 1;
  $(".video_st_1").click(function(){
    if (v==1) {
      $(document).find("video")[1].play();
      $(".video_radio").fadeOut();
      $(this).attr("src","imgs/pause.png");
      v = 2;
    }else if (v == 2) {
      $(document).find("video")[1].pause();
      $(".video_radio").fadeIn();
      $(this).attr("src","imgs/start.png");
      v = 1;
    }
  });
  $(".sd_1").mouseenter(function(){
      $(".video_st_1").show();
      $(this).mouseleave(function(){
      $(".video_st_1").hide();
  });
  });
  // 轮播视频2
  var t = 1;
  $(".video_st_2").click(function(){
    if (t==1) {
      $(document).find("video")[2].play();
      $(this).attr("src","imgs/pause.png");
      $(".video_radio").fadeOut();
      t = 2;
    }else if (t == 2) {
      $(document).find("video")[2].pause();
      $(".video_radio").fadeIn();
      $(this).attr("src","imgs/start.png");
      t = 1;
    }
  });
  $(".sd_2").mouseenter(function(){
      $(".video_st_2").show();
      $(this).mouseleave(function(){
      $(".video_st_2").hide();
  });
  });
  // head
  $(".hd_li_left").mouseover(function() {
    var index = $(this).index();
    $(".light_1").removeClass("show_light");
    $(".light_1").eq(index).addClass("show_light");
    $(this).mouseout(function() {
      $(".light_1").removeClass("show_light");
      $(".light_1").eq(0).addClass("show_light");
    })
  });
  $(".hd_li_right").mouseover(function() {
    var index = $(this).index();
    $(".light_2").removeClass("show_light");
    $(".light_1").removeClass("show_light");
    $(".light_2").eq(index).addClass("show_light");
    $(this).mouseout(function() {
      $(".light_2").removeClass("show_light");
      $(".light_1").eq(0).addClass("show_light");
    })
  });
})