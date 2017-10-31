/*
 *
 *   H+ - 鍚庡彴涓婚UI妗嗘灦
 *   version 4.5
 *
*/

var $parentNode = window.parent.document;

function $childNode(name) {
    return window.frames[name]
}

// tooltips
$('.tooltip-demo').tooltip({
    selector: "[data-toggle=tooltip]",
    container: "body"
});

// 浣跨敤animation.css淇敼Bootstrap Modal
$('.modal').appendTo("body");

$("[data-toggle=popover]").popover();

//鎶樺彔ibox
$('.collapse-link').click(function () {
    var ibox = $(this).closest('div.ibox');
    var button = $(this).find('i');
    var content = ibox.find('div.ibox-content');
    content.slideToggle(200);
    button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
    ibox.toggleClass('').toggleClass('border-bottom');
    setTimeout(function () {
        ibox.resize();
        ibox.find('[id^=map-]').resize();
    }, 50);
});

//鍏抽棴ibox
$('.close-link').click(function () {
    var content = $(this).closest('div.ibox');
    content.remove();
});

//鍒ゆ柇褰撳墠椤甸潰鏄惁鍦╥frame涓�
// if (top == this) {
//     var gohome = '<div class="gohome"><a class="animated bounceInUp" href="index.html?v=4.0" title="杩斿洖棣栭〉"><i class="fa fa-home"></i></a></div>';
//     $('body').append(gohome);
// }

//animation.css
function animationHover(element, animation) {
    element = $(element);
    element.hover(
        function () {
            element.addClass('animated ' + animation);
        },
        function () {
            //鍔ㄧ敾瀹屾垚涔嬪墠绉婚櫎class
            window.setTimeout(function () {
                element.removeClass('animated ' + animation);
            }, 2000);
        });
}

//鎷栧姩闈㈡澘
function WinMove() {
    var element = "[class*=col]";
    var handle = ".ibox-title";
    var connect = "[class*=col]";
    $(element).sortable({
        handle: handle,
        connectWith: connect,
        tolerance: 'pointer',
        forcePlaceholderSize: true,
        opacity: 0.8,
    })
        .disableSelection();
};