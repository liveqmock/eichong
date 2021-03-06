// 获取设备信息==============
// checkDeviceType();
// function checkDeviceType() {
//     if (localStorage.deviceType) {
//         return;
//     }
//     var browser = {
//         versions: function () {
//             var u = navigator.userAgent, app = navigator.appVersion;
//             return {
//                 trident: u.indexOf('Trident') > -1,
//                 presto: u.indexOf('Presto') > -1,
//                 webKit: u.indexOf('AppleWebKit') > -1,
//                 gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,
//                 mobile: !!u.match(/AppleWebKit.*Mobile.*/),
//                 ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
//                 android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1,
//                 iPhone: u.indexOf('iPhone') > -1,
//                 iPad: u.indexOf('iPad') > -1,
//                 webApp: u.indexOf('Safari') == -1
//             };
//         }(),
//         language: (navigator.browserLanguage || navigator.language).toLowerCase()
//     }
//     if (!(browser.versions.mobile)) {
//         //alert("not mobile");
//     } else {
//         if (browser.versions.ios) {
//             localStorage.deviceType = 2;
//         } else if (browser.versions.android) {
//             localStorage.deviceType = 1;
//         }
//     }
// }
// 获取微信版本号
// getWxUserAgent();
// function getWxUserAgent() {
//     var wechatInfo = navigator.userAgent.match(/MicroMessenger\/([\d\.]+)/i);
//     if (!wechatInfo) {
//         layer.closeAll();
//         layer.open({
//             content: '本活动仅支持微信',
//             style: 'border:none; background-color:#cccccc; color:#000;font-size:36/@r;',
//             btn: ['我知道了'],
//             shadeClose: false,
//             anim: 'up',
//             fixed: true
//         });
//         return false;
//     } else if (wechatInfo[1] < "5.0") {
//         layer.closeAll();
//         layer.open({
//             content: '该支付仅支持微信5.0以上版本',
//             style: 'border:none; background-color:#cccccc; color:#000;font-size:36/@r;',
//             btn: ['我知道了'],
//             shadeClose: false,
//             anim: 'up',
//             fixed: true
//         });
//         return false;
//     }
//     return true;
// }
//输入框验证===============
$('.accountTab li').click(function () {
    var flag = $(this).hasClass('active');
    clearStyle();
    if (flag) {
        $('#chargeValue').val('');
    } else {
        $(this).addClass('active');
        $('#chargeValue').val($(this).attr('data-val'));
    }

})
function clearStyle() {
    $('.accountTab li').removeClass('active');
}
// 点击支付按钮=======
$('#payBtn').click(function () {
    // if (getWxUserAgent()) {
    //     getPrepayId();
    // }
    if($('#chargeValue').val()==""){
        layer.closeAll();
        layer.open({
            content: "请输入预充金额",
            style: 'border:none; background-color:#cccccc; color:#000;font-size:36/@r;',
            btn: ['我知道了'],
            shadeClose: false,
            anim: 'up',
            fixed: true
        });

    }else if($('#chargeValue').val()<=1){
        $('#chargeValue').focus();
        $('#chargeValue').prop('placeholder', '充值金额不少于2元');
        $('#chargeValue').val("");

    }
    else {
        getPrepayId();
    }

    // alert($('#chargeValue').val());
    // 判断充电枪连接状态
    // layer.closeAll();
    // layer.open({
    //     content: '<div class="guyStyle">充电枪未连接</div><div class="endTip">请连接充电枪</div>',
    //     style: 'border:none; background-color:#cccccc; color:#000;font-size:36/@r;',
    //     btn: ['我知道了'],
    //     shadeClose: false,
    //     anim: 'up',
    //     fixed: true
    // });
})
// 获取桩体信息==============================
init();
function init() {
    var elPiPowerSize = localStorage.getItem("elPiPowerSize");
    var elPiElectricPileName = localStorage.getItem("elPiElectricPileName");
    var elPiOutputCurrent = localStorage.getItem("elPiOutputCurrent");
    var currentRate = localStorage.getItem("currentRate");
    var pileCode = localStorage.getItem("pileCode");
    var gunCode = localStorage.getItem("gunCode");

//	alert(elPiPowerSize+"-"+elPiElectricPileName+"-"+elPiOutputCurrent+"-"+currentRate);
    if (elPiPowerSize == 6) {
        $('#elPiPowerSize').html("3.5");
    } else if (elPiPowerSize == 15) {
        $('#elPiPowerSize').html("7");
    } else if (elPiPowerSize == 172) {
        $('#elPiPowerSize').html("30");
    } else if (elPiPowerSize == 171) {
        $('#elPiPowerSize').html("37.5");
    } else if (elPiPowerSize == 173) {
        $('#elPiPowerSize').html("45");
    } else if (elPiPowerSize == 169) {
        $('#elPiPowerSize').html("60.5");
    } else if (elPiPowerSize == 170) {
        $('#elPiPowerSize').html("60");
    } else if (elPiPowerSize == 43) {
        $('#elPiPowerSize').html("120");

    }
    $('#elPiOutputCurrent').html(parseInt(elPiOutputCurrent));
    $('#currentRate').html(parseFloat(currentRate).toFixed(2));
    $('#elPiElectricPileName').html(elPiElectricPileName);

    initWxJs();
}
function initWxJs() {
    $.ajax({
        type: "get",
        url: basePath + config,
        async: true,
        dataType: "json",
        data: {
            page: location.href.split('#')[0]
        },
        success: function (datas) {
        	var datas=JSON.parse(datas);
            if (datas.status == 100) {
                var req = datas.data;
                var signature = req.signature;
                var timestamp = req.timestamp;
                var nonceStr = req.nonceStr;
                localStorage.setItem("signature", signature);
                localStorage.setItem("timestamp", timestamp);
                localStorage.setItem("nonceStr", nonceStr);
                wx.config({
                    //  debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: 'wxb26a9c1ed4b0686b',
                    timestamp: timestamp, // 必填，生成签名的时间戳
                    nonceStr: nonceStr, // 必填，生成签名的随机串
                    signature: signature, // 必填，签名
                    jsApiList: ["getLocation", "chooseImage", "hideOptionMenu", 'checkJsApi', 'chooseWXPay']
                    // 必填，需要使用的JS接口列表
                });
                // 通过ready接口处理成功验证
                wx.ready(function () {
                    //  alert("初始化成功！");
                });
                wx.error(function () {
                    //alert("初始化失败！");
                });

            } else {
                layer.closeAll();
                layer.open({
                    content: datas.msg,
                    style: 'border:none; background-color:#cccccc; color:#000;font-size:36/@r;',
                    btn: ['我知道了'],
                    shadeClose: false,
                    anim: 'up',
                    fixed: true
                });
            }
        }
    });

}
function getPrepayId() {
    $.ajax({
        type: "get",
        url: basePath + wxTempOrder,
        async: true,
        dataType: "json",
        data: {
            body: "eichong",
            price: parseFloat($('#chargeValue').val()).toFixed(2) * 100,
            pileCode: localStorage.getItem("pileCode"),
            gunCode: localStorage.getItem("gunCode"),
            device_info: "1111",
            openId: localStorage.getItem("openId")
        },
        success: function (datas) {
        	var datas=JSON.parse(datas);
            //alert(datas.data);
            if (datas.status == 100) {
                var req = datas.data;
                // alert(req);
                toPay(req);
            }
        }
    });
}
// 发起支付=====
function toPay(req) {
//	alert(req.paysign+"|"+req.timestamp+"|"+req.prepayid+"|"+req.noncestr);
    // //发起一个微信请求=======
    wx.chooseWXPay({
        timestamp: req.timestamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
        nonceStr: req.noncestr, // 支付签名随机串，不长于 32 位
        package: 'prepay_id=' + req.prepayid, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
        signType: 'MD5', // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
        paySign: req.paysign, // 支付签名
        success: function (res) {
            /*  alert(res);*/
            toPage("charging.html");
            // 支付成功后的回调函数
        }
    });

}
