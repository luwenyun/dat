    var flag = 0;
    //自定义ajax
    var ajaxSend = function(data, url, mysuccess, myerror) {
        $.ajax({
            url: url,
            data: data,
            type: "POST",
            dataType: "json",
            cache: false,
            success:mysuccess,
            error: myerror
        });
    };
    //自定义输入检查
    var check = function(obj, label) {
            obj.on("input propertychange",function() {
                console.log("haha");
                if ($(".input-tip").length!=0) {
                    $(".input-tip").remove();
                }
                var text = obj.val();
                if (text.match(/[*/\\:"?<>|]/) != null) {
                    var _label= $('<span class="input-tip"></span>');
                    obj.parent(".input").append(_label);
                    _label.text(label+ '名不能含有非法字符*/\\:"?<>|');
                    return;
                }
                if (text.length > 15) {
                    var _label = $("<span class='input-tip'></span>");
                    obj.parent(".input").append(_label);
                    _label.text(label + '名长度不能超过15个字符');
                    return;
                }
             });
        };
        //检查数据库中是否已经含有用户想要定义的表名
    check($("#set-filename"), "文件");
    //文件名输入框失去焦点时触发的事件
    $("#set-filename").blur(function() {
        //检查输入文件名框是否为空值
        var value = $(this).val();
        if (value.trim() == "") {
            if($(".input-tip").length==0){
                var label = $('<span class="input-tip"></span>');
                $("#set-filename").parent(".input").append(label);
                label.text("请输入文件名！");
            }
        }else{
            //检查文件名是否已经存在
            var data = {};
            data["fileName"] = value;
            url = "http://localhost:8080/api/excel/check";
            var mysuccess = function(result) {
                if (result.error != "") {
                    alert(result.error);
                    return;
                }
                if (result.exist == true) {
                    if($(".input-tip").length==0){
                        var label = $("<span class='input-tip'></span>");
                        $("#set-filename").parent(".input").append(label);
                        label.text("该文件名已经存在，请重新输入！");
                    }
                }
            };
            var myerror = function(result) {
                alert("服务器发生错误！");
            };
            ajaxSend(data, url, mysuccess, myerror);
        }
        //如果发生错误，下一步按钮应该添加diabled属性
        if($(".input-tip").length!=0){
            if($(".show-set .next").hasClass('btn-disabled')!=true){
                $(".show-set .next").addClass('btn-disabled');
            }
        }else{
            if($(".show-set .next").hasClass('btn-disabled')==true){
                $(".show-set .next").removeClass('btn-disabled');
            }
        }
    });
    //列出用户的历史上创建的仓库名，让用户决定是否创建新的仓库还是选择现有的仓库
    check($("#set-whname"), "仓库");
    $("#set-whname").focus(function() {
        // console.log("select");
        if ($(".select-whname").length==0) {
            var data = null;
            var url = "http://localhost:8080/api/folder/list";
            var mysuccess = function(result) {
                if (result.error != "") {
                    alert("服务器发生错误！");
                    return;
                }
                //显示已有仓库名列表
                var ul = $('<div class="select-whname"><ul></ul></div>');
                $("#set-whname").parent(".input").append(ul);
                for (var i = 0; i < result.result.length; i++) {
                    var li = $('<li class="whname-li"></li>');
                    li.text(result.result[i]);
                    $(".select-whname ul").append(li);
                    if(i==10){
                        break;
                    }
                }
            };
            var myerror = function(result) {
               console.log("服务器发生错误！")
            };
            ajaxSend(data, url, mysuccess, myerror);
        }
        //当仓库名输入框获得焦点时，如果已经存在仓库名列表并隐藏时。取消隐藏
        $(".select-whname").removeClass("fn-hide");
    }).blur(function(){
        var value = $(this).val();
        if (value.trim() == "") {
            if($(".input-tip").length==0){
                var label = $('<span class="input-tip"></span>');
                $(this).parent(".input").append(label);
                label.text("请输入仓库名！");
            }
        }
        if ($(".select-whname").hasClass('fn-hide')!=true) {
            $(".select-whname").addClass("fn-hide");
        }
        //如果发生错误，下一步按钮应该添加diabled属性
        if($(".input-tip").length!=0){
            if($(".show-set .next").hasClass('btn-disabled')!=true){
                $(".show-set .next").addClass('btn-disabled');
            }
        }else{
            if($(".show-set .next").hasClass('btn-disabled')==true){
                $(".show-set .next").removeClass("btn-disabled");
            }
        }
    });
    //选择现有的仓库名
    //当鼠标进入仓库名列表添加背景
    $(".input").on("mouseenter",".whname-li",function() {
        /* Act on the event */
        console.log("mouse");
        $(this).addClass('whname-active');
    });
    $(".input").on("mouseleave",".whname-li",function() {
        /* Act on the event */
        $(this).removeClass('whname-active');
    });
    $(".input").on("mousedown",".whname-li",function(){
        console.log("click");
        var text = $(this).text();
        $("#set-whname").val(text);
        //动态(脚本)赋值没有触发oninput事件，所以在这里应该清除input-tip类
        if($(".input-tip").length!=0){
            $(".input-tip").remove();
        }
        $(".select-whname").addClass('fn-hide')
    });
    //show-data模块
    //上一步点击事件
    $(".show-data .pre").on("click",function() {
        //删除表
        $(".table").empty();
        //删除表名
        $(".file-name").empty();
        $(".list-1").addClass("flag");
        $(".list-2").removeClass("flag");
        $(".show-data").addClass("fn-hide");
        $(".show-import").removeClass("fn-hide");
    });
    //下一步点击事件
    $(".show-data .next").on("click",function() {
        console.log("data");
        $(".list-2").removeClass("flag");
        $(".list-3").addClass("flag");
        $(".show-data").addClass("fn-hide");
        $(".show-set").removeClass("fn-hide");
    });
    //show-set模块
    $(".show-set .pre").on("click",function() {
        $(".list-2").addClass("flag");
        $(".list-3").removeClass("flag");
        $(".show-set").addClass("fn-hide");
        $(".show-data").removeClass("fn-hide");
    });
    $(".show-set .next").on("click",function() {
        console.log("set");
        var formData = new FormData();
        var fileName = $("#set-filename").val();
        var folderName = $("#set-whname").val();
        var label = $("#set-type").val();
        var comments = $("#set-comments").val();
        // var data={"fileName":fileName,"folderName":folderName,"label":label,"comments":label};
        var data = {};
        data["fileName"] = fileName;
        data["folderName"] = folderName;
        data["label"] = label;
        data["comments"] = comments;
        console.log(data);
        formData.append("fileName", fileName);
        formData.append("folderName", folderName)
        formData.append("label", label);
        formData.append("comments", comments);
        $.ajax({
            url: "http://localhost:8080/api/excel/create",
            type: "POST",
            data: data,
            dataType: "json",
            cache: false,
            success: function(result) {
                if (result.error != "") {
                    alert("提交失败");
                    return;
                }
                
                //如果成功插入文件，则模拟单击事件进入数据处理阶段。
                console.log("$('.nav-title[flag=1]'')",$('.nav-title[flag=1]'));
                $(".nav-title[flag=1]").trigger('click');
            },
            error: function(result) {
                alert("服务器发生错误！");
            },
            complete:function(){
                //把进度标识回到开始状态
                $(".list-3").removeClass("flag");
                $(".list-1").addClass("flag");
                $(".upload-body .table").empty();
                $(".show-set").addClass("fn-hide");
                $(".show-import").removeClass("fn-hide");
            }

        });
    });
    //上传文件
    var uploadFile = function(form1, position, fileTotal) {
        if (form1 == undefined && form1 == null) {
            console.log("failed");
            return;
        }
        var xhr = $.ajax({
            url: "http://localhost:8080/api/excel/upload",
            method: "POST",
            dataType: "json",
            data: form1,
            cache: false,
            contentType: false,
            processData: false,
            xhr: function() {
                var xhr = $.ajaxSettings.xhr();
                //上传进度条
                xhr.upload.onprogress = function(event) {
                    uploadProgerss(event, position);
                };
                //开始上传提示
                xhr.upload.onloadstart = function(event) {
                    //show(event);
                    // console.log("onloadstart");
                    $(".upload-progress").append('<div class="progress-tip" flag="' + position + '"><span>正在上传...</span><a href="javascript:void(0);" id="cancel-btn' + position + '">取消</a></div>');
                    //取消
                    var cancelbtn = "#cancel-btn" + position;
                    $(cancelbtn).click(function() {
                        $(this).parent(".progress-tip").remove();
                        console.log("chidren.length=", $(".upload-progress").children().length);
                        if ($(".upload-progress").children().length == 0) {
                            $(".upload-progress").addClass("fn-hide");
                            $(".choose-file").removeClass("fn-hide");
                        };
                        xhr.abort();
                    });
                }
                return xhr;
            },
            success: function(result) {
                flag += 1;
                // console.log("flag=", flag, fileTotal);
                if (result.error != ""||result.status==0) {
                    console.log(result.error);
                    return;
                }
                showData(result);
                if (flag === fileTotal) {
                    // console.log("enter");
                    $(".upload-progress").empty();
                    $(".upload-progress").addClass("fn-hide");
                    $(".choose-file").removeClass("fn-hide");
                    $(".show-data").removeClass("fn-hide");
                    $(".show-import").addClass("fn-hide");
                }
                // table(result);
            },
            error: function() {
                console.log("上传失败");
            }
        }).progress(function() {
            console.log("hah", result);
        });
        console.log(xhr);
        return xhr;
    };
    //显示数据
    var showData = function(result) {
            $(".list-1").removeClass("flag");
            $(".list-2").addClass("flag");
            var h2 = $('<h2 id="filename"></h2>');
            h2.text(result.result[0].name);
            $(".file-name").append(h2);
            var jqEle = $(".show-data .table");
            var rows = 30;
            // table
            console.log(result.result);
            var table = $("<table></table>");
            for (var i = 0; i < rows; i++) {
                var arr = result.result[0].data[i];
                // tr
                var tr = $("<tr></tr>");
                for (var j = 0; j < arr.length; j++) {
                    // th/td
                    if (i == 0) {
                        var th = $("<th></th>");
                        th.text(arr[j]);
                        tr.append(th);
                    } else {
                        var td = $("<td></td>");
                        td.text(arr[j]);
                        tr.append(td);
                    }
                }
                table.append(tr);
            }
            jqEle.append(table);
        }
        //上传文件进度条
    var uploadProgerss = function(event, position) {
        if (event.lengthComputable) {
            var progressInfo = parseInt(event.loaded * 100 / event.total);
            console.log(progressInfo + '%', position);
            console.log($('.upload-progress div[flag="' + position + '"] span').text());
            $('.upload-progress div[flag="' + position + '"] span').text("正在上传..." + progressInfo + '%');
        }

    };
    //选择文件进行上传
    $("#upload-btn").on("click",function() {
        var my_input = $("<input type='file' name='files' multiple id='my-input'>");
        my_input.val("");
        my_input.click();
        console.log("shang c");
        my_input.change(function() {
            console.log("enter");
            flag=0;
            var formData = new FormData();
            var fileList = my_input[0].files;
            var fileTotal = fileList.length;
            $(document).ajaxStart(function() {
                $(".choose-file").addClass("fn-hide");
                $(".upload-progress").removeClass("fn-hide");
            });
            for (var i = 0; i < fileList.length; i++) {
                var file = fileList[i];
                console.log("file:"+file);
                if((file.type.indexOf("application/vnd.ms-excel"))===-1||file.size>5E7||file.size==0){
                  file=null;
                  //错误提示
                    var  errText="你选择的文件中有不合格的文件类型或者文件为空和过大！";
                    $(".error-tip").text(errText).removeClass("fn-hide");
                    //设置定时显示错误
                    var timer=setInterval(function(){
                        if($(".error-tip").hasClass('fn-hide')){
                            clearInterval(timer);
                        }
                        $(".error-tip").addClass("fn-hide");
                    }, 5000);
                  console.log("你选择的文件中有不合格的文件类型或者文件为空和过大！");
                  return;
                }
                formData.append("file", file);
                //上传文件
               uploadFile(formData, i, fileTotal);
            }
            formData = null;
            $("#my_input").remove();
        });
        //change end
        return false;
    });