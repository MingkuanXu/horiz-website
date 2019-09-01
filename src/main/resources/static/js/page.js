//分页插件
/**
2017-06-15 zh
**/
(function($){
	var ms = {
		init:function(obj,args){
			return (function(){
				ms.fillHtml(obj,args);
				ms.bindEvent(obj,args);
				
			})();
		},
		//填充html
		fillHtml:function(obj,args){
			return (function(){
				obj.empty();
 
				//上一页
				if(parseInt(args.current) > 1){
					obj.append('<a href="javascript:;" class="prevPage"><p class="previous">上一页</p></a>');
				}else{
					obj.remove('.prevPage');
					obj.append('<span class="disabled"><p class="previous">上一页</p></span>');
				}
				//中间页码
				if(parseInt(args.current) != 1 && parseInt(args.current) >= 4 && parseInt(args.pageCount) != 4){
					obj.append('<a href="javascript:;" class="tcdNumber"><p>'+1+'</p></a>');
				}
				if(parseInt(args.current-2) > 2 && parseInt(args.current) <= parseInt(args.pageCount) && parseInt(args.pageCount) > 5){
					obj.append('<span><p>...</p></span>');
				}
				var start = parseInt(args.current) -2;
				var end = parseInt(args.current) +2;
				if((start > 1 && parseInt(args.current) < 4)||parseInt(args.current) == 1){
					end++;
				}
				if(parseInt(args.current) > parseInt(args.pageCount)-4 && parseInt(args.current) >= parseInt(args.pageCount)){
					start--;
				}
				for (;start <= end; start++) {
					if(start <= parseInt(args.pageCount) && start >= 1){ 
						if(start != parseInt(args.current)){
							obj.append('<a href="javascript:;" class="tcdNumber"><p>'+ start +'</p></a>'); 
						}else{
							obj.append('<span class="current"><p class="current">'+ start +'</p></span>');
						}
					}
				}
				if(parseInt(args.current) + 2 < parseInt(args.pageCount) - 1 && parseInt(args.current) >= 1 && parseInt(args.pageCount) > 5){
					obj.append('<span><p>...</p></span>'); 
				}
				if(parseInt(args.current) != parseInt(args.pageCount) && parseInt(args.current) < parseInt(args.pageCount) -2  && parseInt(args.pageCount) != 4){
					obj.append('<a href="javascript:;" class="tcdNumber"><p>'+args.pageCount+'</p></a>');
				}
				//下一页
				if(parseInt(args.current) < parseInt(args.pageCount)){
					obj.append('<a href="javascript:;" class="nextPage"><p class="next">下一页</p></a>');
				}else{
					obj.remove('.nextPage');
					obj.append('<span class="disabled"><p class="next">下一页</p></span>');
				}
 
				obj.append('<span><p style="border:none;">跳转<input type="text" id="pageIndex" onkeyup="var current = $(this).val();if (isNaN(current)) {$(this).val(\'\');}if (current.indexOf(\'.\') != -1) {$(this).val(\'\');}">页</p><a href="javascript:;" class="btn">确定</a></span>');
			})();
		},
		//绑定事件
		bindEvent:function(obj,args){
			return (function(){
				obj.off("click","a.tcdNumber");
				obj.on("click","a.tcdNumber",function(){
					var current = parseInt($(this).text());
					ms.fillHtml(obj,{"current":current,"pageCount":args.pageCount});
					if(typeof(args.backFn)=="function"){
						args.backFn(current);
					}
				});
				//上一页
				obj.off("click","a.prevPage");
				obj.on("click","a.prevPage",function(){
					var current = parseInt(obj.children("span.current").text());
					ms.fillHtml(obj,{"current":current-1,"pageCount":args.pageCount});
					if(typeof(args.backFn)=="function"){
						args.backFn(current-1);
					}
				});
				//下一页
				obj.off("click","a.nextPage");
				obj.on("click","a.nextPage",function(){
					var current = parseInt(obj.children("span.current").text());
					ms.fillHtml(obj,{"current":current+1,"pageCount":args.pageCount});
					if(typeof(args.backFn)=="function"){
						args.backFn(current+1);
					}
				});
				obj.off("click","a.btn");
				obj.on("click","a.btn",function(){
					var current =$("#pageIndex").val();
				
					if(parseInt(current)>0 && parseInt(current)<=parseInt(args.pageCount) && current!=""){
					            ms.fillHtml(obj,{"current":current,"pageCount":args.pageCount});
				              	if(typeof(args.backFn)=="function"){
					        	args.backFn(current);
				          	}
				              	$("#pageIndex").val(current);
					}else{
						$("#pageIndex").val("");
					}
				});
				
			})();
		},
		
			
   	init1:function(obj,args){
			return (function(){
				ms.fillHtml(obj,args);
			})();
		}
	}
	
	$.fn.createPage = function(options){
		var args = $.extend({
			pageCount : 50,
			current : 1,
			backFn : function(){
				
			}
		},options);
		ms.init(this,args);
	};
	
})(jQuery);
