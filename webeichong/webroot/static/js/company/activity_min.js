$(document).ready(function(){queryEichongInfo()});function queryEichongInfo(){$.ajax({type:"POST",url:basepath+"/web/list/findByType.do?releType=1",dataType:"json",data:page.params,success:function(a){var e=a.data;var d="";for(var c=0;c<e.length;c++){var b=e[c];if(b!=""){d+='<li class="Row"><span>'+timestampformat(b.releCreatedate)+'</span><a href="/newsDetail.html?type=1&pkId='+b.pkRelease+'">'+b.releTitle+"</a></li>"}}$("#huodong").html(d);initPage(a,queryEichongInfo)}})}function timestampformat(a){return new Date(a).format("yyyy-MM-dd")};