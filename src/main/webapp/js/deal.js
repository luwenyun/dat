	//rignt-body事件
		//数据处理操作鼠标单击事件
	    $(".oper-li").on("click",function(event) {
	    	$(".li-active").removeClass("li-active");
	        $(this).addClass("li-active");
	    	var flag=$(this).attr("flag");
	    	console.log("flag:"+flag,$(this).text());
	    	$(".active").addClass("fn-hide");
	    	$('.dealwith[flag='+flag+']').removeClass("fn-hide");
	        $(".dealwith").removeClass("active");
	        $('.dealwith[flag='+flag+']').addClass("active");
	        //关联概况
	        if($(this).text()=="关联概况"){
	        	console.log("click 关联概况");
	        	//模拟单击列表视图
	        	$('.view[flag="1"]').trigger('click');
	        }
	        //操作日志
	        if($(this).text()=="操作日志"){
	        	//获取所有已经失效的文件信息
	        	$.ajax({
	        		url:"http://localhost:8080/api/file/all_lose_file",
	        		type:"POST",
	        		dataType:"json",
	        		cache:false,
	        		success:function(result){
	        			if(result.error!=""||result.status=="0"){
	        				console.log(result.error);
	        				return;
	        			}
	        			console.log(result);
	        			//显示失效文件信息
	        			showLogInfo(result);
	        		},
	        		error:function(){
	        			console.log("获取失效文件信息失败");
	        		}
	        	});
	        }
	    });
	    //显示日志信息
	    var showLogInfo=function(result){
	    	var ul=$(".oper-log div ul");
	    	for(var i=0;i<result.result.length;i++){
	    		var li=$("<li class='log-li'><span class='log-title'>[info]</span></li>");
	    		var map=result.result[i];
	    		if(map["type"]=="file"){//文件
	    			var time=$("<span class='log-title'>时间:"+map["ctime"]+"</span>");
	    			var child=$("<span class='log-title'>子表:"+map["child"]+"</span>");
	    			var parent=$("<span class='log-title '>父表:"+map["parent"]+"</span>");
	    			var fileName=$("<span class='log-title ' id='"+"fileName_"+i+"'>文件名:"+map["fileName"]+"</span>");
	    			var folderName=$("<span class='log-title ' id='"+"folderName_"+i+"'>文件夹名:"+map["folderName"]+"</span>");
	    			var type=$("<span class='log-title ' id='"+"type_"+i+"'>类型:"+map["type"]+"</span>");
	    			var comments=$("<span class='log-title'>备注:"+map["comments"]+"</span>");
	    			var div=$("<div><a href='javascript:void(0);' flag='"+i+"' class='log-delete'>删除</a>"+
	    							"<a href='javascript:void(0);' flag='"+i+"' class='log-recover'>恢复</a></div>");
	    			li.append(type).append(fileName).append(folderName).append(child)
	    			.append(parent).append(comments).append(time).append(div);
	    		}else{//文件夹
	    			var time=$("<span class='log-title'>时间:"+map["ctime"]+"</span>");
	    			var folderName=$("<span class='log-title ' id='"+"folderName_"+i+"'>文件夹名:"+map["folderName"]+"</span>");
	    			var type=$("<span class='log-title ' id='"+"type_"+i+"'>类型:"+map["type"]+"</span>");
	    			var div=$("<div><a href='javascript:void(0);' flag='"+i+"' class='log-delete'>删除</a>"+
	    							"<a href='javascript:void(0);' flag='"+i+"'  class='log-recover'>恢复</a></div>");
	    			li.append(type).append(folderName).append(time).append(div);
	    		}
		    	ul.append(li);
		    	//为a绑定事件
		    	logDelete();
		    	logRecover();
	    	}
	    }
	    //永久删除点击事件
	    var logDelete=function(){
	    	$(".log-delete").on("click",function(){
		    	var this_=$(this);
		    	console.log("click delete");
		    	var  data={};
		    	var folderName;
		    	var fileName;
		    	var flag=$(this).attr("flag");
		    	var type=$('#type_'+flag).text().split(":")[1];
		    	console.log("type:"+type);
	    		//删除文件
	    		fileName=$('#fileName_'+flag).text().split(":")[1];
	    		data["fileName"]=fileName;
	    		console.log("fileName:"+fileName);
	    		folderName=$('#folderName_'+flag).text().split(":")[1];
	    		console.log("folderName:"+folderName);
	    		data["folderName"]=folderName;
	    		if(type="file"){
	    			url="http://localhost:8080/api/excel/delete";
	    		}else{
	    			url="http://localhost:8080/api/folder/delete";
	    		}
				$.ajax({
		    		url:url,
					data:data,
					type:"POST",
					dataType:"json",
					cache:false,
					success:function(result){
						if(result.error!=""||result.result!=true){
							console.log(result.error);
							return;
						}
						//把该文件或文件夹信息从操作日志中删除
						this_.parents("li").remove();
					},
					error:function(result){
						console.log("删除失败！");
					},
		    	});
	    	});
	    }
	    //文件恢复点击事件
	    var logRecover=function(){
	    	$(".log-recover").on("click",function(){
		    	var this_=$(this);
		    	console.log("click recover");
		    	var  data={};
		    	var folderName;
		    	var fileName;
		    	var flag=$(this).attr("flag");
		    	var type=$('#type_'+flag).text().split(":")[1];
		    	console.log("type:"+type);
	    		//恢复文件
	    		fileName=$("#fileName_"+flag).text().split(":")[1];
	    		data["fileName"]=fileName;
	    		console.log("fileName:"+fileName);
	    		folderName=$('#folderName_'+flag).text().split(":")[1];
	    		data["folderName"]=folderName;
	    		console.log("folderName:"+folderName);
	    		data["type"]="1";
	    		if(type="file"){
	    			url="http://localhost:8080/api/file/changeFileStatus";
	    		}else{
	    			url="http://localhost:8080/api/folder/changeFolderStatus";
	    		}
				$.ajax({
		    		url:url,
					data:data,
					type:"POST",
					dataType:"json",
					cache:false,
					success:function(result){
						if(result.error!=""||result.result!=true){
							console.log(result.error);
							return;
						}
						if(type=="file"){
							//恢复文件到某个文件夹中
							var li=$('<li>'+
			                                  '<i class="file-icon"></i>'+
			                                  '<span data-value="'+fileName+'">'+fileName+'</span>'+
			                                  '<i class="more-icon">'+
	                                 	'</li>');
		    				$('.wh-folderlist .whfolder div span[data-value="'+folderName+'"]').parents(".whfolder").children("ul").append(li);
		    				//为文件绑定事件
							waitRun();
						}else{
							//添加文件夹到文件夹列表
							var li=$('<li class="whfolder">'+
		    	    						'<div class="folder">'+
		                           				'<i class="folder-icon"></i>'+
		                                    	'<span data-value="'+folderName+'">'+folderName+'</span>'+
		                                    	'<i class="more-icon" style="opacity: 1;"></i>'+
		                                    '</div>'+
		                                    '<ul class="files fn-hide files-hover"></ul>'+
		                                '</li>');
							$(".wh-folderlist").append(li);
							//为文件夹绑定事件
							waitRun();
						}
						//把该文件或文件夹信息从操作日志中删除
						this_.parents("li").remove();
					},
					error:function(result){
						console.log("删除失败！");
					},
		    	});
	    	});
	    }
	    //数据预览方式点击事件
	    $(".condition-ul li").on("click",function(){
	    	console.log($('.condition-ul li').hasClass('active'));
	    	var flag=$(this).attr("flag");
	    	if($(this).hasClass('li-up')){
	    		$('.li-up[flag='+flag+']').addClass("fn-hide");
	    		$('.li-down[flag='+flag+']').removeClass("fn-hide");
	    		$('.condition[flag='+flag+']').removeClass("fn-hide");
	    		if($('.condition-ul li').hasClass('active')==true){
		    		$(".condition-ul .active").removeClass("fn-hide");
		    		if(flag==1){
		    			console.log(flag);
		    			$('.li-down[flag="2"]').addClass("fn-hide");
		    		}else{
		    			$('.li-down[flag="1"]').addClass("fn-hide");
		    		}
		    		$('.condition-active').addClass("fn-hide");
		    		$(".condition-ul li").removeClass("active");
		    		$(".condition").removeClass("condition-active");
	    		}
	    		$(this).addClass("active");
	    		$('.condition[flag='+flag+']').addClass("condition-active");
	    	}else{
	    		$('.li-down[flag='+flag+']').addClass("fn-hide");
	    		$('.li-up[flag='+flag+']').removeClass("fn-hide");
	    		$('.condition[flag='+flag+']').addClass("fn-hide");
	    		if($('.condition-ul li').hasClass('active')==true){
		    		$(".condition-ul li").removeClass("active");
		    		$(".condition").removeClass("condition-active");
	    		}
	    	}
	    	//填充字段列表
            console.log("enter 数据预览");
            //填充条件字段列表
            $(" .dealwith[flag=1] table th").filter(function(index){ 
                var option=$("<option></option>");
                option.text($(this).text());
                $(".fieldSelect").append(option);
            });
            //填充表达式过滤字段列表
            $(" .dealwith[flag=1] table th").filter(function(index){ 
                var option=$("<li><i></i><span></span></li>");
                option.children("span").text($(this).text());
                $(".fieldUl").append(option);
                //为字段绑定单击事件函数-show.js
                fieldUl_li();
            });
            //填充工作表字段
            checkBoxField();
	    });
	    //视图单击事件
	    $(".view").on("click",function(){
	    	var flag=$(this).attr("flag");
	    	$(".view-active").removeClass("view-active");
	    	$(this).addClass("view-active");
	    	$(".list-active").addClass("fn-hide");
	    	$(".list-active").removeClass("list-active");
	    	$(".list-view[flag="+flag+"]").removeClass("fn-hide");
	    	$(".list-view[flag="+flag+"]").addClass("list-active");
			//封装参数
			var data={};
			var fileName=$('.data-deal[flag="1"] .title span').text();
			data["fileName"]=fileName;
			data["folderName"]=$('.data-deal .whfolder ul li span[data-value="'+fileName+'"]').parents(".whfolder").children("div").children("span").text();
			console.log(data);
			//请求关联概况数据
			$.ajax({
				url:"http://localhost:8080/api/file/orm",
				type:"POST",
				dataType:"json",
				data:data,
				cache:false,
				success:function(result){
					if(result.error!=""||result.status=="0"){
						console.log(result.error);
						return;
					}
					console.log(result);
					//渲染数据
					if(flag==1){
						//列表视图
						showListView(result);
					}else{
						//过程视图
						showProgressView(result);
					}
				},
				error:function(){
					console.log("获取表关联概况失败!");
				}
			});
	    });
	    //显示过程列表的关联概况
	    var showListView=function(result){
	    	var table=$('.orm-table table');
	    	//把之前的列表视图移除
	    	table.empty();
	    	var tr_=$('<tr>'+
                       		'<th>关联图表</th>'+
                            '<th>关联子表</th>'+
                            '<th>关联父表</th>'+
                		'</tr>');
	    	table.append(tr_);
	    	var size_=0;
	    	var size_parent=0;
	    	var size_child=0;
	    	var size_chart=0;
	    	var parentsArr=[];//父表数组
	    	var chartsArr=[];//图表数组
	    	var childrenArr=[];//子表数组
	    	//为数组赋值
	    	for(var name in result.result){
	    		if(name=="children"){//children key
	    			if(name.length>size_){
	    				size_child=result.result[name].length;
	    			}
	    			console.log("childrenArr:"+result.result[name]);
	    			childrenArr=result.result[name];
	    		}else if(name="parents"){//parent key
	    			if(name.length>size_){
	    				size_parent=result.result[name].length;
	    			}
	    			console.log("parentsArr:"+result.result[name]);
	    			parentsArr=result.result[name];
	    		}else{//chart key
	    			if(name.length>size_){
	    				size_chart=result.result[name].length;
	    			}
	    			console.log("chartsArr:"+result.result[name]);
	    			chartsArr=result.result[name];
	    		}
	    	}
	    	//修改数组长度
	    	if(size_parent!=0||size_chart!=0||size_child!=0){
	    		var max=size_parent;
	    		if(size_chart>max){
	    			max=size_chart;
	    		}
	    		if(size_child>max){
	    			max=size_child;
	    		}
	    		size_=max;
	    	}
	    	console.log("size_:"+size_);
	    	parentsArr.length=size_;
	    	childrenArr.length=size_;
	    	chartsArr.length=size_;
	    	if(size_==0){
	    		var i=size_;
	    		var tr=$("<tr></tr>");
	    		if(parentsArr[i]==undefined||parentsArr[i]==""||parentsArr[i]==null){
	    			parentsArr[i]="无";
	    		}
	    		if(childrenArr[i]==undefined||childrenArr[i]==""||childrenArr[i]==null){
	    			childrenArr[i]="无";
	    		}
	    		if(chartsArr[i]==undefined||chartsArr[i]==""||chartsArr[i]==null){
	    			chartsArr[i]="无";
	    		}
	    		var parent_td=$('<td>'+parentsArr[i]+'</td>');
	    		var child_td=$('<td>'+childrenArr[i]+'</td>');
	    		var chart_td=$('<td>'+chartsArr[i]+'</td>');
	    		tr.append(chart_td);
	    		tr.append(child_td);
	    		tr.append(parent_td);
	    		table.append(tr);
	    	}
	    	//添加tr
	    	for(var i=0;i<size_;i++){
	    		var tr=$("<tr></tr>");
	    		if(parentsArr[i]==undefined||parentsArr[i]==""||parentsArr[i]==null){
	    			parentsArr[i]="无";
	    		}
	    		if(childrenArr[i]==undefined||childrenArr[i]==""||childrenArr[i]==null){
	    			childrenArr[i]="无";
	    		}
	    		if(chartsArr[i]==undefined||chartsArr[i]==""||chartsArr[i]==null){
	    			chartsArr[i]="无";
	    		}
	    		var parent_td=$('<td>'+parentsArr[i]+'</td>');
	    		var child_td=$('<td>'+childrenArr[i]+'</td>');
	    		var chart_td=$('<td>'+chartsArr[i]+'</td>');
	    		tr.append(chart_td);
	    		tr.append(child_td);
	    		tr.append(parent_td);
	    		table.append(tr);
	    	}
	    }
	    var showProgressView=function(result){
	    	console.log("enter showProgressView");
	    	var parentsArr=[];//父表数组
	    	var chartsArr=[];//图表数组
	    	var childrenArr=[];//子表数组
	    	var chartDiv=$('.orm-chart .charts');
	    	var childrenDiv=$('.orm-children .children');
	    	var parentsDiv=$('.orm-parents .parents');
	    	//把之前的视图移出
	    	chartDiv.children("div").empty();
	    	chartDiv.children("ul").remove();
	    	childrenDiv.children("div").empty();
	    	childrenDiv.children("ul").remove();
	    	parentsDiv.children("div").empty();
	    	parentsDiv.children("ul").remove();
	    	var fileName=$('.data-deal[flag="1"] .title span').text();//工作表名
	    	var ul=$('<ul id="org" class="fn-hide"></ul>');
	    	var li=$('<li></li>');
	    	li.text(fileName);
	    	//为数组赋值
	    	for(var name in result.result){
	    		if(name=="children"){//children key
	    			console.log("childrenArr:"+result.result[name]);
	    			childrenArr=result.result[name];
	    		}else if(name="parents"){//parent key
	    			console.log("parentsArr:"+result.result[name]);
	    			parentsArr=result.result[name];
	    		}else{//chart key
	    			console.log("chartsArr:"+result.result[name]);
	    			chartsArr=result.result[name];
	    		}
	    	}
	    	//添加过程视图  ul-li-ul-li-ul-li1 li2 li3
	    	if(childrenArr.length!=0){
	    		console.log("children not null");
	    		var ul_=$("<ul></ul>");
	    		var li_=$("<li></li>");
	    		li_.text("子表");
	    		var ul__=$("<ul></ul>");
	    		for(var i=0;i<childrenArr.length;i++){
	    			var li__=$("<li></li>");
	    			li__.text(childrenArr[i]);
	    			ul__.append(li__);
	    		}
	    		li_.append(ul__);
	    		ul_.append(li_);
	    		li.append(ul_);
	    		ul.append(li);
	    		childrenDiv.append(ul);
	    		$("#org").jOrgChart({
                	chartElement : '#child'
            	});
            	if(childrenDiv.next("p").hasClass('fn-hide')==false){
            		childrenDiv.next("p").addClass('fn-hide');
            	}
	    	}else{
	    		childrenDiv.next("p").removeClass("fn-hide");
	    	}
	    	if(parentsArr.length!=0){
	    		console.log("parent not null");
	    		var ul_=$("<ul></ul>");
	    		var li_=$("<li></li>");
	    		li_.text("父表");
	    		var ul__=$("<ul></ul>");
	    		for(var i=0;i<parentsArr.length;i++){
	    			var li__=$("<li></li>");
	    			li__.text(parentsArr[i]);
	    			ul__.append(li__);
	    		}
	    		li_.append(ul__);
	    		ul_.append(li_);
	    		li.append(ul_);
	    		ul.append(li);
	    		parentsDiv.append(ul);
	    		$("#org").jOrgChart({
               		chartElement : '#parent'
            	});
            	if(parentsDiv.next("p").hasClass('fn-hide')==false){
            		parentsDiv.next("p").addClass('fn-hide');
            	}
	    	}else{
	    		parentsDiv.next("p").removeClass('fn-hide');
	    	}
	    	if(chartsArr.length!=0){
	    		console.log("chart not null");
	    		var ul_=$("<ul></ul>");
	    		var li_=$("<li></li>");
	    		li_.text("图表");
	    		var ul__=$("<ul></ul>");
	    		for(var i=0;i<chartsArr.length;i++){
	    			var li__=$("<li></li>");
	    			li__.text(chartsArr[i]);
	    			ul__.append(li__);
	    		}
	    		li_.append(ul__);
	    		ul_.append(li_);
	    		li.append(ul_);
	    		ul.append(li);
	    		chartsDiv.append(ul);
	    		$("#org").jOrgChart({
                	chartElement : '#chart'
            	});
            	if(chartDiv.next("p").hasClass('fn-hide')==false){
            		chartDiv.next("p").addClass('fn-hide');
            	}
	    	}else{
	    		chartDiv.next("p").removeClass('fn-hide');
	    	}
	    }
	    //
	    //添加图表点击事件
	    var addIcon=function(){
	    	$(".add .add-icon").on("click",function(){
		    	//先隐藏原先活动的左右布局
		    	var flag=$(".left-active").attr("flag");
		    	$(".left-active").addClass('fn-hide');
		    	$(".right-active").addClass('fn-hide');
		    	$(".left-active").removeClass('left-active');
		    	$(".right-active").removeClass('right-active');
		    	//显示编辑创建图表的布局
		    	$(".edit-body").removeClass("fn-hide");
		    	$(".edit-body").addClass('edit-active');
		    	//取消添加图表事件
		    	$(".edit-body .cancel a").on("click",function(){
		    		$(".edit-body").removeClass('edit-active');
		    		$(".edit-body").addClass("fn-hide");
		    		$(".left-body[flag='2']").removeClass('fn-hide');
		    		$(".right-body[flag='2']").removeClass('fn-hide');
		    		$(".left-body[flag='2']").addClass('left-active');
		    		$(".right-body[flag='2']").addClass('right-active');
		    	});
		    	var data={};
		    	data["fileName"]=$(".data-view .file-selected").children("span").text();
		    	data["folderName"]=$(".data-view .file-selected").parents(".whfolder").children("div").children("span").text();
		    	console.log(data);
		    	$.ajax({
		    		url:"http://localhost:8080/api/file/getFileField",
		    		type:"POST",
		    		dataType:"json",
		    		data:data,
		    		cache:false,
		    		success:function(result){
		    			if(result.error!=""||result.status=="0"){
		    				console.log(result.error);
		    				return;
		    			}
		    			//填充edit-body维度、数值
		    			var select=$('.edit-body .choose select');
		    			//把之前的select去掉
		    			select.children("option").filter(function(index){
		    				console.log($(this).text());
		    				if($(this).text()=="选择维度"||$(this).text()=="选择数值"){
		    					console.log("return");
		    					return;
		    				}
	    					$(this).remove();
		    			});
		    			var fields=result.result["field"].split("/");
		    			for(var i=0;i<fields.length;i++){
		    				var option=$("<option></option>");
		    				option.text(fields[i]);
		    				select.append(option);
		    			}
		    			//为维度和数值select添加change事件
		    			$(".edit-body .choose select").on("change",function(){
		    				console.log("change choose select");
		    				if($(this).val()=="选择维度"||$(this).val()=="选择数值"){
		    					return;
		    				}
		    				if($(this).hasClass('field-text')){
		    					//维度
		    					var text=$(this).val();
		    					var val=$("input[name='fieldText']").val();
		    					if(val==""||val==null||val==undefined){
		    						$("input[name='fieldText']").val(text);
		    					}else{
		    						if(val.indexOf(text)!=-1){
		    							console.log("不能选择重复字段");
		    							return;
		    						}
		    						$("input[name='fieldText']").val(val+"/"+text);
		    					}
		    				}else{
		    					//数值
		    					var text=$(this).val();
		    					var val=$("input[name='fieldNum']").val();
		    					if(val==""||val==null||val==undefined){
		    						$("input[name='fieldNum']").val(text);
		    					}else{
		    						if(val.indexOf(text)!=-1){
		    							console.log("不能选择重复字段");
		    							return;
		    						}
		    						$("input[name='fieldNum']").val(val+"/"+text);
		    					}
		    				}
		    			});
		    		},
		    		error:function(){
		    			console.log("请求文件字段失败");
		    		}
		    	});
	    	});
	    }
	    addIcon();
	    //筛选方式点击事件
	    $(".condition-type label input").on("click",function(event){
	    	//把之前选中的按钮取消
	    	$(".condition-type label input").removeAttr('checked');
	    	//为选中的按钮设置checked属性
	    	$(this).attr("checked","checked");
	    	var flag=$(this).attr("flag");
	    	if(flag==1){
	    		if($('.for[flag=1]').hasClass('fn-hide')==false){
	    			return;
	    		}
	    		$('.for[flag=1]').removeClass('fn-hide');
	    		$('.for[flag=2]').addClass('fn-hide');
	    	}else{
	    		if($('.for[flag=2]').hasClass('fn-hide')==false){
	    			return;
	    		}
	    		$('.for[flag=2]').removeClass("fn-hide");
	    		$('.for[flag=1]').addClass("fn-hide");
	    	}
	    });
	    //字段与函数单击事件
	    $(".query-title li").on("click",function(){
	    	//添加css选中状态
	    	$(".title-active").removeClass("title-active");
	    	$(this).addClass('title-active');
	    	if ($(this).text().indexOf("字段")!=-1) {
	    		$(".query div").removeClass('fn-hide');
	    		$(".method-list").addClass('fn-hide');
	    	}else{
	    		$(".query div").removeClass('fn-hide');
	    		$(".field-list").addClass('fn-hide');
	    	}
	    })
	    //数据处理单击事件(创建分表、合表、追加数据等)
	    $(".method ul li").on("click",function(event){
	    	event.stopPropagation();
	    	var text=$(this).text();
	    	switch (text) {
	    		case "创建分表":
	    			createSplitTable($(this),event);
	    			break;
				case "创建合表":
					createJoinTable($(this),event);
					break;
				case "追加数据":
	    			appendData($(this),event);
	    			break;
	    		case "替换数据":
	    			replaceData($(this),event);
	    			break;
	    		case "添加字段":
	    			addField($(this),event);
	    			break;
	    		case "删除字段":
	    			delField($(this),event);
	    			break;
	    		default:
	    			break;
	    	}
	    })
	    //数据处理-创建分表
	    var  createSplitTable=function(obj,event){
	    	var tableName=$(".data-deal .title span").text();
	    	$(".split-table").remove();
	    	console.log(tableName);
	    	var div=$('<div class="split-table">'+
	    				'<div class="title">创建分表</span></div>'+
	    				'<div class="obj-table"><span>目标表:</span><input type="text" name="dtableName" ></div>'+
	    				'<div class="source-table"><span>源表:</span><input type="text" value="'+tableName+'" name="stableName"></div>'+
	    				'<div class="field"><span>字段:</span><input type="text" name="fields" value="" placeholder="请从右边选择字段" size=100>'+
	    					'<select id="fieldSelect"> '+
							  '<option>添加字段</option> '+
							'</select></div>'+
							'<div ><span>记录:</span><span>开始行</span><input type="text" size=10 id="startRow"><span>终止行</span><input type="text" size=10 id="endRow"></div>'+
							'<div class="table-btn"><a href="javascript:void(0);">确定</a><a href="javascript:void(0);">取消</a></div>'+
	    				'</div>');
	    	$("body").append(div);
	    	//填充字段列表
	    	$(" .dealwith[flag=1] table th").filter(function(index){ 
	    		console.log($(this).text())
	    		var option=$("<option></option>");
	    		option.text($(this).text());
	    		$("#fieldSelect").append(option);
	    	});
	    	div.css({"position":"absolute","left":event.pageX,"top":event.pageY});
	    	//阻止冒泡
	    	$(".split-table").on("click",function(event){
	    		event.stopPropagation();
	    	})
	    	//确定、取消按钮
	    	$(".split-table .table-btn a").on("click",function(event) {
	    		var objTableName=$('.obj-table input[name="dtableName"').val();
	    		var text=$(this).text();
	    		if(text.indexOf("确定")!=-1){//确定按钮
	    			//读取表单数据
	    			var data={};
	    			//目标表数据
	    			data["objTableName"]=objTableName;
	    			//源表数据
	    			data["sourceTableName"]=tableName;
	    			//仓库名
	    			data["folderName"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			//字段数据
	    			data["fields"]=$('.split-table .field input[name="fields"]').val();
	    			//记录数据：开始行-终止行
	    			data["startRow"]=$('#startRow').val();
	    			data["endRow"]=$('#endRow').val();
	    			//类型
	    			data["flag"]="split";
	    			console.log(data);
	    			//ajax上传数据到服务器
	    			$.ajax({
	    				url:"http://localhost:8080/api/file/create",
	    				type:"POST",
	    				dataType:"json",
	    				data:data,
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result==false){
	    						console.log(result.error);
	    						return;
	    					}
	    					//在与源表所在的文件夹中显示生成的表
	    					var li=$('<li>'+
		                                  '<i class="file-icon"></i>'+
		                                  '<span data-value="'+objTableName+'">'+objTableName+'</span>'+
		                                  '<i class="more-icon">'+
                                 	'</li>');
	    					$('.wh-folderlist li ul li span[data-value="'+tableName+'"]').parents('.files').append(li);
	    					//绑定事件
	    					waitRun();
	    				},
	    				error:function(result){
	    					console.log("创建分表失败!");
	    				},
	    				complete:function(){
	    					$(".split-table").remove();
	    				}

	    			});
	    		}else{//取消按钮
	    			$(".split-table").remove();
	    		}
	    	});
	    	//添加字段change事件
	    	$("#fieldSelect").on("change",function(event){
	    		var text=$(this).val();
	    		var val=$(".split-table .field input[name='fields']").attr("value");
	    		if(text.indexOf("添加字段")!=-1){
	    			return;
	    		}
	    		if(val==""){
	    			val=text;
	    			$(".split-table .field input[name='fields']").attr("value",val);
	    		}else{
	    			if(val.indexOf(text)!=-1){
	    				//错误提示
	    				var  errText="不能选择重复字段";
	    				$(".error-tip").text(errText).removeClass("fn-hide");
	    				//设置定时显示错误
	    				var timer=setInterval(function(){
	    					if($(".error-tip").hasClass('fn-hide')){
		    					clearInterval(timer);
	    					}
	    					$(".error-tip").addClass("fn-hide");
	    				}, 3000);
	    				return;
	    			}
	    			val=val+"/"+text;
	    			$(".split-table .field input[name='fields']").attr("value",val);
	    		}
	    	});
	    }
	    //数据处理-创建合表
	    var  createJoinTable=function(obj,event){
	    	var tableName=$(".data-deal .title span").text();
	    	console.log("enter");
	    	$(".join-table").remove();
	    	var div=$('<div class="join-table">'+
	    				'<div class="title">创建合表</span></div>'+
	    				'<div class="obj-table"><span>目标表:</span><input type="text" name="dtableName"></div>'+
	    				'<div class="source-table"><span>源表:</span><input type="text" id="tables" name="tables" value="" placeholder="请从右边选择源表" size=100>'+
	    					'<select id="tableSelect"> '+
							  '<option>添加源表</option> '+
							'</select></div>'+
							'<div class="table-btn"><a href="javascript:void(0);">确定</a><a href="javascript:void(0);">取消</a></div>'+
	    				'</div>');
	    	$("body").append(div);
	    	//填充源表列表
	    	$('.data-deal .wh-folderlist').children().filter(function(index){
	    		$(this).children("ul").children("li").filter(function(liIndex){
	    			var option=$("<option></option>");
	    			option.text($(this).children("span").text());
	    			$("#tableSelect").append(option);
	    		});
	    	});
	    	div.css({"position":"absolute","left":event.pageX,"top":event.pageY});
	    	//阻止冒泡
	    	$(".join-table").on("click",function(event){
	    		event.stopPropagation();
	    	})
	    	//确定、取消按钮
	    	$(".join-table .table-btn a").on("click",function(event) {
	    		var objTableName=$('.obj-table input[name="dtableName"').val();
	    		var text=$(this).text();
	    		if(text.indexOf("确定")!=-1){
	    			//读取表单数据
	    			var data={};
	    			//仓库名
	    			data["folderName"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			//目标表数据
	    			data["objTableName"]=objTableName;
	    			//源表数据
	    			data["sourceTableName"]=$("#tables").val();
	    			//操作类型
	    			data["flag"]="join";
	    			var sourceTableName=data["sourceTableName"];
	    			if(sourceTableName.indexOf("/")!=-1){
	    				var arr=data["sourceTableName"].split("/");
	    				var folderNameStr;
	    				for(var i=0;i<arr.length;i++){
                             var folderName=$('.whfolder ul li span[data-value="'+arr[i]+'"]').parents(".whfolder").children('div').children('span').text();
                            if(i==0){
                            	folderNameStr=folderName
                            }else{
                            	folderNameStr=folderNameStr+"/"+folderName;
                            }
                        }
                        data["folderNameList"]=folderNameStr;
	    			}else{
		    			//仓库名
		    			data["folderNameList"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			}
	    			console.log(data);
	    			//ajax上传数据到服务器
	    			$.ajax({
	    				url:"http://localhost:8080/api/file/create",
	    				type:"POST",
	    				dataType:"json",
	    				data:data,
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result==false){
	    						console.log(result.error);
	    						return;
	    					}
	    					//在与源表所在的文件夹中显示生成的表
	    					var li=$('<li>'+
		                                  '<i class="file-icon"></i>'+
		                                  '<span data-value="'+objTableName+'">'+objTableName+'</span>'+
		                                  '<i class="more-icon">'+
                                 	'</li>');
	    					$('.wh-folderlist li ul li span[data-value="'+tableName+'"]').parents('.files').append(li);
	    					//绑定事件
	    					waitRun();
	    				},
	    				error:function(result){
	    					console.log("创建合表失败!");
	    				},
	    				complete:function(){
	    					$(".join-table").remove();
	    				}
	    			});
	    		}else{
	    			$(".join-table").remove();
	    		}
	    	});
	    	//选择源表change事件
	    	$("#tableSelect").on("change",function(event){
	    		//选择框的值
	    		var text=$(this).val();
	    		//源表输入框的值
	    		var val=$(".join-table .source-table input[name='tables']").attr("value");
	    		if(text.indexOf("添加源表")!=-1){
	    			return;
	    		}
	    		if(val==""){
	    			val=text;
	    			$(".join-table .source-table input[name='tables']").attr("value",val);
	    		}else{
	    			if(val.indexOf(text)!=-1){
	    				//错误提示
	    				var  errText="不能选择重复源表";
	    				$(".error-tip").text(errText).removeClass("fn-hide");
	    				//设置定时显示错误
	    				var timer=setInterval(function(){
	    					if($(".error-tip").hasClass('fn-hide')){
		    					clearInterval(timer);
	    					}
	    					$(".error-tip").addClass("fn-hide");
	    				}, 3000);
	    				return;
	    			}
	    			val=val+"/"+text;
	    			$(".join-table .source-table input[name='tables']").attr("value",val);
	    		}
	    	});

	    }
	    //数据处理-追加数据
	    var  appendData=function(obj,event){ 
	    	var tableName=$(".data-deal .title span").text();
	    	console.log("enter");
	    	$(".append-data").remove();
	    	var div=$('<div class="append-data">'+
	    				'<div class="title">追加数据</span></div>'+
	    				'<div class="source-table"><span>源表:</span><input type="text" name="table" value="'+tableName+'">'+
							'</div>'+
							'<div class="field-data"><span>字段：</span><ul class="fn-clear">'+
								'</ul></div>'+
							'<div class="table-btn"><a href="javascript:void(0);">确定</a><a href="javascript:void(0);">取消</a></div>'+
	    				'</div>');
	    	$("body").append(div);
	    	//填充字段列表
	    	$(" .dealwith[flag=1] table th").filter(function(index){ 
	    		console.log($(this).text())
	    		var li=$('<li><span>'+$(this).text()+'</span><input type="text" value=""></li>');
	    		$(".append-data .field-data ul").append(li);
	    	});
	    	div.css({"position":"absolute","left":event.pageX-100,"top":event.pageY});
	    	//阻止冒泡
	    	$(".append-data").on("click",function(event){
	    		event.stopPropagation();
	    	})
	    	//确定、取消按钮
	    	$(".append-data .table-btn a").on("click",function(event) {
	    		var text=$(this).text();
	    		if(text.indexOf("确定")!=-1){
	    			//读取表单数据
	    			var data={};
	    			//文件夹
	    			data["folderName"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			//源表数据
	    			data["fileName"]=tableName;
	    			var fieldValueList;
	    			//字段值,通过遍历获取对应的字段值。
	    			$(".append-data .field-data ul li").filter(function(index){
	    				var val;
	    				if(index==0){
                           val=$(this).children("input").val();
                           fieldValueList=val;
	    				}else{
	    					val=$(this).children("input").val();
	    				    fieldValueList=fieldValueList+"/"+val;
	    				}
	    			});
	    			data["fieldValueList"]=fieldValueList;
	    			//操作类型
	    			data["type"]="1";
	    			console.log(data);
	    			//ajax上传数据到服务器
	    			$.ajax({
	    				url:"http://localhost:8080/api/file/update",
	    				type:"POST",
	    				dataType:"json",
	    				data:data,
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result==false){
	    						console.log(result.error);
	    						return;
	    					}
	    					//重新请求表数据
	    					$('.whfolder ul li span[data-value="'+tableName+'"]').trigger('click');
	    				},
	    				error:function(result){
	    					console.log("追加数据失败!");
	    				},
	    				complete:function(){
	    					$(".append-data").remove();
	    				}
	    			});
	    			
	    		}else{//取消按钮
	    			$(".append-data").remove();
	    		}
	    	});
	    	//选择源表change事件
	    	$("#tableSelect").on("change",function(event){
	    		//选择框的值
	    		var text=$(this).val();
	    		//源表输入框的值
	    		console.log("enter tableSelect");
	    		var val=$(".append-table .source-table input[name='table']").val();
	    		console.log(text);
	    		if(text.indexOf("选择源表")!=-1){
	    			$(".append-data .source-table input[name='table']").val("");
	    			return;
	    		}
    			val=text;
    			$(".append-data .source-table input[name='table']").val(val);
	    	});


	    }
	    //数据处理-替换数据
	    var replaceData=function(obj,event){
	    	var tableName=$(".data-deal .title span").text();
	    	console.log("enter");
	    	$(".replace-data").remove();
	    	var div=$('<div class="replace-data">'+
	    				'<div class="title">替换数据</span></div>'+
	    				'<div class="source-table"><span>源表:</span><input type="text" name="table" value="'+tableName+'" >'+
	    					'</div>'+
							'<div class="field-select"><span>字段:</span><input type="text" name="fields" value="" placeholder="请从右边选择字段">'+
							        '<select id="fieldSelect">'+
										'<option>选择字段</option>'+
									'</select><span></span><input type="text" placeholder="请输入替换内容" id="replaceText"></div>'+
									'<div><span>位置:</span><input type="text" id="matchText" placeholder="请输入匹配内容"><span>开始行</span><input type="text" size=10 id="startRow"><span>终止行</span><input type="text" size=10 id="endRow"></div>'+
							'<div class="table-btn"><a href="javascript:void(0);">确定</a><a href="javascript:void(0);">取消</a></div>'+
	    				'</div>');
	    	$("body").append(div);
	    	//填充字段列表
	    	$(" .dealwith[flag=1] table th").filter(function(index){ 
	    		console.log($(this).text())
	    		var option=$("<option></option>");
	    		option.text($(this).text());
	    		$("#fieldSelect").append(option);
	    	});
	    	div.css({"position":"absolute","left":event.pageX-100,"top":event.pageY});
	    	//阻止冒泡
	    	$(".replace-data").on("click",function(event){
	    		event.stopPropagation();
	    	})
	    	//确定、取消按钮
	    	$(".replace-data .table-btn a").on("click",function(event) {
	    		var text=$(this).text();
	    		if(text.indexOf("确定")!=-1){
	    			//读取表单数据
	    			var data={};
	    			//文件夹
	    			data["folderName"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			//源表数据
	    			data["fileName"]=tableName;
	    			//字段值
	    			data["fields"]=$('.field-select input[name="fields"]').val();
	    			//替换内容
	    			data["replaceText"]=$('#replaceText').val();
	    			//匹配内容
	    			data["matchText"]=$('#matchText').val();
	    			//位置
	    			data["startRow"]=$('#startRow').val();
	    			data["endRow"]=$('#endRow').val();
	    			//操作类型
	    			data["type"]="2";
	    			console.log(data);
	    			//ajax上传数据到服务器
	    			$.ajax({
	    				url:"http://localhost:8080/api/file/update",
	    				type:"POST",
	    				dataType:"json",
	    				data:data,
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result==false){
	    						console.log(result.error);
	    						return;
	    					}
	    					//重新请求表数据
	    					$('.whfolder ul li span[data-value="'+tableName+'"]').trigger('click');
	    				},
	    				error:function(result){
	    					console.log("追加数据失败!");
	    				},
	    				complete:function(){
	    					$(".replace-data").remove();
	    				}
	    			});
	    		}else{//取消按钮
	    			$(".replace-data").remove();
	    		}
	    	});
	    	//选择替换的字段change事件
	    	$("#fieldSelect").on("change",function(event){
	    		console.log("enter fieldSelect");
	    		//选择框的值
	    		var text=$(this).val();
	    		//源表字段输入框的值
	    		if(text.indexOf("选择字段")!=-1){
	    			return;
	    		}
	    		$(".replace-data .field-select input[name='fields']").attr("value",text);
	    	});


	    }
	    //数据处理-添加字段
	    var  addField=function(obj,event){
	    	var tableName=$(".data-deal .title span").text();
	    	console.log("enter");
	    	$(".add-field").remove();
	    	var div=$('<div class="add-field">'+
	    				'<div class="title">添加字段</span></div>'+
	    				'<div class="source-table"><span>源表:</span><input type="text" name="table" value="'+tableName+'">'+
	    					'</div>'+
							'<div class="field-select"><span>字段:</span><input type="text" id="fields" name="fields" value="" size=80 placeholder="请从右边边输入字段">'+
									'<input type="text" id="field" name="field" value="" placeholder="请输入添加的字段名" ><a href="javascript:void(0)">添加</a></div>'+
							'<div class="table-btn"><a href="javascript:void(0);">确定</a><a href="javascript:void(0);">取消</a></div>'+
	    				'</div>');
	    	$("body").append(div);
	    	//填充源表列表
	    	$('.data-deal .wh-folderlist').children().filter(function(index){
	    		$(this).children("ul").children("li").filter(function(liIndex){
	    			var option=$("<option></option>");
	    			option.text($(this).children("span").text());
	    			$("#tableSelect").append(option);
	    		});
	    	});
	    	div.css({"position":"absolute","left":event.pageX-300,"top":event.pageY});
	    	//阻止冒泡
	    	$(".add-field").on("click",function(event){
	    		event.stopPropagation();
	    	})
	    	//确定、取消按钮
	    	$(".add-field .table-btn a").on("click",function(event) {
	    		var text=$(this).text();
	    		if(text.indexOf("确定")!=-1){
	    			//读取表单数据
	    			var data={};
	    			//文件夹
	    			data["folderName"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			//源表数据
	    			data["fileName"]=tableName;
	    			//字段值
	    			data["fields"]=$('.add-field .field-select input[name="fields"]').val();
	    			//操作类型
	    			data["type"]="3";
	    			console.log(data);
	    			//ajax上传数据到服务器
	    			$.ajax({
	    				url:"http://localhost:8080/api/file/update",
	    				type:"POST",
	    				dataType:"json",
	    				data:data,
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result==false){
	    						console.log(result.error);
	    						return;
	    					}
	    					//重新请求表数据
	    					$('.whfolder ul li span[data-value="'+tableName+'"]').trigger('click');
	    				},
	    				error:function(result){
	    					console.log("追加数据失败!");
	    				},
	    				complete:function(){
	    					$(".add-field").remove();
	    				}
	    			});
	    		}else{//取消
	    			$(".add-field").remove();
	    		}
	    	});
	    	//添加字段事件
	    	$('.field-select a').on("click",function(){
	    		console.log("enter field-selsect");
	    		var fieldValue=$("#field").val();
	    		var fieldValues=$("#fields").val();
	    		if(fieldValue==""){
	    			return;
	    		}
	    		if(fieldValues.indexOf(fieldValue)!=-1){
	    			return;
	    		}
	    		if(fieldValues==""){
	    			$("#fields").attr("value",fieldValue);
	    		}else{
	    			fieldValues=fieldValues+"/"+fieldValue;
	    			$("#fields").attr("value",fieldValues);
	    		}
	    	});
	    }
	    //数据处理-删除字段
	    var  delField=function(obj,event){
	    	var tableName=$(".data-deal .title span").text();
	    	console.log("enter");
	    	$(".del-field").remove();
	    	var div=$('<div class="del-field">'+
	    				'<div class="title">删除字段</div>'+
	    				'<div class="source-table"><span>源表:</span><input type="text" name="tables" value="'+tableName+'">'+
	    					'</div>'+
							'<div class="field-select"><span>字段:</span><input type="text" name="fields" id="fields" value="" placeholder="请从右边选择字段">'+
								'<select id="fieldSelect">'+
									'<option>选择字段</option>'+
									'</select></div>'+
							'<div class="table-btn"><a href="javascript:void(0);">确定</a><a href="javascript:void(0);">取消</a></div>'+
	    				'</div>');
	    	$("body").append(div);
	    	//填充字段列表
	    	$(" .dealwith[flag=1] table th").filter(function(index){ 
	    		console.log($(this).text())
	    		var option=$("<option></option>");
	    		option.text($(this).text());
	    		$("#fieldSelect").append(option);
	    	});
	    	div.css({"position":"absolute","left":event.pageX-100,"top":event.pageY});
	    	//阻止冒泡
	    	$(".del-field").on("click",function(event){
	    		event.stopPropagation();
	    	})
	    	//确定、取消按钮
	    	$(".del-field .table-btn a").on("click",function(event) {
	    		var text=$(this).text();
	    		if(text.indexOf("确定")!=-1){
	    			//读取表单数据
	    			var data={};
	    			//文件夹
	    			data["folderName"]=$('.whfolder ul li span[data-value="'+tableName+'"]').parents(".whfolder").children('div').children('span').text();
	    			//源表数据
	    			data["fileName"]=tableName;
	    			//字段值
	    			data["fields"]=$('#fields').val();
	    			//操作类型
	    			data["type"]="4";
	    			console.log(data);
	    			//ajax上传数据到服务器
	    			$.ajax({
	    				url:"http://localhost:8080/api/file/update",
	    				type:"POST",
	    				dataType:"json",
	    				data:data,
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result==false){
	    						console.log(result.error);
	    						return;
	    					}
	    					//重新请求表数据
	    					$('.whfolder ul li span[data-value="'+tableName+'"]').trigger('click');
	    				},
	    				error:function(result){
	    					console.log("删除字段失败!");
	    				},
	    				complete:function(){
	    					$(".del-field").remove();
	    				}
	    			});
	    		}else{//取消
	    			$(".del-field").remove();
	    		}
	    	});
	    	//选择删除的字段change事件
	    	$("#fieldSelect").on("change",function(event){
	    		//选择框的值
	    		var text=$(this).val();
	    		//源表字段输入框的值
	    		var val=$(".del-field .field-select input[name='fields']").attr("value");
	    		if(text.indexOf("删除字段")!=-1){
	    			return;
	    		}
	    		if(val==""){
	    			val=text;
	    			$(".del-field .field-select input[name='fields']").attr("value",val);
	    		}else{
	    			if(val.indexOf(text)!=-1){
	    				//错误提示
	    				var  errText="不能选择重复字段";
	    				errorTip(errText);
	    				return;
	    			}
	    			val=val+"/"+text;
	    			$(".del-field .field-select  input[name='fields']").attr("value",val);
	    		}
	    	});
	    }
	//数据仓库事件，也是left-body中的事件
		//文件夹点击事件（等文件夹生成后才执行）
	var waitRun=function(){
		//去除之前的绑定
		$(".wh-folderlist .folder").off("click");
	    $(".wh-folderlist .folder").on('click',  function(event) {
	    	console.log("folder enter");
	    	if($(this).hasClass('folder-selected')==true){
	    		$(this).next(".files").addClass("fn-hide");
	    		$(this).removeClass("folder-selected");
	    	}else{
	    		$(this).addClass("folder-selected");
	    		$(this).next(".files").removeClass("fn-hide");
	    	}
	    });
	    //文件点击事件
	    $(".files li").off();
	    $(".files li").on('click', function(event) {
	    	//文件被中时的状态
	    	console.log("file enter");
    		if($(".files li").hasClass('file-selected')==true){
    			$(".files li").removeClass('file-selected');
    		}
    		$(this).addClass("file-selected");
	    	var text=$(this).children("span").text();
	    	$(".right-body[flag=1] .title span").text(text);
	    	console.log(text);
	    	//文件名
	    	var data={};
	    	data["fileName"]=text;
	    	data["folderName"]=$(this).parents(".whfolder").children("div").children("span").text();
	    	console.log(data);
	    	//data-view
	    	if($(this).parents(".data-view").length!=0){
	    		url="http://localhost:8080/api/chart/list";
	    	}else{//data-deal
	    		url="http://localhost:8080/api/file/read";
	    	}
	    	//right-body显示内容
	    	$.ajax({
	    		url:url,
	    		type:"POST",
	    		dataType:"json",
	    		data:data,
	    		cache:false,
	    		success:function(result){
	    			console.log(result);
	    			if(result.error!=""||result.status=="0"){
	    				console.log(result.error);
	    				return;
	    			}
	    			if($(this).parents(".data-view").length!=0){
	    				//data-view
	    				showFileChart(reulst);
			    	}else{//data-deal
		    			//显示文件内容
		    			showFile(result);
		    			//模拟单击数据处理
		    			$(".oper-li[flag='1']").trigger('click');
			    	}
	    		},
	    		error:function(result){
	    			console.log("请求服务器文件内容失败！")
	    		}
	    	})
	    });
	    //显示文件图表
	    var showFileChart=function(result){
	    	//移出之前的图表信息
	    	var ul=$(".pic-list ul");
	    	ul.empty();
	    	//第一个默认的图表
	    	var first_li=$("<li><div class='add'><i class='add-icon' title='添加图表'></i></div></li>");
	    	ul.append(first_li);
	    	//重新给add-icon图表绑定事件
	    	addIcon();
	    	//显示文件图表信息
	    	console.log("图表信息:"+result);
	    }
	    //显示文件内容
	    var showFile=function(result,text){
	    	//先删除之前的
	    	$('.right-body .dealwith .table table').empty();
	    	$(".right-body[flag=1] .title span").text(text);
	    	var table=$(".right-body .dealwith .table table");
	    	var rows = 30;
	    	console.log(result);
            // table
            console.log(result.result);
            for (var i = 0; i < rows; i++) {
                var arr = result.result.data[i];
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
	    }
	    //文件和文件夹图标more-icon单击事件
	    $(".more-icon").off("click");
	    $(".more-icon").on("click",function(event) {
	    	//阻止冒泡
	    	console.log("click more-icon");
	    	event.stopPropagation();
	    	if($(this).hasClass("div-more")==true){
	    		return;
	    	}
	    	//恢复之前状态
	    	$(".more-icon").css("opacity","1");
	    	$(".div-more").remove();
	    	//状态改变
	    	$(this).css("opacity",".5");
	    	var div;
	    	//添加文件或者文件夹操作列表
	    	if($(this).siblings().hasClass('file-icon')){
	    		//把文件名或者文件夹名加上
	    		var name=$(this).siblings("span").text();
	    		div=$('<div name="'+name+'" class="div-more"><ul><li>文件重命名</li><li>文件移动到<li>文件删除</li><li>文件下载</li></ul></div>');
	    	}else{
	    		var name=$(this).siblings("span").text();
	    		div=$('<div name="'+name+'" class="div-more"><ul><li>文件夹重命名</li><li>文件夹删除</li></ul></div>');
	    	}
	    	$("body").append(div);
	    	div.css({"left":event.pageX,"top":event.pageY});
	    });
	    //当点击document的时候，除了阻止冒泡的div，都会把之前创建的div 清除
	    $(document).off("click");
	    $(document).on("click",function(){
	    	$(".del-field").remove();
	    	$(".add-field").remove();
	    	$(".replace-data").remove();
	    	$(".append-data").remove();
	    	$(".join-table").remove();
	    	$(".split-table").remove();
	    	$(".div-moveto").remove();
	    	$(".div-rename").remove();
	    	$(".div-add").remove();
	    	$(".div-more").remove();
	    	$(".more-icon").css("opacity","1");
	    });
	    //添加文件夹
	    $(".wh .add-icon").off("click");
	    $(".wh .add-icon").on("click",function(event) {
	    	event.stopPropagation();
	    	/* Act on the event */
	    	console.log("enter folder");
	    	if($(this).hasClass("div-add")==true){
	    		return;
	    	}
	    	//添加增加文件夹操作div
	    	var div=$('<div class="div-add"><div class="add-title"><h2>新建文件夹</h2></div><div class="add-input"><span>文件夹名称</span><input type="text" name="folderName" id="addFolder"></div><div class="add-btn"><input type="button" value="确定"><input type="button" value="取消"></div></div>');
	    	$("body").append(div);
	    	$(".add-btn").css({"text-align":"right"});
	    	$(".div-add div").css({"padding":"5px 10px","margin":"0px 10px"});
	    	$(".add-btn input").css({"outline":"none","border":"0","padding":"0 10","margin":"5px","color":"rgba(81,130,228,1)","fontWeight":"700","text-align":"center"});
	    	$(".add-input input").css({"outline":"none","border":"0","box-shadow":"0 -1px 0 0 rgba(81,130,228,.6) inset"})
	    	div.css({"background":"#fff","position":"absolute","left":event.pageX,"top":event.pageY});
	    	//当点击wh-set不冒泡
	    	$(".div-add").on("click",function(event){
	    		event.stopPropagation();
	    		return;
	    	})
	    	$(".add-btn input").on("click",function(){
	    		//点击取消按钮
	    		if($(this).attr("value").indexOf("取消")!=-1){
	    			$(".div-add").remove();
	    		}else{//点击确定按钮
	    			var value=$("#addFolder").val();
	    			var data={};
	    			data["folderName"]=value;
	    			$.ajax({
	    				url:"http://localhost:8080/api/folder/create",
	    				data:data,
	    				type:"POST",
	    				dataType:"json",
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result!=true){
	    						return;
	    					}
	    					//创建成功时，把创建成功的文件夹名显示到仓库列表中
	    					var li=$('<li class="whfolder">'+
    	    						'<div class="folder">'+
                           				'<i class="folder-icon"></i>'+
                                    	'<span data-value="'+value+'">'+value+'</span>'+
                                    	'<i class="more-icon" style="opacity: 1;"></i>'+
                                    '</div>'+
                                    '<ul class="files files-hover"></ul>'+
                                '</li>');
	    					$(".wh-folderlist").append(li);
	    					//为文件夹绑定事件
	    					waitRun();
	    				},
	    				error:function(result){
	    					console.log("添加文件夹失败！");
	    				},
	    				complete:function(){
	    					$(".div-add").remove();
	    				}
	    			});
	    		}
	    	});
	    });
	   
	    //生成文件夹或者文件重命名操作div或移动文件夹或者文件div
	    $(".div-more ul li").off("click");
	    $(document).on("click",".div-more ul li",function(event){
	    	//阻止div-more ul li往上冒泡
	    	event.stopPropagation();
	    	console.log("div-more");
	    	//把之前显示的div-rename  div-moveto删除
	    	$(".div-rename").remove();
	    	$(".div-moveto").remove();
	    	console.log($(".div-more").attr("name"));
	    	//根据text判断是重命名操作还是移动操作、删除
	    	if($(this).text().indexOf("重命名")!=-1){
	    		//重命名
	    		rename($(this),event);
	    		return false;
	    	}else if($(this).text().indexOf("移动到")!=-1){
	    		//移动
	    		moveto($(this),event);
	    	}else if($(this).text().indexOf("下载")!=-1){
	    		//下载
	    		fileDownLoad($(this),event);
	    	}else{
	    		//删除
	    		del($(this),event);
	    	}
	    })
	    //下载文件
	    var fileDownLoad=function(obj,event){
	    	var fileName=$(".div-more").attr("name");
	    	var folderName=$('.wh-folderlist .whfolder ul li span[data-value="'+fileName+'"]').parents(".whfolder").children('div').children('span').text();
	    	console.log("enter fileDownLoad",folderName,fileName);
	    	var form=$('<form action="http://localhost:8080/api/excel/download" method="get"></form>');
	    	var fileInput=$('<input type="text" name="fileName" value="'+fileName+'">');
	    	var folderInput=$('<input type="text", name="folderName" value="'+folderName+'">');
	    	$("body").append(form);
	    	form.append(fileInput);
	    	form.append(folderInput);
	    	form.submit();
	    	form.remove();
	    	//div-more不能删除，因为由jq生成的表单用于下载时，需要进行长连接，如果删除就获取不到div-more数据
	    }
	    //重命名
	    var  rename=function(obj,event){
	    	if(obj.text().indexOf("文件夹")==-1){
	    		var div=$('<div class="div-rename"><div class="rename-title"><h2>文件重命名</h2></div><div class="rename-input"><span>文件新名称</span><input type="text" name="rename" id="rename"></div><div class="rename-btn"><input type="button" value="确定"><input type="button" value="取消"></div></div>');
	    	}else{
	    		var div=$('<div class="div-rename"><div class="rename-title"><h2>文件夹重命名</h2></div><div class="rename-input"><span>文件夹新名称</span><input type="text" name="rename" id="rename"></div><div class="rename-btn"><input type="button" value="确定"><input type="button" value="取消"></div></div>');
	    	}
	    	$("body").append(div);
	    	div.css({"position":"absolute","left":event.pageX,"top":event.pageY});
	    	//阻止冒泡
	    	$(".div-rename").on('click',  function(event) {
	    		event.stopPropagation();
	    		/* Act on the event */
	    	});
	    	//确定\取消按钮
	    	$(".rename-btn input").on("click",function(event){
	    		event.stopPropagation();
	    		//点击取消按钮
	    		if($(this).attr("value").indexOf("取消")!=-1){
	    			$(".div-rename").remove();
	    		}else{//点击确定按钮
	    			var value=$("#rename").val();
	    			var original_name=$(".div-more").attr("name");
	    			var data={};
	    			var url;
	    			//区分是文件夹重命名还是文件重命名,就修改url地址
	    			if(obj.text().indexOf("文件夹")!=-1){
	    				console.log("文件夹");
	    				//原来的文件名
	    				data["original_name"]=original_name;
	    				data["folderName"]=value;
	    				url="http://localhost:8080/api/folder/update";
	    			}else{
	    				console.log("文件");
	    				data["original_name"]=original_name;
	    				data["objFileName"]=value;
	    				url="http://localhost:8080/api/excel/update";
	    				data["folderName"]=$('.left-body[flag=1] .whfolder ul li span[data-value="'+data["original_name"]+'"]').parents(".whfolder").children("div").children("span").text();
	    				console.log("original_name:"+original_name+"value:"+value);
	    			}
	    			$.ajax({
	    				url:url,
	    				data:data,
	    				type:"POST",
	    				dataType:"json",
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result!=true){
	    						console.log(result.error);
	    						return;
	    					}
	    					//重命名成功后，修改原先的文件夹或者文件名，为了能与修改的元素对应上，在对应元素在
	    					//生成时添加data-value属性。
	    					if(obj.text().indexOf("文件夹")!=-1){
	    						//修改原先的文件夹名
	    						console.log("value and folderName",value,original_name);
	    						$('.wh-folderlist .whfolder div span[data-value="'+original_name+'"]').text(value);
	    						$('.wh-folderlist .whfolder div span[data-value="'+original_name+'"]').attr("data-value",value);
	    						console.log("data-value:"+$('.wh-folderlist .whfolder div span[data-value="'+value+'"]').attr("data-value"));

	    					}else{
	    						//修改原先的文件名
	    						$('.wh-folderlist .whfolder ul li span[data-value="'+original_name+'"]').text(value);
	    						$('.wh-folderlist .whfolder ul li span[data-value="'+original_name+'"]').attr("data-value",value);
	    						console.log("data-value:"+$('.wh-folderlist .whfolder ul li span[data-value="'+value+'"]').attr("data-value"));
	    					}
	    				},
	    				error:function(result){
	    					console.log("重命名失败！");
	    				},
	    				complete:function(){
	    					$(".div-rename").remove();
	    					$(".div-more").remove();
	    				}
	    			});
	    		}
	    		return false;//IE
	    	});
	    }
	    //删除文件夹或者文件
	    var del=function(obj,event){
	    	var  data={};
	    	var folderName;
	    	var fileName;
	    	if(obj.text().indexOf("文件夹")==-1){
	    		//删除文件
	    		console.log("enter file")
	    		fileName=$(".div-more").attr("name");
	    		data["fileName"]=fileName;
	    		folderName=$('.wh-folderlist .whfolder ul li span[data-value="'+data["fileName"]+'"]').parents(".whfolder").children('div').children('span').text();
	    		data["folderName"]=folderName;
	    		data["type"]="0";
				url="http://localhost:8080/api/file/changeFileStatus";
	    	}else{
	    		//删除文件夹
	    		console.log("enter folder");
	    		folderName=$(".div-more").attr("name");
	    		console.log(folderName);
	    		data["folderName"]=folderName;
	    		data["type"]="0";
				url="http://localhost:8080/api/folder/changeFolderStatus";
	    	}
	    	$.ajax({
	    		url:url,
				data:data,
				type:"POST",
				dataType:"json",
				cache:false,
				success:function(result){
					if(result.error!=""||result.result==false){
						console.log(result.error);
						return;
					}
					//删除文件夹
					if(obj.text().indexOf("文件夹")!=-1){
						//从视图中移出该文件夹
						$('.wh-folderlist li div span[data-value="'+folderName+'"]').parents(".whfolder").remove();
					}else{
						//删除文件
						$('.whfolder ul li span[data-value="'+fileName+'"]').parents("li").first().remove();
					}
				},
				error:function(result){
					console.log("删除失败！");
				},
				complete:function(){
					$(".div-more").remove();
				}
	    	})
	    }
	    //移动文件
	    var  moveto=function(obj,event){
	    	var div=$('<div class="div-moveto">'+
	    		'<div class="moveto-title"><h2>移动文件</h2></div>'+
	    		'<div class="moveto-input"><span>移动到</span><input type="text" name="folderName" id="folderName">'+
	    		'<select id="folderSelect">'+
	    			'<option>请选择文件夹</option>'+
	    		'</select>'+
	    		'</div>'+
	    		'<div class="moveto-btn"><input type="button" value="确定"><input type="button" value="取消"></div></div>');
	    	$("body").append(div);
	    	//生成文件夹列表
	    	var text=$(".div-more").attr("name");
	    	console.log("text:"+text);
	    	$('.whfolder ul li span[data-value="'+text+'"]').parents(".wh-folderlist").children().filter(function(index){
	    		var option=$('<option></option>');
	    		var folderName=$('.whfolder ul li span[data-value="'+text+'"]').parents(".whfolder").children("div").children("span").text();
	    		console.log(index+folderName);
	    		if(folderName.indexOf($(this).children("div").children("span").text())==-1){
		    		option.text($(this).children("div").children("span").text());
		    		$("#folderSelect").append(option);
		    	}
	    	});
	    	div.css({"position":"absolute","left":event.pageX,"top":event.pageY});
	    	//阻止冒泡
	    	$(".div-moveto").on('click',  function(event) {
	    		event.stopPropagation();
	    		/* Act on the event */
	    	});
	    	//确定\取消按钮
	    	$(".moveto-btn input").on("click",function(event){
	    		event.stopPropagation();
	    		//点击取消按钮
	    		if($(this).attr("value").indexOf("取消")!=-1){
	    			$(".div-moveto").remove();
	    		}else{//点击确定按钮
	    			var value=$("#folderName").val();
	    			var data={};
	    			var fileName=$(".div-more").attr("name");
	    			var url="http://localhost:8080/api/excel/move";
	    			//移动的文件名
	    			data["fileName"]=fileName;
	    			//要移动的文件名所在的文件夹名
	    			data["sourceFolderName"]=$('.wh-folderlist .whfolder ul li span[data-value="'+data["fileName"]+'"]').parents(".whfolder").children("div").children("span").text();
	    			//移动到的文件夹名
	    			data["folderName"]=value;
	    			$.ajax({
	    				url:url,
	    				data:data,
	    				type:"POST",
	    				dataType:"json",
	    				cache:false,
	    				success:function(result){
	    					if(result.error!=""||result.result!=true){
	    						console.log(result.error);
	    						return;
	    					}
	    					//移动文件时，先把在原来的文件夹中的文件删除，再为移动到的文件夹添加新的文件。
	    					$('.wh-folderlist .whfolder ul li span[data-value="'+fileName+'"]').parents("li").first().remove();
	    					var li=$('<li>'+
		                                  '<i class="file-icon"></i>'+
		                                  '<span data-value="'+fileName+'">'+fileName+'</span>'+
		                                  '<i class="more-icon">'+
                                 	'</li>');
	    					$('.wh-folderlist .whfolder div span[data-value="'+value+'"]').parents(".whfolder").children("ul").append(li);
	    					//为新文件绑定事件
	    					waitRun();
	    				},
	    				error:function(result){
	    					console.log("移动文件失败！");
	    				},
	    				complete:function(){
	    					$(".div-more").remove();
	    					$(".div-moveto").remove();
	    				}
	    			});
	    		}
	    		return false;//IE
	    	});
	    	//选择文件夹change事件
	    	$("#folderSelect").on("change",function(event){
	    		//选择框的值
	    		var text=$(this).val();
	    		//文件夹输入框的值
	    		var val=$("#folderName").val();
	    		if(text.indexOf("请选择文件夹")!=-1){
	    			return;
	    		}
    			val=text;
    			$("#folderName").val(val);
	    	});
	    };
	}
