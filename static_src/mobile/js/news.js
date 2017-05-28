$(document).ready(function(){
    $('.news_title').bind("touchstart",function(){
        $(this).css("color","#cb4820");
        $(this).bind("touchend",function(){
            $(this).css("color","#a1a1a1");
        })
    });
});