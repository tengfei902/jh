<%@ page import="org.springframework.web.context.WebApplicationContext" contentType="text/html; charset=GB2312"%>
<%@ page import="jh.biz.service.PageService" %>
<%@ page import="jh.model.dto.PageInfo" %>
<%@ page import="java.util.List" %>
<%@ page import="jh.model.PageLayOut" %>
<html><head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">
    <title>������֧��ϵͳ</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link href="Public/Front/css/bootstrap.min.css" rel="stylesheet">
    <link href="Public/Front/css/font-awesome.min.css" rel="stylesheet">
    <link href="Public/Front/css/animate.css" rel="stylesheet">
    <link href="Public/Front/css/style.css" rel="stylesheet">
    <!-- <link href="Public/Admin/css/head.css" rel="stylesheet">
    <link href="Public/Admin/css/nav.css" rel="stylesheet"> -->
    <link href="css/head.css" rel="stylesheet">
    <link href="css/nav.css" rel="stylesheet">
    <link href="css/style1.css" rel="stylesheet">
    <link href="css/nav.css" rel="stylesheet">

    <style>
        #wrapper{
            overflow:hidden !important;
        }
    </style>

    <%
        WebApplicationContext context = (WebApplicationContext)this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        PageService pageService = (PageService) context.getBean("pageServiceImpl");
        List<PageInfo> list = pageService.getPageList();
        System.out.println("--------------:"+list.size());
    %>

    <meta name="__hash__" content="11f6b3285bf71a87811c3dadccb7b166_4dbc565f6b2f4135ce9c6ca789ef40ca"><link id="layuicss-laydate" rel="stylesheet" href="http://demo.ddi9.com/Public/Front/js/plugins/layui/css/modules/laydate/default/laydate.css?v=5.0.2" media="all"><link id="layuicss-layer" rel="stylesheet" href="http://demo.ddi9.com/Public/Front/js/plugins/layui/css/modules/layer/default/layer.css?v=3.0.3" media="all"><link id="layuicss-skincodecss" rel="stylesheet" href="http://demo.ddi9.com/Public/Front/js/plugins/layui/css/modules/code.css" media="all"></head>
<body class="fixed-sidebar full-height-layout gray-bg  pace-done"><div class="pace  pace-inactive"><div class="pace-progress" data-progress-text="100%" data-progress="99" style="width: 100%;">
    <div class="pace-progress-inner"></div>
</div>
    <div class="pace-activity"></div></div>
<div id="wrapper" style="background-color:#3c4043">
    <!-- ͷ����ʼ -->
    <div class="row head-bar" id="head-bar">
        <div class="col-lg-5 col-md-4 col-sm-3 col-xs-6 head-left">
            <img src="./images/logo.png">
            <span class="hidden-xs hidden-sm">�����̨</span>
        </div>
        <div class="col-lg-7 col-md-8 col-sm-9 col-xs-6 head-right">
            <div class="hidden-xs">
                <a href="../shouye/shouye.html" class="index">��վ��ҳ</a>
                <a href="Admin_Index_clearCache.html" class="qchc">�������</a>
                <a href="javascript:;" onclick="reset_pwd('�޸�����','Admin_System_editPassword.html',360,320)" class="edit">�޸�����</a>
                <a href="Admin_Login_loginout.html" class="loginout">�˳�</a>
            </div>
            <div class="dropdown ">
    		<span class="clear">
	            <span class="block m-t-xs">
	                adminroot	            </span>
	            <span class="text-muted text-xs block">
	                �ܹ���Ա	            </span>
            </span>
            </div>
            <div class="userimg hidden">
                <span><img alt="image" class="img-circle" src="/Public/Front/img/avatar.jpg"></span>
            </div>
            <div class="welcom hidden-xs" style="padding:0;color:#fff;font-size:16px;float:right;margin-right:20px">��ӭ��</div>
        </div>
    </div>
    <!-- ͷ������ -->
    <!--��ർ����ʼ-->
    <nav class="navbar-default navbar-static-side" role="navigation">
        <div class="nav-close"><i class="fa fa-times-circle"></i></div>
        <div class="slimScrollDiv" style="position: relative; width: auto; height: 100%;"><div class="sidebar-collapse" style="width: auto; height: 100%;">
            <ul class="nav" id="side-menu">
                <li class="nav-header visible-xs">
                    <div class="logo-element visible-xs">MENU</div>
                </li>

                <%
                    for(PageInfo pageInfo:list) {
                        PageLayOut page1 = pageInfo.getPageLayOut();
                %>
                        <li class="nav-menu"><a href="<%=page1.getPath()%>"> <i class="<%=page1.getType()%>"></i> <span class="nav-label"><%=page1.getName()%></span></a>
                            <ul class="nav nav-second-level collapse" style="">
                            <%
                                for(PageLayOut pageLayOut: pageInfo.getSubList()) {
                            %>
                                    <li><a href="<%=pageLayOut.getPath()%>" class="<%=pageLayOut.getType()%>" data-index="<%=pageLayOut.getRowIndex()%>"><%=pageLayOut.getName()%></a></li>
                            <%
                                }
                            %>
                            </ul>
                        </li>
                <%
                    }
                %>
            </ul>
        </div><div class="slimScrollBar" style="background: rgb(0, 0, 0); width: 4px; position: absolute; top: 0px; opacity: 0.4; display: none; border-radius: 7px; z-index: 99; right: 1px; height: 974px;"></div><div class="slimScrollRail" style="width: 4px; height: 100%; position: absolute; top: 0px; display: none; border-radius: 7px; background: rgb(51, 51, 51); opacity: 0.9; z-index: 90; right: 1px;"></div></div>
    </nav>


    <!--��ർ������-->
    <!--�Ҳಿ�ֿ�ʼ-->
    <div id="page-wrapper" class="gray-bg dashbard-1" style="padding-right:0">
        <div class="navbar-header hidden" style="position:absolute;"><a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="#"><i class="fa fa-bars"></i> </a></div>
        <div class="row J_mainContent" id="content-main">
            <iframe class="J_iframe" name="iframe0" width="100%" height="100%" src="main.html" frameborder="0" data-id="main.html" seamless="" style="display: none;"></iframe>
            <iframe class="J_iframe" name="iframe1" width="100%" height="100%" src="Admin_System_base.html" frameborder="0" data-id="Admin_System_base.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe2" width="100%" height="100%" src="/Admin_System_email.html" frameborder="0" data-id="Admin_System_email.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe1" width="100%" height="100%" src="Admin_System_base.html" frameborder="0" data-id="Admin_System_base.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe2" width="100%" height="100%" src="Admin_System_email.html" frameborder="0" data-id="Admin_System_email.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe1" width="100%" height="100%" src="Admin_System_base.html" frameborder="0" data-id="Admin_System_base.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe2" width="100%" height="100%" src="Admin_System_email.html" frameborder="0" data-id="Admin_System_email.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe10" width="100%" height="100%" src="/Admin_Order_changeRecord.html" frameborder="0" data-id="Admin_Order_changeRecord.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe9" width="100%" height="100%" src="Admin_Order_index.html" frameborder="0" data-id="Admin_Order_index.html" seamless="" style="display: none;"></iframe><iframe class="J_iframe" name="iframe10" width="100%" height="100%" src="Admin_Order_changeRecord.html" frameborder="0" data-id="Admin_Order_changeRecord.html" seamless=""></iframe></div>

    </div>
    <!--�Ҳಿ�ֽ���-->
</div>
<!-- ȫ��js -->

<!-- <script src="/Public/Front/js/jquery.min.js"></script> -->
<!-- <script src="/Public/Front/js/bootstrap.min.js"></script> -->
<script src="js/jquery.peity.min.js"></script>
<!-- <script src="/Public/Front/js/content.js"></script> -->
<!-- <script src="/Public/Front/js/plugins/layui/layui.all.js" charset="utf-8"></script> -->
<!-- <script src="/Public/Front/js/x-layui.js" charset="utf-8"></script> -->
<!-- <script src="Public/Front/js/plugins/metisMenu/jquery.metisMenu.js"></script> -->
<!-- <script src="Public/Front/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
<!-- <script src="http://demo.ddi9.com/Public/Front/js/hplus.js"></script> -->
<script src="http://demo.ddi9.com/Public/Front/js/iNotify.js"></script>
<script src="http://demo.ddi9.com/Public/Front/js/plugins/pace/pace.min.js"></script>

<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/content.js"></script>
<script src="js/hplus.js"></script>
<script src="js/x-layui.js" charset="utf-8"></script>
<script src="js/layui.all.js" charset="utf-8"></script>
<script src="Public/Front/js/contabs.js"></script>
<script src="js/content.js"></script>
<script>
    layui.use(['laypage', 'layer', 'form'], function () {
        var form = layui.form,
                layer = layui.layer,
                $ = layui.jquery;
    });
    function reset_pwd(title,url,w,h){
        x_admin_show(title,url,w,h);
    }
</script>
<script>
    var iNotify = new iNotify({
        message: '����Ϣ�ˡ�',//����
        effect: 'flash', // flash | scroll ��˸���ǹ���
        interval: 300,
        audio:{
            //file: ['/Public/sound/msg.mp4','/Public/sound/msg.mp3','/Public/sound/msg.wav']
            file:'http://tts.baidu.com/text2audio?lan=zh&ie=UTF-8&spd=5&text=�пͻ�����������'
        }
    });
    setInterval(function() {
        $.ajax({
            type: "GET",
            url: "Admin_Withdrawal_checkNotice.html",
            cache: false,
            success: function (res) {
                if (res.num>0) {
                    iNotify.setFavicon(res.num).setTitle('����֪ͨ').notify({
                        title: "��֪ͨ",
                        body: "�пͻ���������..."
                    }).player();
                }
            }
        });
    },10000);

    // lm
    $(".nav-menu").click(function(){
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        $(this).children(".nav").toggleClass("in");
        $(this).siblings().children(".nav").removeClass("in");
        console.log($(this).children(".nav"));
    });
</script>
<audio src="http://tts.baidu.com/text2audio?lan=zh&amp;ie=UTF-8&amp;spd=5&amp;text=�пͻ�����������"></audio>

<div style="display:none;"><script type="text/javascript">var cnzz_protocol = (("https:" == document.location.protocol) ? " https://" : " http://");document.write(unescape("%3Cspan id='cnzz_stat_icon_1261742514'%3E%3C/span%3E%3Cscript src='" + cnzz_protocol + "s11.cnzz.com/stat.php%3Fid%3D1261742514' type='text/javascript'%3E%3C/script%3E"));</script><span id="cnzz_stat_icon_1261742514"><a href="http://www.cnzz.com/stat/website.php?web_id=1261742514" target="_blank" title="վ��ͳ��">վ��ͳ��</a></span><script src=" http://s11.cnzz.com/stat.php?id=1261742514" type="text/javascript"></script><script src="http://c.cnzz.com/core.php?web_id=1261742514&amp;t=z" charset="utf-8" type="text/javascript"></script></div>
</body>
</html>