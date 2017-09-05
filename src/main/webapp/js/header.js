$(".user li").on("click",function(){
	var name=$(this).attr("name");
	console.log("name:"+name);
	switch (name) {
		case "username":
			userInfo();
			break;
		case "bj":
			// statements_1
			break;
		case "information":
			// statements_1
			break;
		case "system-set":
			// statements_1
			break;
		case "help":
			// statements_1
			break;
		case "exit":
			exit_();
			break;
		case "talk":
			// statements_1
			break;
		default:
			// statements_def
			break;
	}
});
//用户信息
var userInfo=function(){
	console.log("enter userInfo");
	$.ajax({
		url:"http://localhost:8080/api/user/userInfo",
		type:"POST",
		dataType:"json",
		cache:false,
		success:function(result){
			if(result.error!=""||result.status=="0"){
				console.log(result.error);
				return;
			}
			//显示用户信息
			$(".userInfo-show input[name='nickname']").attr("value",result.result.nickname);
			$(".userInfo-show input[name='work']").attr("value",result.result.work);
			$(".userInfo-show input[name='sex']").attr("value",result.result.sex);
			$(".userInfo-show input[name='tel']").attr("value",result.result.tel);
			$(".userInfo-show input[name='email']").attr("value",result.result.email);
			$(".userInfo-show input[name='company']").attr("value",result.result.company);
			$(".userInfo-show").removeClass('fn-hide');
		},
		error:function(){
			console.log("获取用户信息失败");
		}
	})

}
//
$('.user-submit a').on("click",function(){
	if($(this).text()=="取消"){
		$(".userInfo-show").addClass('fn-hide');
		//更改修改图标透明度
		$(".modify-icon").css({
			opacity: '1',
		});
	}else{
		var data={};
		if($(".user-info").hasClass('selected')&&$(".user-pwd").hasClass('selected')){
			data["nickname"]=$(".userInfo-show input[name='nickname']").val();
			data["tel"]=$(".userInfo-show input[name='tel']").val();
			data["sex"]=$(".userInfo-show input[name='sex']").val();
			data["work"]=$(".userInfo-show input[name='work']").val();
			data["email"]=$(".userInfo-show input[name='email']").val();
			data["company"]=$(".userInfo-show input[name='company']").val();
			data["pwd"]=$(".userInfo-show input[name='pwd']").val();
			data["type"]="3";
		}
		else if($(".user-info").hasClass('selected')){
			data["nickname"]=$(".userInfo-show input[name='nickname']").val();
			data["tel"]=$(".userInfo-show input[name='tel']").val();
			data["sex"]=$(".userInfo-show input[name='sex']").val();
			data["work"]=$(".userInfo-show input[name='work']").val();
			data["email"]=$(".userInfo-show input[name='email']").val();
			data["company"]=$(".userInfo-show input[name='company']").val();
			data["type"]="2";
		}
		else if($(".user-pwd").hasClass('selected')){
			var pwd=$(".userInfo-show input[name='pwd']").val();
			if(pwd.indexOf("*")!=-1||pwd.length>16||pwd.length<6){
				console.log("密码格式不正确");
				return;
			}
			data["pwd"]=$(".userInfo-show input[name='pwd']").val();
			data["type"]="1";
		}else{
			console.log("没有修改");
			return;
		}
		console.log(data);
		$.ajax({
			url:"http://localhost:8080/api/user/updateUserInfo",
			type:"POST",
			dataType:"json",
			data:data,
			cache:false,
			success:function(result){
				if(result.error!=""||result.status=="0"){
				console.log(result.error);
				return;
				}
				//更改input为不可编辑状态
				$(".userInfo-show input[name='nickname']").attr("disabled","disabled");
				$(".userInfo-show input[name='work']").attr("disabled","disabled");
				$(".userInfo-show input[name='sex']").attr("disabled","disabled");
				$(".userInfo-show input[name='tel']").attr("disabled","disabled");
				$(".userInfo-show input[name='email']").attr("disabled","disabled");
				$(".userInfo-show input[name='pwd']").attr("disabled","disabled");
				$(".userInfo-show input[name='company']").attr("disabled","disabled");
				//把之前选中变为不选中
				if($(".user-pwd").hasClass('selected')){
					$(".user-pwd").removeClass('selected');
				}
				if($(".user-info").hasClass('selected')){
					$(".user-info").removeClass('selected');
				}
				$(".userInfo-show").removeClass('fn-hide');
				//更改修改图标透明度
				$(".modify-icon").css({
					opacity: '1',
				});
			},
			error:function(){
				console.log("修改用户信息失败");
			}
		})
	}
});
//修改图标点击事件
$(".modify-icon").on("click",function(){
	$(this).css({
		opacity: '0.5',
	});
	if($(this).parents(".user-pwd").length==1){
		//修改密码
		$(".user-pwd").addClass("selected");
		//把input改为可编辑状态
		$(".userInfo-show input[name='pwd']").attr("disabled",false);
		//获得焦点
		$(".userInfo-show input[name='pwd']").focus();
	}else{
		//修改个人信息
		$(".user-info").addClass("selected");
		//把input改为可编辑状态
		$(".userInfo-show input[name='nickname']").attr("disabled",false);
		$(".userInfo-show input[name='work']").attr("disabled",false);
		$(".userInfo-show input[name='sex']").attr("disabled",false);
		$(".userInfo-show input[name='tel']").attr("disabled",false);
		$(".userInfo-show input[name='email']").attr("disabled",false);
		$(".userInfo-show input[name='company']").attr("disabled",false);
		//获得焦点
		$(".userInfo-show input[name='nickname']").focus();
	};
});
//注销
var exit_=function(){
	console.log("enter exit_");
	$.ajax({
		url:"http://localhost:8080/api/user/exit",
		type:"POST",
		dataType:"json",
		cache:false,
		success:function(result){
			if(result.error!=""||result.status=="0"){
				console.log(result.error);
				return;
			}
			window.location.href="/login.html";
		},
		error:function(){
			console.log("注销失败");
		}
	})
}