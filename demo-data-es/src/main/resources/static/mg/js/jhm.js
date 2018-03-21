String.prototype.getQuery = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = this.substr(this.indexOf("\?") + 1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }
    var s = window.location.href;
    var sn = s.getQuery("sn");
    var openid = s.getQuery("openid");
    var dt = new Date();
    var f_dt = dt.getFullYear() + "-" + (dt.getUTCMonth() + 1);
    if (!openid) {
    	if (localStorage.getItem(f_dt)) {
            openid = localStorage.getItem(f_dt);
            console.log('local openid=', openid);
        }
    }
    
    if (!openid) {
        window.location = "http://service.sincetimes.com/signon?back_url=" + "jihuoma_wx?sn="+sn;
    } else {
    	localStorage.setItem(f_dt, openid);
    	window.history.replaceState({},0,'/jihuoma_wx?sn='+sn);
        //{"result":2,"code":"7sJt9bn4RnBTAY"}
        $.ajax({
            type: "get",
            dataType: 'json',
            url: "http://jztpy.sincetimes.com/get_a_code",
            data: {
                who:openid,
                sn:sn

            },
            success: function(data) {
                var data_var=eval(data);
                console.log('data=',data);
                console.log('data_var=',data_var);
                // 领取标志,激活码,0被抢光,1第一次领取，2已经领取,-1时间没到,-2时间过了,-3这个激活码地址不存在
                if(data.result==1){
                    $('#_code_id').html(data.code);
                }else if(data.result==2) {
                    $('#_code_id').html(data.code);
                }else if(data.result==0) {
                    alert('激活码已经被抢光');
                }else if(data.result==-1) {
                    alert('活动暂未开启');
                }else if(data.result==-2) {
                    alert('活动已经结束');
                }else if(data.result==-3) {
                    alert('激活码不存在');
                }

            },
            error: function() {
                console.log("fail!!!,请刷新再试!");
            }
        });

    }