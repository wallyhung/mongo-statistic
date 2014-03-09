/**
 * map reduce
 */

function Map() {
	emit(this.fid, {count : 1,time : this.time})
}

function Reduce(key, values) {
	var res = {count : 0,time : ""};
	res.time = values[0].time;
	for (var i = 0; i < values.length; i++) {
		res.count += values[i].count; // 计算总值
		if (res.time > values[i].time) {
			res.time = values[i].time; // 置于时间的最小值
		}
	}
	return res;
}



function OReduce(key, values) 
{
	var count = 0;
	values.forEach(function(val) {
		count += val.count; 
	});
	return count;
}


function() {
	
	emit(this.fid,{count:1,request:this.request,
		                   push:this.push,
		                   view:this.view,
		                   click:this.click
		                   download:this.download
		                   install:this.install
		                   first:this.first})
}


function (key, values) {
	var res = {count : 0,request:0,push:0,view:0,click:0,download:0,install:0,first : ""};
	res.first = values[0].first;
	for (var i = 0; i < values.length; i++) {
		res.count += values[i].count;       // 计算总值
		res.request += values[i].request;     // 计算请求数
		res.push += values[i].push; 		// 计算推送数
		res.view += values[i].view; 		// 计算展示数
		res.click += values[i].click; 		// 计算点击数
		res.download += values[i].download; // 计算下载数
		res.install += values[i].install; 	// 计算安装数
		
		if (res.first > values[i].first) {
			res.first = values[i].first; // 置于时间的最小值
		}
	}
	return res;
}


function Map() {
	emit(
		this.day,				
		{count: 1, new_u:this.new_u, push:this.push,view:this.view,click:this.click,alive:this.alive}
	); 
	
}

function Reduce(key, values) {
	var res = {count : 0,new_u:0,push:0,view:0,click:0,alive:0};
	for (var i = 0; i < values.length; i++) {
		res.count += values[i].count;      
		res.new_u += values[i].new_u;     
		res.push += values[i].push; 	
		res.view += values[i].view; 	
		res.click += values[i].click; 	
		res.alive += values[i].alive; 
	}
	return res;
}