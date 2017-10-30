/*寮瑰嚭灞�*/
/*
	鍙傛暟瑙ｉ噴锛�
	title	鏍囬
	url		璇锋眰鐨剈rl
	id		闇€瑕佹搷浣滅殑鏁版嵁id
	w		寮瑰嚭灞傚搴︼紙缂虹渷璋冮粯璁ゅ€硷級
	h		寮瑰嚭灞傞珮搴︼紙缂虹渷璋冮粯璁ゅ€硷級
*/
function x_admin_show(title,url,w,h){
	if (title == null || title == '') {
		title=false;
	};
	if (url == null || url == '') {
		url="404.html";
	};
	if (w == null || w == '') {
		w=800;
	};
	if (h == null || h == '') {
		h=($(window).height() - 50);
	};
	layer.open({
		type: 2,
		area: [w+'px', h +'px'],
		fix: false, //涓嶅浐瀹�
		maxmin: true,
		shadeClose: true,
		shade:0.4,
		title: title,
		content: url
	});
}

/*鍏抽棴寮瑰嚭妗嗗彛*/
function x_admin_close(){
	var index = parent.layer.getFrameIndex(window.name);
	parent.layer.close(index);
}