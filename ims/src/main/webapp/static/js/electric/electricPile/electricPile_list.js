//获取电桩列表的menuId
var getMenuMap = window.localStorage.getItem('menuMap');
var getCurruntMap = JSON.parse(getMenuMap);
var menuId = getCurruntMap['电桩列表'];
var selectMap = {
    1: 'electricPileType',
    3: 'electricChargeMode',
    4: 'electricPower'
}
$(function () {
    ctrlMenu(menuId);
    //去加载表格的数据
    setTimeout("getElectricPileList()", 100);
    //加载省市
    setTimeout("toLoadProvince('', '#provinceCodeHtml', '.cpyProvinceUl', 'toUnbindEvent')", 200);
    setTimeout("toLoadProvince('', '#OwnProvinceCodeHtml', '.ownCpyProvinceUl', 'toUnbindEvent')", 300);
    setTimeout('initSelects(selectMap)',300);
})
function ctrlMenu(menuId) {
    $.ajax({
        type: "post",
        url: basePath + getSelfButtonTreeUrl,
        async: true,
        data: {
            menuId: menuId
        },
        success: function (req) {
            var data = req.dataObject;
            if(data==null){
                return;
            }
            if(data==''){
                return;
            }
            else {
                for (var i = 0; i < data.length; i++) {
                    var contents = data[i].contents;
                    if (contents.indexOf('显示') > -1) {
                        $('#showBtn').show();
                    }
                    if (contents.indexOf('导入') > -1) {
                        $('#importElectricBtn').show();
                    }
                    if (contents.indexOf('导出') > -1) {
                        $('#exportElectricBtn').show();
                    }
                    if (contents.indexOf('新建') > -1) {
                        $('#addElectric').show();
                    }
                    if (contents.indexOf('删除') > -1) {
                        $('#deleteBtn').show();
                    }
                    if (contents.indexOf('审核上线') > -1) {
                        $('#auditBtn').show();
                    }
                }
            }


        }
    });
}


function toUnbindEvent() {
    $('.pileStatusBlock').unbind();
    $('.electricPileTypeBlock').unbind();
    $('.electricChargeModeBlock').unbind();
    $('.electricPowerBlock').unbind();
    $('.cpyProvinceBlock').unbind();
    $('.cypCityBlock').unbind();
    $('.cpyAreaBlock').unbind();
    $('.pileMakerBlock').unbind();
    $('.ownerShipBlock').unbind();
    $('.ownCpyProvinceBlock').unbind();
    $('.ownCypCityBlock').unbind();
    selectModel();
}
toUnbindEvent();

function getElectricPileList() {
    toGiveHiddenInput();
    initTable("electricListForm", "electricListPage", electricListUrl, electricListCallback);
}
function electricListCallback(req) {
    var data = req.dataObject;
//    var pageNum = req.pager.pageNo;
    var listTr = "";
    for (var i = 0; i < data.length; i++) {
        var dateTime = data[i].gmtCreate.time;
        listTr += '<tr><td><input type="checkbox" name="ids" class="selectedBox" value="' + data[i].id + '"/></td>'
        +'<td class="electricPileList_powerStationName">' + data[i].powerStationName
        +'</td><td class="electricPileList_electricPileCode"><span class="toPileDetail" data-id="' + data[i].id + '" onclick="toLoadPileDetail(this)">' + data[i].code
        + '</span></td><td class="electricPileList_electricPileNum">' + data[i].num
        + '</td><td class="electricPileList_chargeMode">' + data[i].chChargingMethod
        + '</td><td class="electricPileList_powerNum">' + data[i].chPower
        + '</td><td class="electricPileList_headNum">' + data[i].muzzleNumber
        + '</td><td class="electricPileList_pileStatus">' + data[i].chStatus
        + '</td><td class="electricPileList_pileAddress">' + data[i].address
        + '</td><td class="electricPileList_pileOwnner">' + data[i].company
        + '</td><td class="electricPileList_creatTime">' + new Date(dateTime).format("yyyy-MM-dd")
        + '</td><td class="electricPileList_typeSpan">' + data[i].productModel
        + '</td><td class="electricPileList_pileType">' + data[i].type
        + '</td><td class="electricPileList_pileCompany">' + data[i].pileMaker
        + '</td><td class="electricPileList_defaultRateInfo">' + data[i].rateInformationId
        + '</td><td class="electricPileList_pileName">' + data[i].name
        + '</td></tr>';

    }
    $("#myTbogy").html(listTr);
    var arr = ['electricPileList_electricPileCode', 'electricPileList_electricPileNum',
        'electricPileList_chargeMode', 'electricPileList_powerNum', 'electricPileList_headNum',
        'electricPileList_pileStatus', 'electricPileList_pileAddress', 'electricPileList_pileOwnner',
        'electricPileList_creatTime', 'electricPileList_typeSpan', 'electricPileList_pileType',
        'electricPileList_pileCompany', 'electricPileList_defaultRateInfo', 'electricPileList_pileName'];
    toGetLocalStorageInfo(arr);
}
//查询条件部分=========================
//点击桩编号，进去桩详情
function toLoadPileDetail(obj) {
    var electricPileId = $(obj).attr('data-id');
    window.location.href = 'electricPile_detail.html?electricPileId=' + electricPileId;
}
function toGiveHiddenInput() {
    var pileStatusHtmlValue = $('#pileStatusHtml').attr('data-value');
    var powerHtmlValue = $('#powerHtml').attr('data-value');
    var chargingMethodHtmlValue = $('#chargingMethodHtml').attr('data-value');
    var pileMakerIdHtmlValue = $('#pileMakerIdHtml').attr('data-value');
    var typeIdHtmlValue = $('#typeIdHtml').attr('data-value');
    var provinceCodeHtmlValue = $('#provinceCodeHtml').attr('data-value');
    var cityCodeHtmlValue = $('#cityCodeHtml').attr('data-value');
    var areaCodeHtmlValue = $('#areaCodeHtml').attr('data-value');
    var ownerShipHtmlValue = $('#ownerShipHtml').attr('data-value');
    var codeValue = $('input[name=code]').val();
    if (codeValue == "") {
        $('input[name=code]').val('');
    } else {
        $('input[name=code]').val();
    }
    if (pileStatusHtmlValue == "") {
        $('input[name=status]').val('');
    } else {
        $('input[name=status]').val(pileStatusHtmlValue);
    }
    if (powerHtmlValue == "") {
        $('input[name=power]').val('');
    } else {
        $('input[name=power]').val(powerHtmlValue);
    }
    if (chargingMethodHtmlValue == "") {
        $('input[name=chargingMethod]').val('');
    } else {
        $('input[name=chargingMethod]').val(chargingMethodHtmlValue);
    }
    if (pileMakerIdHtmlValue == "") {
        $('input[name=pileMakerId]').val('');
    } else {
        $('input[name=pileMakerId]').val(pileMakerIdHtmlValue);
    }
    if (typeIdHtmlValue == "") {
        $('input[name=typeId]').val('');
    } else {
        $('input[name=typeId]').val(typeIdHtmlValue);
    }
    if (provinceCodeHtmlValue == '') {
        $('input[name=provinceCode]').val('');
    } else {
        $('input[name=provinceCode]').val(provinceCodeHtmlValue);
    }
    if (cityCodeHtmlValue == '') {
        $('input[name=cityCode]').val('');
    } else {
        $('input[name=cityCode]').val(cityCodeHtmlValue);
    }
    if (areaCodeHtmlValue == '') {
        $('input[name=areaCode]').val('');
    } else {
        $('input[name=areaCode]').val(areaCodeHtmlValue);
    }

    if (ownerShipHtmlValue == '') {
        $('input[name=companyId]').val('');
    } else {
        $('input[name=companyId]').val(ownerShipHtmlValue);
    }
}


//功率选择
$('#electricPower').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
});
//充电方式选择
$('#electricChargeMode').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
});
//电桩类型
$('#electricPileType').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
});
//电桩状态
$('.pileStatusUl').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
});
//点击省加载市
$('.cpyProvinceUl').on("click", "li", function () {
    $('#cityCodeHtml').html('请选择');
    $('.cypCityUl').html('');
    $('#cityCodeHtml').attr('data-value', '');
    $('#areaCodeHtml').html('请选择');
    $('.areaUl').html('');
    $('#areaCodeHtml').attr('data-value', '');
    var provinceCodeId = $(this).attr('data-option');
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
    var flag = $(this).attr('data-option');
    if (flag == "") {
        $('#cityCodeHtml').html('请选择');
        $('.cypCityUl').html('');
        $('#cityCodeHtml').attr('data-value', '');
        $('#areaCodeHtml').html('请选择');
        $('.areaUl').html('');
        $('#areaCodeHtml').attr('data-value', '');
    } else {
        toLoadCity(provinceCodeId, '', '#cityCodeHtml', '.cypCityUl', 'toUnbindEvent');
    }
})
//点击市加载区
$('.cypCityUl').on("click", "li", function () {
    $('#areaCodeHtml').html('请选择');
    $('.areaUl').html('');
    $('#areaCodeHtml').attr('data-value', '');
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
    var flag = $(this).attr('data-option');
    var cityCodeId = '';
    var provinceCodeHtmlValue='';
    if (flag == "") {
        $('#areaCodeHtml').html('请选择');
        $('.areaUl').html('');
        $('#areaCodeHtml').attr('data-value', '');
    } else {
        provinceCodeHtmlValue = $('#provinceCodeHtml').attr('data-value');
        cityCodeId = $(this).attr('data-option');
        //加载公司
        toLoadArea(cityCodeId, '', '#areaCodeHtml', '.areaUl', 'toUnbindEvent');

    }
})
//点击区获取值
$('.areaUl').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
    var flag = $(this).attr('data-option');
    if (flag == "") {
        $('#cityCodeHtml').html('请选择').attr('data-value', '');
        $('.cypCityUl').html('');
        $('#provinceCodeHtml').html('请选择').attr('data-value', '');
        $('.areaUl').html('');
    }

})

//加载资产归属公司
$('.ownCpyProvinceUl').on("click", "li", function () {
    $('#OwnCityCodeHtml').html('请选择').attr('data-value', '');
    $('.ownCypCityUl').html('');
    $('.ownerShipUl').html('');
    $('#ownerShipHtml').html('请选择').attr('data-value','');
    var provinceCodeId = $(this).attr('data-option');
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
    var flag = $(this).attr('data-option');
    if (flag == "") {
        $('#OwnCityCodeHtml').html('请选择').attr('data-value', '');
        $('.ownCypCityUl').html('');
        $('.ownerShipUl').html('');
        $('#ownerShipHtml').html('请选择').attr('data-value','');
    } else {
        toLoadCity(provinceCodeId, '', '#OwnCityCodeHtml', '.ownCypCityUl', 'toUnbindEvent');
    }
})
//点击市加载区
$('.ownCypCityUl').on("click", "li", function () {
    $('.ownerShipUl').html('');
    $('#ownerShipHtml').html('请选择').attr('data-value','');
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
    var flag = $(this).attr('data-option');
    var cityCodeId = '';

    var provinceCodeHtmlValue='';
    if (flag == "") {
        $('.ownerShipUl').html('');
        $('#ownerShipHtml').html('请选择').attr('data-value','');
    } else {
        provinceCodeHtmlValue = $('#provinceCodeHtml').attr('data-value');
        cityCodeId = $(this).attr('data-option');
        //加载公司
        toLoadComponyName2(provinceCodeHtmlValue, cityCodeId);
    }
})


//电桩制造厂商选择
setTimeout('toPileMarkerList()', 200);
$('.pileMarkerUl').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
});
$('.ownerShipUl').on("click", "li", function () {
    $(this).parent().siblings('div.model-select-text').text($(this).text())
        .attr('data-value', $(this).attr('data-option'));
    var flag = $(this).attr('data-option');
    if (flag == "") {
        $('#OwnCityCodeHtml').html('请选择').attr('data-value', '');
        $('.ownCypCityUl').html('');
        $('#OwnProvinceCodeHtml').html('请选择').attr('data-value', '');
        $('.ownerShipUl').html('');
    }
});
$('body').on('click', '#addElectric', function () {
   window.location.href = 'add_electricPile.html';
})
//按钮功能部分
//导出
$('body').on('click', '#exportElectricBtn', function () {
    toGiveHiddenInput();
    var obj = {
        code: $('input[name=code]').val(),
        powerStationName: $('input[name=powerStationName]').val(),
        status: $('input[name=status]').val(),
        power: $('input[name=power]').val(),
        chargingMethod: $('input[name=chargingMethod]').val(),
        pileMakerId: $('input[name=pileMakerId]').val(),
        provinceCode: $('input[name=provinceCode]').val(),
        cityCode: $('input[name=cityCode]').val(),
        areaCode: $('input[name=areaCode]').val(),
        typeId: $('input[name=typeId]').val(),
        companyId: $('input[name=companyId]').val()
    };
    window.location.href = basePath + exportElectricUrl + '?code='
    + obj.code + '&powerStationName='
    + obj.powerStationName + '&status='
    + obj.status + '&power='
    + obj.power + '&chargingMethod='
    + obj.chargingMethod + '&pileMakerId='
    + obj.pileMakerId + '&provinceCode='
    + obj.provinceCode + '&cityCode='
    + obj.cityCode + '&areaCode='
    + obj.areaCode + '&typeId='
    + obj.typeId + '&companyId='
    + obj.companyId;
});
//审核上线开始
$("body").off("click", "#auditBtn").on("click", "#auditBtn", function () {
    layer.closeAll();
    layer.open({
        type: 1,
        offset: '100px',
        title: ["确认", "font-size:12px;text-align:left;padding-left:20px;"],
        shadeClose: false,
        closeBtn: 1,
        area: ['310px', '180px'],//宽高
        content: '确定审核上线吗？',
        btn: ["确定", "取消"],
        yes: function (index, layero) {
            layer.closeAll();
            toAuditElectric();
        },
        cancel: function (index, layero) {
            layer.closeAll();
        }
    });
});
function toAuditElectric() {
    var ids = '';
    $('input[name=ids]').each(function () {
        var flag = $(this).prop('checked');
        if (flag == true) {
            ids += $(this).attr('value') + ',';
        }
    });
    if (!ids) {
        layer.open({
            type: 1,
            offset: '100px',
            title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
            shadeClose: false,
            closeBtn: 1,
            area: ['310px', '160px'],//宽高
            content: '请指定表单元素',
            time: 3000,
            btn: ["确定"]
        });
    } else {
        $.ajax({
            type: "post",
            url: basePath + auditElectricPileUrl,
            async: true,
            data: {
                electricPileIds: ids
            },
            success: function (data) {
                if (data.success == true) {
                    layer.closeAll();
                    layer.open({
                        type: 1,
                        offset: '100px',
                        title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                        shadeClose: false,
                        closeBtn: 1,
                        area: ['310px', '160px'],//宽高
                        content: data.msg,
                        btn: ["确定"],
                        yes: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        },
                        cancel: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        }
                    });

                } else {
                    layer.open({
                        type: 1,
                        offset: '100px',
                        title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                        shadeClose: false,
                        closeBtn: 1,
                        area: ['310px', '160px'],//宽高
                        content: data.msg,
                        btn: ["确定"],
                        yes: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        },
                        cancel: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        }
                    });
                }
            }
        });
    }
}
//审核上线结束
//删除电桩
$("body").off("click", "#deleteBtn").on("click", "#deleteBtn", function () {
    layer.closeAll();
    layer.open({
        type: 1,
        offset: '100px',
        title: ["确认", "font-size:12px;text-align:left;padding-left:20px;"],
        shadeClose: false,
        closeBtn: 1,
        area: ['310px', '180px'],//宽高
        content: $('.deleteContent'),
        btn: ["确定", "取消"],
        yes: function (index, layero) {
            layer.closeAll();
            toDeleteElectric();
        },
        cancel: function (index, layero) {
            layer.closeAll();
        }
    });
});
function toDeleteElectric() {
    var ids = '';
    $('input[name=ids]').each(function () {
        var flag = $(this).prop('checked');
        if (flag == true) {
            ids += $(this).attr('value') + ',';
        }
    });
    if (!ids) {
        layer.open({
            type: 1,
            offset: '100px',
            title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
            shadeClose: false,
            closeBtn: 1,
            area: ['310px', '160px'],//宽高
            content: '请指定表单元素',
            time: 3000,
            btn: ["确定"]
        });
    } else {
        $.ajax({
            type: "post",
            url: basePath + delElectricUrl,
            async: true,
            data: {
                ids: ids
            },
            success: function (data) {
                if (data.success == true) {
                    layer.closeAll();
                    layer.open({
                        type: 1,
                        offset: '100px',
                        title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                        shadeClose: false,
                        closeBtn: 1,
                        area: ['310px', '160px'],//宽高
                        content: '删除成功',
                        btn: ["确定"],
                        yes: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        },
                        cancel: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        }
                    });

                } else {
                    layer.open({
                        type: 1,
                        offset: '100px',
                        title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                        shadeClose: false,
                        closeBtn: 1,
                        area: ['310px', '160px'],//宽高
                        content: data.msg,
                        time: 3000,
                        btn: ["确定"]
                    });
                }
            }
        });
    }
}

function toLoadComponyName2(cpyProvinceCodeValue,cypCityCodeValue , cpyId) {
    var cpyObject = {
        provinceCode: cpyProvinceCodeValue,
        cpyCity: cypCityCodeValue
    };
    if (JSON.stringify(cpyObject) == "{}") {
        $('#ownerShipHtml').html("请选择");
    } else {
        toAjaxCompany(cpyObject,cypCityCodeValue, cpyId);
    }
}
function toLoadOwnShip(data,cpyId){
    var dataModule = data.dataObject;
    if (cpyId == '') {
        var ownShipLi = '<li data-option="" class="data-selected">请选择</li>';
        for (var i = 0; i < dataModule.length; i++) {
            ownShipLi +='<li data-option="' + dataModule[i].cpyId + '" data-cpyNumber="' + dataModule[i].cpyNumber + '">' + dataModule[i].cpyName + '</li>';
        }
    } else {
        var ownShipLi = '<li data-option="" class="data-selected">请选择</li>';
        for (var i = 0; i < dataModule.length; i++) {
            if (dataModule[i].cpyId == cpyId) {
                $('#ownerShipHtml').html(dataModule[i].cpyId);
                ownShipLi +='<li data-option="' + dataModule[i].cpyId + '" data-cpyNumber="' + dataModule[i].cpyNumber + '"  class="data-selected">' + dataModule[i].cpyName + '</li>';
            } else {
                ownShipLi +='<li data-option="' + dataModule[i].cpyId + '" data-cpyNumber="' + dataModule[i].cpyNumber + '">' + dataModule[i].cpyName + '</li>';
            }

        }
    }
    $('.ownerShipUl').html(ownShipLi);
    toUnbindEvent();
}

function toAjaxCompany(cpyObject, cpyId) {
    $.ajax({
        type: "post",
        url: basePath + initCompanyListUrl,
        async: true,
        data: cpyObject,
        success: function (data) {
            if (data.success == true) {
                toLoadOwnShip(data,cpyId);//加载资产归属

            }
        }
    });
}



//监听回车键
$(document).keyup(function (event) {
    if (event.keyCode == 13) {
        getElectricPileList();
    }
});
$("#importElectricBtn").on("click",function(){
    layer.closeAll();
    layer.open({
        type: 1,
        offset: '100px',
        title: ["电桩导入","font-size:12px;padding-left:20px;"],
        shadeClose:false,
        closeBtn:1,
        area: ['310px', '270px'],//宽高
        content:$("#fileInputContainer"),
        //btn:["取消"],
        yes: function(index,layero){
            layer.closeAll();
        },
        cancel: function(index,layero){
            layer.closeAll();
        }
    });
})
//下载模版
$('body').off('click','#downloadXlsx').on('click','#downloadXlsx',function(){
    window.location.href = basePath + '/upload/electricPile_demo.xlsx';
})
$('body').off('click','.quitBtn').on('click','.quitBtn',function(){
    layer.closeAll(); //关闭loading
})
//导入电桩importElectricBtn
/*
layui.use('upload', function () {
    var upload = layui.upload;
    //执行实例
    var uploadInst = upload.render({
        elem: '#importElectricFile'
        , url: basePath + importElectricUrl //上传接口
        , accept: 'file'
        , exts: 'xlsx|xls'
        //, auto: false
        //, bindAction:'#sureImport'
        , before: function (obj) {
            layer.load(1); //上传loading
        }
        ,choose:function(obj){
            var files = obj.pushFile();
            obj.preview(function(index, file, result){
                $('#fileText').html(file.name);
                //console.log(file.name)
            });
            console.log(files)
        }
        , done: function (res, index, upload) {
            if (res.success == true) {
                layer.closeAll(); //关闭loading
                layer.open({
                    type: 1,
                    offset: '100px',
                    title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                    shadeClose: false,
                    closeBtn: 1,
                    area: ['310px', '160px'],//宽高
                    content: '导入成功',
                    btn: ["确定"],
                    yes:function(index,layero){
                        window.location.reload();
                    }
                });

            } else {
                layer.closeAll();
                layer.open({
                    type: 1,
                    offset: '100px',
                    title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                    shadeClose: false,
                    closeBtn: 1,
                    area: ['310px', '160px'],//宽高
                    content: res.msg,
                    time: 2000
                });
            }
        }
    });
});*/
//导入电桩
$('#importElectricfile').change(function(){
    var fileName = $('#importElectricfile').val();
    $('#importElectricText').html(fileName);
})
$('#sureImport').on('click',function(){
    var importElectricText=$('#importElectricText').html();
    if(importElectricText==''){
        return;
    }
    importElectric();
})
function importElectric(){
    var formData = new FormData();
    formData.append("file", $('#importElectricfile')[0].files[0]);
    $.ajax({
        url: basePath + importElectricUrl,
        type: 'POST',
        data: formData,
        async: true,
        cache: false,
        contentType: false,
        processData: false,
        success: function (returndata) {
            layer.closeAll(); //关闭loading
            if(returndata.success==true){
                layer.open({
                    type: 1,
                    offset: '100px',
                    title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                    shadeClose: false,
                    closeBtn: 1,
                    area: ['310px', '160px'],//宽高
                    content: returndata.msg,
                    btn: ["确定"],
                    yes:function(index,layero){
                        layer.closeAll();
                        window.location.reload();
                    },
                    cancel:function(index,layero){
                        layer.closeAll();
                        window.location.reload();
                    }
                });
            }else{
                if(returndata.status == 9001){
                    layer.open({
                        type: 1,
                        offset: '100px',
                        title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                        shadeClose: false,
                        closeBtn: 1,
                        btn: ["确定"],
                        area: ['310px', '160px'],//宽高
                        content: returndata.msg,
                        yes: function (index, layero) {
                            layer.closeAll();
                            window.top.location.href = basePath + toLoginUrl;
                        },
                        cancel: function (index, layero) {
                            layer.closeAll();
                            window.top.location.href = basePath + toLoginUrl;
                        }
                    });
                }else{
                    layer.open({
                        type: 1,
                        offset: '100px',
                        title: ["提示", "font-size:12px;text-align:left;padding-left:20px;"],
                        shadeClose: false,
                        closeBtn: 1,
                        area: ['310px', '180px'],//宽高
                        content: returndata.msg,
                        btn: ["确定"],
                        yes:function(index,layero){
                            window.location.reload();
                        },
                        cancel: function (index, layero) {
                            layer.closeAll();
                            window.location.reload();
                        }
                    });
                }

            }

        }
    });
}