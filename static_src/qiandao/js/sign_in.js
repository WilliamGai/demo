var back_url = "http://jztpy.sincetimes.com/qiandao/sign_in.html";
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
var dt = new Date();
var f_dt = dt.getFullYear()+"-"+(dt.getUTCMonth()+1)+"-"+dt.getDate()+"-"+dt.getHours();
console.log(f_dt,localStorage.getItem(f_dt))
if(openid){
    //window.history.replaceState({},0,'/qiandao/sign_in.html');
    localStorage.setItem(f_dt, openid);
}else{
    if(localStorage.getItem(f_dt)){
        openid=localStorage.getItem(f_dt);
    }else {
        window.location = "http://service.sincetimes.com/signon?back_url=" + back_url;
    }
}
$(document).ready(function() {
    //获取头像昵称url(查询积分接口)
    var Zurl = base_url+"/look_jifen",
        //签到url
        Qurl = base_url+"/qiandao",
        //积分

        Jurl = base_url+"/jifen_rank";
        //openid = "oSSKNwL2ckTrOtptEwLHeYRs9eIc";


    $.ajax({
        url: Zurl,
        dataType: "json",
        type: "GET",
        data: {
            openid: openid,
        },
        success: function(data) {
            var data = eval(data);
            $(".head_img img").attr("src", "" + data.data[1] + "");
            $(".head_p").html(data.data[0]);
            $(".myNum").html(data.data_other);
        }
    });
    //签到
    $(".sign_btn").click(function() {
        console.log('get rank ready', Qurl,openid );
        $.ajax({
            url: Qurl,
            dataType: "json",
            type: "GET",
            data: {
                openid: openid
            },
            success: function(data) {
                console.log('rank', data);
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
                $(".tbody_2").append(str1);
            },
            error:function(){
                alert("查看失败，请刷新再试~")
            }
        })
    };
    $(".close").click(function(){
        $(".numList").hide();
        $(".slider_bg").hide();
        $(".tbody").find("tr").remove();
        $(".tbody_2").find("tr").remove();
    });
});