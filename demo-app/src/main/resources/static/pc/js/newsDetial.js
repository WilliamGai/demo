$(document).ready(function() {
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
  // 导航切换
  $(".news_d_ul li").click(function() {
      var index = $(this).index();
      $(".news_li_2").removeClass("news_li_choose");
      $(".news_li_2").eq(index).addClass("news_li_choose");
      $('.news_d_div').removeClass("new_div_show");
      $('.news_d_div').eq(index).addClass("new_div_show");
    })
    // head
  $(".hd_li_left").mouseover(function() {
    var index = $(this).index();
    $(".light_1").removeClass("show_light");
    $(".light_1").eq(index).addClass("show_light");
    $(".light_2").removeClass("show_light");
    $(this).mouseout(function() {
      $(".light_1").removeClass("show_light");
      $(".light_2").eq(0).addClass("show_light");
    })
  });
  $(".hd_li_right").mouseover(function() {
    var index = $(this).index();
    $(".light_2").removeClass("show_light");
    $(".light_1").removeClass("show_light");
    $(".light_2").eq(index).addClass("show_light");
    $(this).mouseout(function() {
      $(".light_2").removeClass("show_light");
      $(".light_2").eq(0).addClass("show_light");
    })
  });
});