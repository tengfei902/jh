// JavaScript Document
$(".paytype").click(function(){
	$(".banklist").hide();
});
$(".bankpay").click(function(){
	$(".banklist").show();
});
$(".payTypeOnly").click(function(event){
	var bankid=$(this).data("bankid");
	$("input[name='payType']").attr("value",bankid);
	$(".payTypeOnly").parent().removeClass("active");
	$(this).parent().addClass("active");
	// var left=event.offsetX;
	// var top=event.offsetY;
	// console.log(event.offsetX);
	// console.log(event.offsetY);
	
});
// $(".type").click(function(event){
// 	console.log(event.offsetX);
// 	console.log(event.offsetY);
// 	$(".paytypeBorder").animate({left:left,top:top});
// });
$(".change").click(function(){
	$(".mask").css("display","none");
	$(".notice-box").css("display","none");
	$(".steps .payType").addClass("current");
	$("http://demo.ddi9.com/Public/Home/js/.steps .Pay").removeClass("current");
});
$(".paynow").click(function(){
	$(".mask").css("display","block");
	$(".notice-box").css("display","block");
	$(".steps .payType").removeClass("current");
	$("http://demo.ddi9.com/Public/Home/js/.steps .Pay").addClass("current");
});
$(".over").click(function(){
	window.location="http://demo.ddi9.com/Ex_callback.html";
});
$(".goindex").click(function(){
	window.location="http://demo.ddi9.com/Home_Ex_index.html";
});
$(".agen").click(function(){
	window.location="http://demo.ddi9.com/Home_Ex_index.html";
});