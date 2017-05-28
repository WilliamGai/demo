var s = window.location.href;
var openid;
console.log("url =", s);
String.prototype.getQuery = function(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = this.substr(this.indexOf("\?") + 1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
openid = s.getQuery("openid");
console.log("openid =", openid);
if(openid){
    window.history.replaceState({},0,'/qiandao/sign_in.html');
}else{
    window.location = "http://service.sincetimes.com/signon?back_url=" + "/qiandao/sign_in.html";
}
$(document).ready(function() {
    //获取头像昵称url(查询积分接口)
    var Zurl = "http://123.56.13.70/look_jifen/shop_01",
        //签到url
        Qurl = "http://123.56.13.70/qiandao/shop_01",
        //积分
        //积分排行榜
        Jurl = "http://123.56.13.70/jifen_rank/shop_01";

    $.ajax({
        url: Zurl,
        dataType: "json",
        type: "GET",
        data: {
            openid: openid,
        },
        success: function(data) {
            var data = eval(data);
            console.log(data);
            $(".head_img img").attr("src", "" + data.data[1] + "");
            $(".head_p").html(data.data[0]);
            $(".myNum").html(data.data_other);
        }
    });
    //签到
    $(".sign_btn").click(function() {
        $.ajax({
            url: Qurl,
            dataType: "json",
            type: "GET",
            data: {
                openid: openid
            },
            success: function(data) {
                var data = eval(data);
                if (data.result == 1 || data.result == 2) {
                    $(".slider_bg").show();
                    $(".slider_1").show();
                    $(".addNum").html(data.data);
                    myData = data.data_other;
                    $(".myNum").html(myData);//总分更改
                } else if (data.result == 0) {
                    $(".slider_bg").show();
                    $(".slider_2").show();
                }
            }
        });
    });
    //签到弹窗关闭
    $(".sd1_btn").click(function() {
        $(".slider_bg").hide();
        $(".slider_1").hide();
        $(".slider_2").hide();
    })
    //积分榜
    $(".numL").click(function(){
        $(".numList").show();
        $(".slider_bg").show();
        numList();
    })
    function numList(){
        $.ajax({
            url:Jurl,
            dataType:"json",
            type:"GET",
            data:{
                openid:openid,
            },
            success:function(data){
                var data = eval(data);
                var str="";
                for(var i = 0;i<data.data.length;i++){
                    str+="<tr>"+
                        "<td style='width:0.72rem;'>"+data.data[i].ranking+"</td>"+
                        "<td style='width:1.533333rem;'><img style='vertical-align:middle;' src="+data.data[i].pic+" alt=''></td>"+
                        "<td style='width:2.613333rem;'>"+data.data[i].name+"</td>"+
                        "<td>"+data.data[i].score+"</td>"+
                    "</tr>";
                };
                $(".tbody").append(str);
                var str1 = "<tr>"+
                            "<td style='width:0.72rem;'>"+data.data_other.ranking+"</td>"+
                            "<td style='width:1.533333rem;'><img style='vertical-align:middle;' src="+data.data_other.pic+"></td>"+
                            "<td style='width:2.613333rem;'>"+data.data_other.name+"</td>"+
                            "<td>"+data.data_other.score+"</td>"+
                    "</tr>";
                $(".jf_table_2").append(str1);
            },
            error:function(){
                alert("查看失败，请刷新再试~")
            }
        })
    };
    $(".close").click(function(){
        $(".numList").hide();
        $(".slider_bg").hide();
    });
});