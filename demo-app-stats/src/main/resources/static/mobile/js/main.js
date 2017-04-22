$(document).ready(function() {
    function isWeiXin() {
        var ua = window.navigator.userAgent.toLowerCase();
        if (ua.match(/MicroMessenger/i) == 'micromessenger') {
            return true;
        } else {
            return false;
        }
    };
    $('.down_btn').click(function(){
        window.location="http://jztpy.sincetimes.com/mobile_new/download_img.html";
        // if (isWeiXin() == true) {
        //     $('.wx_bg').show();
        // } else {
        //     if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {//iphone下载地址
        //         window.location="https://itunes.apple.com/cn/app/rong-yao-zhan-jian-quan-xin/id1179709510?l=zh&ls=1&mt=8";
        //     }else if(navigator.userAgent.match(/android/i)){//android下载地址
        //         window.location="";
        //     }
        // }
    });
    $('.wx_bg').click(function() {
        $(this).hide();
    })
});