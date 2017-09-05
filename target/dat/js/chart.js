//图表可视化
//柱形图

// var data=[100,200,130,50,70,300];
//请求数据，并分类数据
$(".chart-icon").on("click",function(){
	//类型
	var type=$(this).parent().attr("title");
	console.log("type:"+type);
	//请求数据
	//字段个数
	//每个字段的值
	//每个字段的类型
	var data={};
	data["fields"]=$('input[name="fieldText').val()+"/"+$('input[name="fieldNum').val();
	data["folderName"]=$(".data-view .file-selected").parents(".whfolder").children("div").children("span").text();
	data["fileName"]=$(".data-view .file-selected").children("span").text();
	console.log(data);
	$.ajax({
		url:"http://localhost:8080/api/chart/getFieldValues",
		type:"POST",
		dataType:"json",
		data:data,
		cache:false,
		success:function(result){
			if(result.error!=""||result.status=="0"){
				console.log(result.error);
				return;
			}
			var map_=dealData(result);
			console.log(result);
			switch (type) {
				case "折线图":
					// statements_1
					ZX(map_);
					break;
				case "簇状柱形图":
					CZZX(map_);
					break;
				case "饼状图":
					BJ(map_);
					break;
				case "面积图":
					MJ(map_);
					break;
				default:
					// statements_def
					break;
			}
		},
		error:function(result){
			console.log("获取维度和数值数据失败");
		}
	});
});
var dealData=function(result){
	var arr=result.result.fieldValue;
	var len=arr.length;
	var type;
	var result_={};
	var data=new Array();
	var type;
	var flag_date=-20;
	var flag_number=-20;
	var flag_text=-20;
	var map_length=0;
	for(var name in arr[0]){
		if(arr[0][name].match(/^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}/)!=null||
				arr[0][name].match(/^[0-9]{4}\/[0-9]{1,2}\/[0-9]{1,2}/)!=null){
				//日期字段
				flag_date=map_length;
			}else if(arr[0][name].match(/^[$￥]*[0-9][0-9\.]{0,9}$/)!=null){
				//数值字段
				flag_number=map_length;
			}else{
				//文本字段
				flag_text=map_length;
			}
		map_length=map_length+1;
	}
	console.log("flag_date:"+flag_date);
	console.log("flag_date:"+flag_number);
	var flag=0;
	list:for(var i=0;i<len;i++){
		var list=arr[i];
		var map={};
		var length_=0;
		map:for(var name in list){
			if(length_==flag_date){
				if(list[name].match(/^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}/)!=null||
					list[name].match(/^[0-9]{4}\/[0-9]{1,2}\/[0-9]{1,2}/)!=null){
					map.date=list[name];
				}else{
					console.log("error date");
					break;
				}
			}
			if(length_==flag_text){
				map.text=list[name];
			}
			if(length_==flag_number){
				if(list[name].match(/^[$￥]*[0-9][0-9\.]{0,9}$/)==null){
					console.log("error number");
					break;
				}
				map.number=list[name];
			}
			length_=length_+1;
		}
		if(d3.map(map).size()==0){
			console.log("map==0");
			continue;
		}
		data[flag]=map;
		flag=flag+1;
	}
	console.log("data.length:"+data.length);
	result_.data=data;
	result_.type=type;
	//console.log("data:"+data);
	return result_;
}
var CZZX=function(result){
	console.log("enter CZZX:");
	console.log(result.type);
	$(".edit-body .svg-d3 svg").empty();
	var data=result.data;
	//选择集
	var svg = d3.select("svg"),
    margin = {top: 20, right: 20, bottom: 30, left: 40},
    //画布长和宽
    width = +svg.attr("width") - margin.left - margin.right,
    height = +svg.attr("height") - margin.top - margin.bottom;
    //序数比例尺和线性比例尺值域
	var x = d3.scaleTime()
	    .rangeRound([0, width])
	    .domain(d3.extent(data,function(d){return new Date(d.date);}));
	var y = d3.scaleLinear()
	    .rangeRound([height, 0])
		.domain(d3.extent(data,function(d){return +d.number;}));
	//生成g
	var g = svg.append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	//x横坐标
	g.append("g")
	    .attr("class", "axis axis--x")
	    .attr("transform", "translate(0," + height + ")")
	    .call(d3.axisBottom(x));
	//y纵坐标
	g.append("g")
	    .attr("class", "axis axis--y")
	    .call(d3.axisLeft(y))
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", "0.71em")
        .attr("text-anchor", "end")
        .text("Frequency");
	  //rect图形
	  g.selectAll(".bar")
	    .data(data)
	    .enter().append("rect")
	      .attr("class", "bar")
	      .attr("x", function(d) { return x(new Date(d.date)); })
	      .attr("y", function(d) { return y(+d.number); })
	      .attr("width", "30px")
	      .attr("height", function(d) { return height - y(+d.number); });

}
//折线图
var ZX=function(result){
	console.log("enter ZX:");
	$(".edit-body .svg-d3 svg").empty();
	console.log(result.type);
	var data=result.data;
	var mean=d3.mean(data,function(d){return d.number;});
	console.log("mean:"+mean);
	var dateMax=d3.extent(data,function(d){return d3.timeFormat("%Y-%m-%d")(new Date(d.date));});
	var dataMax=d3.extent(data,function(d){return +d.number});
	console.log("numbermax:"+dataMax);
	console.log("dateMax:"+dateMax);
	//获取svg选择集
	var svg = d3.select("svg"),
    margin = {top: 20, right: 20, bottom: 30, left: 80},
    //设置画布长宽
    width = +svg.attr("width") - margin.left - margin.right,
    height = +svg.attr("height") - margin.top - margin.bottom,
    //生成g
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	//时间比例尺值域
	var x = d3.scaleTime()
	    .rangeRound([0, width])
	    .domain(d3.extent(data,function(d){return new Date(d.date);}));
	//线性比例尺值域
	var y = d3.scaleLinear()
	    .rangeRound([height, 0])
		.domain(d3.extent(data,function(d){return +d.number;}));
	//线条
	var line = d3.line()
	    .x(function(d) { return x(new Date(d.date));})
	    .y(function(d) { return y(+d.number); });
	//设置x坐标轴
	g.append("g")
	    .attr("transform", "translate(0," + height + ")")
	    .call(d3.axisBottom(x));
	    // .select(".domain");
	    // .remove();
	//设置y坐标轴
	g.append("g")
	      .call(d3.axisLeft(y))
	    .append("text")
	      .attr("fill", "#000")
	      .attr("transform", "rotate(-90)")
	      .attr("y", 6)
	      .attr("dy", "0.71em")
	      .attr("text-anchor", "end")
	      .text("size ($)");
	//设置path
	g.append("path")
	      .datum(data)
	      .attr("fill", "none")
	      .attr("stroke", "steelblue")
	      .attr("stroke-linejoin", "round")
	      .attr("stroke-linecap", "round")
	      .attr("stroke-width", 1.5)
	      .attr("d", line);
};
//面积图
var MJ=function(result){
	console.log("enter mj:");
	$(".edit-body .svg-d3 svg").empty();
	console.log(result.type);
	var data=result.data;
	var mean=d3.mean(data,function(d){return d.number;});
	console.log("mean:"+mean);
	var dateMax=d3.extent(data,function(d){return d3.timeFormat("%Y-%m-%d")(new Date(d.date));});
	var dataMax=d3.extent(data,function(d){return +d.number});
	console.log("numbermax:"+dataMax);
	console.log("dateMax:"+dateMax);
	//获取svg选择集
	var svg = d3.select("svg"),
    margin = {top: 20, right: 20, bottom: 30, left: 80},
    //设置画布长宽
    width = +svg.attr("width") - margin.left - margin.right,
    height = +svg.attr("height") - margin.top - margin.bottom,
    //生成g
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	//时间比例尺值域
	var x = d3.scaleTime()
	    .rangeRound([0, width])
	    .domain(d3.extent(data,function(d){return new Date(d.date);}));
	//线性比例尺值域
	var y = d3.scaleLinear()
	    .rangeRound([height, 0])
		.domain(d3.extent(data,function(d){return +d.number;}));
	//面积
	var area = d3.area()
	    .x(function(d) { return x(new Date(d.date));})
	    .y(function(d) { return y(+d.number); });
	//设置x坐标轴
	g.append("g")
	    .attr("transform", "translate(0," + height + ")")
	    .call(d3.axisBottom(x));
	//设置y坐标轴
	g.append("g")
	    .call(d3.axisLeft(y))
	    .append("text")
	      .attr("fill", "#000")
	      .attr("transform", "rotate(-90)")
	      .attr("y", 6)
	      .attr("dy", "0.71em")
	      .attr("text-anchor", "end")
	      .text("size ($)");
	//设置path
	g.append("path")
      .datum(data)
      .attr("fill", "steelblue")
      .attr("d", area);
};