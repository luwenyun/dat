//筛选方式
	var addSub=function(sub_obj,add_obj){//定义一个新的方法来重用一个事件执行体
		//减少条件图标按钮单击事件
		sub_obj.on("click",function(){
			console.log("enter sub");
			var flag=parseInt($(this).parents('.condition-div').attr("flag"));
			console.log("flag:"+flag);
			if($('.condition-div').length==1){
				return;
			}
			$(this).parents('.condition-div').remove();
		});
		add_obj.on("click",function(){
			//增加条件图标按钮单击事件
			console.log("enter add-icon");
			var divStr=$('.for-condition .submit').prev().html();
			var div=$('<div class="condition-div" flag="">'+divStr+'</div>');
			div.insertBefore('.for-condition .submit');
			var flag=parseInt(div.prev().attr("flag"));
			div.attr("flag",flag+1);
			//给新生成的sub-icon,add-icon图标添加绑定事件
			//先移出之前绑定的事件，防止多次触发。
			$(".condition-label .add-icon").off("click");
			$(".condition-label .sub-icon").off("click");
			addSub($(".condition-label .sub-icon"),$(".condition-label .add-icon"));
		});
	}
	var add_icon=$(".condition-label .add-icon");
	var sub_icon=$(".condition-label .sub-icon");
	addSub(sub_icon,add_icon);
 	//条件过滤确定按钮单击事件
 	$('.for-condition .submit a').on("click",function(){
 		var  orAnd=$("#orAnd").val();
 		var fieldName=$(".fieldSelect").val();
 		var condition=$(".conditionSelect").val();
 		var value=$(".fieldValue").val();
 		console.log("orAnd:"+orAnd+"fieldName:"+fieldName+"condition:"+condition+"value:"+value)
 		if(fieldName.indexOf("请选择字段")!=-1||condition.indexOf("请选择条件语句")!=-1||value==undefined){
 			console.log("请输入完整的条件");
 			return;
 		}
 		$('.dealwith[flag="2"] .table table').empty();
 		conditionMethod(fieldName,condition,value);
 	});
 	//等于
 	var conditionMethod=function(fieldName,condition,value){
		var flag;//字段位置
		var table=$('.dealwith[flag="2"] .table table');
		var tr_th;
 		$('.dealwith[flag="1"] .table table tr').filter(function(index){
 			if(index==0){
 				tr_th=$(this);
 				$(this).children("th").filter(function(_index){
 					if($(this).text().indexOf(fieldName)!=-1){
 						flag=_index;
 						console.log("flag:"+flag);
 						table.append(tr_th);
 					}
 				});
 			}else{
 				var _this=$(this);
 				$(this).children("td").filter(function(_index) {
 					if(flag==_index){
 						console.log("_index:"+_index);
 						var td=$('<td></td>');
 						switch(condition){
				 			case "等于":
				 				console.log("td:"+$(this).text())
				 				if($(this).text()==value){
		 							//tr
		 							console.log("enter =");
						 			table.append(_this);
		 						}
				 				break;
				 			case "不等于":
				 				//不等于
				 				if($(this).text()!=value){
		 							//tr
		 							console.log("enter !=");
		 							table.append(_this);
		 						}
				 				break;
				 			case "包含":
				 				//包含
				 				if($(this).text().indexOf(value)!=-1){
				 					//tr
				 					console.log("enter in");
		 							table.append(_this);
 								}
				 				break;
				 			case "不包含":
				 				//不包含
				 				if($(this).text().indexOf(value)==-1){
				 					//tr
				 					console.log("enter not in");
		 							table.append(_this);
 								}
				 				break;
				 			case "为空":
				 				//为空
				 				console.log("enter null");
				 				if($(this).text()==""||$(this).text()==undefined){
				 					//tr
		 							table.append(_this);
				 				}
				 				break;
				 			case "不为空":
				 				//不为空
				 				console.log("enter not null");
				 				if($(this).text()!=""||$(this).text()!=undefined){
				 					//tr
		 							table.append(_this);
				 				}
				 				break;
				 			default:
				 			 	return;
				 		}
 					}
 				});
 			}
 		});
 	}
 	//表达式过滤字段选择单击事件-由于它是后来生成的标签，所以必须等它插入文档才能绑定事件
 	var  fieldUl_li=function(){
	 	$(".fieldUl li").on("click",function(){
	 		console.log("enter fieldUl li");
	 		var text=$(this).children("span").text();
	 		var val=$("#fieldMethod").val();
	 		console.log("text:"+text+"   "+"fieldMethod val:"+val);
	 		if(val==""||val==undefined){
	 			console.log("val=null");
	 			val="["+text+"]";
	 			$("#fieldMethod").val(val);
	 			$("#fieldMethod").focus();
	 		}else{
	 			//获取光标位置
	 			var obj=document.getElementById("fieldMethod");
	 			var pos=getCursorPosition(obj);
	 			var startStr=val.substring(0, pos);
	 			var endStr=val.substring(pos,val.length);
	 			console.log("startStr:"+startStr);
	 			console.log("pos:"+pos,val.substring(pos,val.length));
	 			if(val.indexOf(text)!=-1){
	 				console.log("不能选择重复字段");
	 				return;
	 			}
	 			val=startStr+"["+text+"]"+endStr;
	 			$("#fieldMethod").val(val);
	 			$("#fieldMethod").focus();
	 		}
	 	});
	 }
 	//表达式函数选择单击事件
 	$('.method-list ul li').on("click",function(){
 		console.log("enter fieldMethod li");
 		var text=$(this).children("span").text();
 		var val=$("#fieldMethod").val();
 		console.log("fieldMethod val:"+val);
 		if(val==""||val==undefined){
 			val=val+text+"()";
 			$("#fieldMethod").val(val);
 			$("#fieldMethod").focus();
 			var obj=document.getElementById("fieldMethod");
 			var pos=val.length-1;
 			//console.log("obj"+obj+"  "+"pos:"+pos);
 			setCursurPosition(obj,pos);
 		}else{
 			if(val.indexOf(text)!=-1){
 				//console.log("不能选择重复的函数");
 				return;
 			}
 			val=val+text+"()";
 			$("#fieldMethod").val(val);
 			$("#fieldMethod").focus();
 			var obj=document.getElementById("fieldMethod");
 			var pos=val.length-1;
 			//console.log("obj"+obj+"  "+"pos:"+pos);
 			setCursurPosition(obj,pos);
 		}
 	});
 	//获取光标位置
	function getCursorPosition(obj) {
	    var pos = 0;   // IE
	    if (document.selection) {
	        var Sel = document.selection.createRange();
	        Sel.moveStart ('character', -obj.text.length);
	        pos = Sel.text.length;
	    }
	    // 非IE
	    else if (obj.selectionStart || obj.selectionStart == '0')
	        pos = obj.selectionStart;
	    return pos;
	}
 	//设置光标位置
 	var setCursurPosition=function(obj,pos){
 		if(obj.setSelectionRange){//非IE
 			//开始位置与结束位置，如果开始位置与结束位置不同则表示选中
        	obj.setSelectionRange(pos,pos);
	    }else if (obj.createTextRange) {//IE
	        var range = obj.createTextRange();
	        range.collapse(true);
	        range.moveEnd('character', pos);
	        range.moveStart('character', pos);
	        range.select();
	    }
 	}
 	//表达式过滤确定按钮事件
 	$(".edit-submit").on("click",function(){
 		console.log("click endit-submit");
 		var val=$("#fieldMethod").val();
 		console.log("#fieldMethod:"+val);
 		if(val==""||val==undefined){
 			console.log("表达式不能为空");
 			return;
 		}
 		//字段
 		if(val.indexOf("=")!=-1){
 			console.log(val);
 			var strArr=val.split("=");
 			console.log(strArr);
	 		var fieldName=strArr[0].substring(1,strArr[0].length-1);
	 		var condition="等于";
	 		var value=strArr[1];
	 		console.log("fieldName:"+fieldName);
	 		$('.dealwith[flag="2"] .table table').empty();
 			conditionMethod(fieldName,condition,value);
 		}
 		//函数
 		if(val.indexOf("ROW_MAX")!=-1||val.indexOf("ROW_MIN")!=-1||val.indexOf("ROW_AVERAGE")!=-1){
 			//移出之前的table
 			$('.dealwith[flag="2"] .table table').empty();
 			if(val.indexOf("ROW_MAX")!=-1){
 				//获取字段位置
 				var start=val.indexOf("[");
 				var end=val.indexOf("]");
 				var fieldName=val.substring(start+1,end);//范围[start,end);
 				//获取该字段最大值所在的行
 				getMax_Min(fieldName,"max");
 			}else if(val.indexOf("ROW_MIN")!=-1){
 				//获取字段位置
 				var start=val.indexOf("[");
 				var end=val.indexOf("]");
 				var fieldName=val.subString(start+1,end);//范围[start,end);
 				//获取该字段最小值所在的行
 				getMax_Min(fieldName,"min");
 			}else{
 				console.log("未知的函数错误");
 				return;
 			}
 		}
 	});
 	//最大值
 	var getMax_Min=function(fieldName,type){
 		console.log("fieldName:"+fieldName);
 		var flag;
 		var max=0;
 		var min=0;
 		var tr_th;
 		var tr;
 		var table=$('.dealwith[flag="2"] .table table');
 		$('.dealwith[flag="1"] .table table tr').filter(function(index){
 			if(index==0){
 				tr_th=$(this);
 				$(this).children("th").filter(function(_index){
 					if($(this).text().indexOf(fieldName)!=-1){
 						flag=_index;
 						table.append(tr_th);
 					}
 				});
 			}else{
 				var _this=$(this)
 				$(this).children("td").filter(function(_index){
 					if(_index==flag){
 						var val=parseInt($(this).text());
 						if(type=="max"){
	 						if(val>max){//交换最大值
	 							max=val;
	 							tr=_this;
	 						}
 						}else{
 							if(val<min){//交换最小值
 								min=val;
 								tr=_this;
 							}
 						}
 					}
 				});
 			}
 		});
 		//生成该字段最大或者最小值所在的行
 		table.append(tr);
 	};
 	//生成多选字段列表
 	var checkBoxField=function(){
 		console.log("enter checkBoxField");
 		var fieldDate=$('.field_date');
 		var fieldText=$('.field_text');
 		var fieldNumber=$('.field_number');
 		var fieldValueArr=[];//定义一个存储字段值的数组
 		$('.dealwith[flag="1"] .table table tr').filter(function(index){
 			if(index==0){//获取字段的值
 				$(this).children("th").filter(function(_index){
 					fieldValueArr[_index]=$(this).text();
 				});
 			}
 			if(index==1){
 				$(this).children("td").filter(function(_index){
 					var fieldValue=$(this).text();
 					if(fieldValue.match(/^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}/)!=null||
 						fieldValue.match(/^[0-9]{4}\/[0-9]{1,2}\/[0-9]{1,2}/)!=null){
 						//日期字段
 						console.log("match date");
 						var dd=$('<dd>'+
                                    '<input type="checkbox" name="fieldName" >'+
                                    '<span>'+fieldValueArr[_index]+'</span>'+
                                '</dd>');
 						fieldDate.append(dd);
 					}else if(fieldValue.match(/^[$￥]*[0-9][0-9\.]{0,9}$/)!=null){
 						//数值字段
 						console.log("match number");
 						var dd=$('<dd>'+
                                    '<input type="checkbox" name="fieldName">'+
                                    '<span>'+fieldValueArr[_index]+'</span>'+
                                '</dd>');
 						fieldNumber.append(dd);
 					}else{
 						//文本字段
 						console.log("match text");
 						var dd=$('<dd>'+
                                    '<input type="checkbox" name="fieldName">'+
                                    '<span>'+fieldValueArr[_index]+'</span>'+
                                '</dd>');
 						fieldText.append(dd);
 					}

 				});
 			}
 			return;
 		});
 		//把没有匹配到的inpu隐藏
 		if($('.field_number dd span').text()==""){
 			$('.field_number').parents('.field-type').addClass("fn-hide");
 		}
 		if($('.field_text dd span').text()==""){
 			$('.field_text').parents('.field-type').addClass("fn-hide");
 		}
 		if($('.field_date dd span').text()==""){
 			$('.field_date').parents('.field-type').addClass("fn-hide");
 		}
	 	//为日期全选或者全不选绑定事件
	 	$(".field-type .all").on("click",function(){
	 		console.log("click input");
	 		var type=$(this).attr("name");
	 		console.log("type:"+type);
	 		if(type=="date"){
	 			var inputObj=$('.field_date dd input');
	 			allSelect($(this),inputObj);
	 		}else if(type=="text"){
	 			var inputObj=$('.field_text dd input');
				allSelect($(this),inputObj);
	 		}else{
	 			var inputObj=$('.field_number dd input');
	 			allSelect($(this),inputObj);
	 		}
	 	});
 	}
 	//全选或者全不选
 	var allSelect=function(obj,inputObj){
 			if(obj.is(':checked')){
 				inputObj.filter(function(index){
 					if($(this).is(':checked')==false){
 						$(this).click();
 					}
 				});
 			}else{
 				inputObj.filter(function(index){
 					if($(this).is(':checked')==true){
 						$(this).click();
 					}
 				});
 			}
 	};
 	//显示字段确定按钮事件
 	$(".submit-cf a").on("click",function(){
 		console.log("click submit-cf a");
 		var table=$('.dealwith[flag="2"] .table table');
 		//移出之前生成的table内容
 		table.empty();
 		var tr_th;
 		var tr;
 		var fieldSelectArr=[];//选中显示的字段列表
 		var fieldNOArr=[];//字段对应的列的位置列表
 		$('.field-type input[name="fieldName"]').filter(function(index){//遍历字段列表
 			if($(this).is(":checked")){
	 			var fieldName=$(this).next("span").text();
	 			fieldSelectArr[index]=fieldName;
	 		}
 		});
 		//遍历整个表
 		$('.dealwith[flag="1"] .table table tr').filter(function(index){
 			//把字段名和列位置对应起来
 			if(index==0){
 				tr_th=$("<tr></tr>")
 				var flag=0;
 				$(this).children("th").filter(function(_index){
 					var fileName=$(this).text();
 					for(var i=0;i<fieldSelectArr.length;i++){
 						if(fileName==fieldSelectArr[i]){
 							//th
 							tr_th.append($(this));
 							fieldNOArr[flag++]=_index;
 						}
 					}
 				});
 				//插入显示的字段
 				table.append(tr_th);
 			}else{
 				tr=$("<tr></tr>");
 				//把对应的列显示出来
 				$(this).children('td').filter(function(_index){
 					for(var i=0;i<fieldNOArr.length;i++){
 						if(_index==fieldNOArr[i]){
 							tr.append($(this));
 						}
 					}
 				});
 				table.append(tr);
 			}
 		});
 	});
	