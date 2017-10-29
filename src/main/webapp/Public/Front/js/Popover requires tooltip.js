<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>404--威付宝支付系统</title>
  <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
  <script type="text/javascript">jQuery.extend(jQuery.easing,{easeInExpo:function(x,t,b,c,d){return(t==0)?b:c*Math.pow(2,10*(t/d-1))+b},easeInOutExpo:function(x,t,b,c,d){if(t==0)return b;if(t==d)return b+c;if((t/=d/2)<1)return c/2*Math.pow(2,10*(t-1))+b;return c/2*(-Math.pow(2,-10*--t)+2)+b},easeOutElastic:function(x,t,b,c,d){var s=1.70158;var p=0;var a=c;if(t==0)return b;if((t/=d)==1)return b+c;if(!p)p=d*.3;if(a<Math.abs(c)){a=c;var s=p/4}else var s=p/(2*Math.PI)*Math.asin(c/a);return a*Math.pow(2,-10*t)*Math.sin((t*d-s)*(2*Math.PI)/p)+c+b},});$(function(){$('#links').hide();$('dt:not(#fourohfour)').css('opacity',.4);$('dd:not(#fourohfour + dd)').css('opacity',.4);$('#fourohfour').css({'position':'relative','z-index':'1'});$('#fourohfour + dd').css({'position':'relative','z-index':'1'});var boxHeight=$('#fourohfour').height()+$('#fourohfour + dd').height();var highlightPadding=parseInt($('#highlight').css('paddingTop'))+parseInt($('#highlight').css('paddingBottom'));$('#highlight').css({'opacity':0.8,'top':Math.floor(.5*$(window).height()-.5*boxHeight-.5*highlightPadding)+'px'}).height(boxHeight);var offset=$('#fourohfour').offset();var final=offset.top-.5*$(window).height()+.5*boxHeight;setTimeout(function(){$('dl').animate({marginTop:-final+'px'},2000,'easeInOutExpo',function(){$('#links').slideDown()})},1000)});</script>

  <style>
    * {
      margin: 0;
      padding: 0;
    }
    body {
      overflow: hidden;
      background: #fafafa;
      font-family: Georgia, "Trebuchet MS", Arial, "Times New Roman";
    }
    dl {
      margin: 0 10px;
    }
    dt {
      font-size: 1.5em;
      font-weight: bold;
    }
    dd {
      font-size: 1.2em;
      margin: 0 0 2em 1em;
    }
    #highlight {
      width: 100%;
      background: #ddf;
      position: absolute;
      border-top: 2px solid #66f;
      border-bottom: 2px solid #66f;
      padding: 20px 0;
    }
    #links {
      position: absolute;
      bottom: -1px;
      right: .5em;
      border: 1px solid #66f;
      -moz-border-radius-topright: 4px;
      -moz-border-radius-topleft: 4px;
      -webkit-border-top-left-radius:4px;
      -webkit-border-top-right-radius:4px;
      padding: 0 4px;
      background: #006;
      font-size:.9em;
    }
    #links a {
      color: #fff;
      padding: 0 2px;
    }
  </style>

<meta name="__hash__" content="a95f2c1ea5ebe4f59deebcbffe3e48e7_48eb0bd81617315f2c62f85354fc9cc3" /></head>

<body>

<div id="highlight">
  <div id="links">
    <a href="/">首页</a> <a href="#">联系我们</a></div>
</div>
<dl id="codes">
  <dt>100 Continue </dt>
  <dd>The requestor should continue with the request. The server returns this
    code to indicate that it has received the first part of a request and is waiting
    for the rest. </dd>

  <dt>101 Switching Protocols </dt>
  <dd>This means the requester has asked the server to switch protocols and the
    server is acknowledging that it will do so</dd>
  <dt>102 Processing </dt>
  <dd>As a WebDAV request may contain many sub-requests involving file operations,
    it may take a long time to complete the request. This code indicates that the
    server has received and is processing the request, but no response is available
    yet. This prevents the client from timing out and assuming the request was lost.</dd>
  <dt>200 OK </dt>
  <dd>Standard response for successful HTTP requests. The actual response will
    depend on the request method used. In a GET request, the response will contain
    an entity corresponding to the requested resource. In a POST request the response
    will contain an entity describing or containing the result of the action.</dd>

  <dt>201 Created </dt>
  <dd>The request has been fulfilled and resulted in a new resource being created.</dd>
  <dt>202 Accepted </dt>
  <dd>The request has been accepted for processing, but the processing has not
    been completed. The request might or might not eventually be acted upon, as
    it might be disallowed when processing actually takes place.</dd>
  <dt>203 Non-Authoritative Information (since HTTP/1.1) </dt>
  <dd>The server successfully processed the request, but is returning information
    that may be from another source.</dd>

  <dt>204 No Content </dt>
  <dd>The server successfully processed the request, but is not returning any
    content.</dd>
  <dt>205 Reset Content </dt>
  <dd>The server successfully processed the request, but is not returning any
    content. Unlike a 204 response, this response requires that the requester reset
    the document view.</dd>
  <dt>206 Partial Content </dt>
  <dd>The server is delivering only part of the resource due to a range header
    sent by the client. This is used by tools like wget to enable resuming of interrupted
    downloads, or split a download into multiple simultaneous streams.</dd>

  <dt>207 Multi-Status (WebDAV) (RFC 2518 ) </dt>
  <dd>The message body that follows is an XML message and can contain a number
    of separate response codes, depending on how many sub-requests were made.</dd>
  <dt>300 Multiple Choices </dt>
  <dd>Indicates multiple options for the resource that the client may follow.
    It, for instance, could be used to present different format options for video,
    list files with different extensions, or word sense disambiguation.</dd>
  <dt>301 Moved Permanently </dt>
  <dd>This and all future requests should be directed to the given URI.</dd>

  <dt>302 Found </dt>
  <dd>This is the most popular redirect code, but also an example of industrial
    practice contradicting the standard. HTTP/1.0 specification (RFC 1945 ) required
    the client to perform a temporary redirect (the original describing phrase was
    &quot;Moved Temporarily&quot;), but popular browsers implemented it as a 303 See Other.
    Therefore, HTTP/1.1 added status codes 303 and 307 to distinguish between the
    two behaviors. However, the majority of Web applications and frameworks still
    use the 302 status code as if it were the 303.</dd>
  <dt>303 See Other (since HTTP/1.1) </dt>
  <dd>The response to the request can be found under another URI using a GET method.
    When received in response to a PUT, it should be assumed that the server has
    received the data and the redirect should be issued with a separate GET message.</dd>
  <dt>304 Not Modified </dt>

  <dd>Indicates the resource has not been modified since last requested. Typically,
    the HTTP client provides a header like the If-Modified</dd>
  <dd>Since header to provide a time against which to compare. Utilizing this
    saves bandwidth and reprocessing on both the server and client, as only the
    header data must be sent and received in comparison to the entirety of the page
    being re-processed by the server, then resent using more bandwidth of the server
    and client.</dd>
  <dt>305 Use Proxy </dt>
  <dd>The requestor can only access the requested page using a proxy. When the
    server returns this response, it also indicates the proxy that the requestor
    should use.</dd>
  <dt>306 Switch Proxy </dt>
  <dd>No longer used.</dd>

  <dt>307 Temporary Redirect (since HTTP/1.1) </dt>
  <dd>In this occasion, the request should be repeated with another URI, but future
    requests can still use the original URI. In contrast to 303, the request method
    should not be changed when reissuing the original request. For instance, a POST
    request must be repeated using another POST request.</dd>
  <dt>400 Bad Request </dt>
  <dd>The request contains bad syntax or cannot be fulfilled. The server didn&#39;t
    understand the syntax of the request.</dd>
  <dt>401 Unauthorized </dt>
  <dd>Similar to <i>403 Forbidden</i>, but specifically for use when authentication
    is possible but has failed or not yet been provided. The response must include
    a WWW-Authenticate header field containing a challenge applicable to the requested
    resource. The request requires authentication. The server might return this
    response for a page behind a login.</dd>

  <dt>402 Payment Required </dt>
  <dd>The original intention was that this code might be used as part of some
    form of digital cash or micropayment scheme, but that has not happened, and
    this code has never been used.</dd>
  <dt>403 Forbidden </dt>
  <dd>The request was a legal request, but the server is refusing to respond to
    it. Unlike a <i>401 Unauthorized</i> response, authenticating will make no difference.</dd>
  <dt id="fourohfour">404 Not Found </dt>

  <dd>The server can&#39;t find the requested page. For instance, the server often
    returns this code if the request is for a page that doesn&#39;t exist on the server.
    The requested resource could not be found but may be available again in the
    future. Subsequent requests by the client are permissible. No indication is
    given of whether the condition is temporary or permanent.</dd>
  <dt>405 Method Not Allowed </dt>
  <dd>A request was made of a resource using a request method not supported by
    that resource; for example, using GET on a form which requires data to be presented
    via POST, or using PUT on a read-only resource.</dd>
  <dt>406 Not Acceptable </dt>
  <dd>The requested resource is only capable of generating content not acceptable
    according to the Accept headers sent in the request.</dd>

  <dt>407 Proxy Authentication Required</dt>
  <dd>This code is similar to 401 (Unauthorized), but indicates that the client
    must first authenticate itself with the proxy. The proxy MUST return a Proxy-Authenticate
    header field containing a challenge applicable to the proxy
    for the requested resource.</dd>
  <dt>408 Request Timeout </dt>
  <dd>The server timed out waiting for the request.</dd>
  <dt>409 Conflict </dt>
  <dd>Indicates that the request could not be processed because of conflict in
    the request, such as an edit conflict.</dd>

  <dt>410 Gone </dt>
  <dd>Indicates that the resource requested is no longer available and will not
    be available again. This should be used when a resource has been intentionally
    removed; however, it is not necessary to return this code and a <i>404 Not Found</i>
    can be issued instead. Upon receiving a 410 status code, the client should not
    request the resource again in the future. Clients such as search engines should
    remove the resource from their indexes.</dd>
  <dt>411 Length Required </dt>
  <dd>The request did not specify the length of its content, which is required
    by the requested resource.</dd>
  <dt>412 Precondition Failed </dt>

  <dd>The server does not meet one of the preconditions that the requester put
    on the request.</dd>
  <dt>413 Request Entity Too Large </dt>
  <dd>The request is larger than the server is willing or able to process.</dd>
  <dt>414 Request URI Too Long </dt>
  <dd>The URI provided was too long for the server to process.</dd>
  <dt>415 Unsupported Media Type </dt>

  <dd>The request did not specify any media types that the server or resource
    supports. For example the client specified that an image resource should be
    served as image/svg+xml, but the server cannot find a matching version of the
    image.</dd>
  <dt>416 Requested Range Not Satisfiable </dt>
  <dd>The client has asked for a portion of the file, but the server cannot supply
    that portion (for example, if the client asked for a part of the file that lies
    beyond the end of the file).</dd>
  <dt>417 Expectation Failed </dt>
  <dd>The server cannot meet the requirements of the Expect request-header field.</dd>
  <dt>418 I&#39;m a teapot </dt>

  <dd>The HTCPCP server is a teapot. The responding entity MAY be short and stout.
    This code was defined as one of the traditional IETF April Fools&#39; jokes, in
    RFC 2324, <i>Hyper Text Coffee Pot Control Protocol</i>, and is not expected
    to be implemented by actual HTTP servers.</dd>
  <dt>422 Unprocessable Entity (WebDAV) (RFC 4918 ) </dt>
  <dd>The request was well-formed but was unable to be followed due to semantic
    errors.</dd>
  <dt>423 Locked (WebDAV) (RFC 4918 ) </dt>
  <dd>The resource that is being accessed is locked</dd>

  <dt>424 Failed Dependency (WebDAV) (RFC 4918 ) </dt>
  <dd>The request failed due to failure of a previous request (e.g. a PROPPATCH).</dd>
  <dt>425 Unordered Collection (RFC 3648 ) </dt>
  <dd>Defined in drafts of WebDav Advanced Collections, but not present in &quot;Web
    Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol&quot;.</dd>
  <dt>426 Upgrade Required (RFC 2817 ) </dt>

  <dd>The client should switch to TLS/1.0.</dd>
  <dt>449 Retry With </dt>
  <dd>A Microsoft extension. The request should be retried after doing the appropriate
    action.</dd>
  <dt>450 Blocked by Windows Parental Controls </dt>
  <dd>A Microsoft extension. This error is given when Windows Parental Controls
    are turned on and are blocking access to the given webpage.</dd>
  <dt id="fiveohoh">500 Internal Server Error </dt>

  <dd>A generic error message, given when no more specific message is suitable.</dd>
  <dt>501 Not Implemented </dt>
  <dd>The server either does not recognize the request method, or it lacks the
    ability to fulfil the request.</dd>
  <dt>502 Bad Gateway </dt>
  <dd>The server was acting as a gateway or proxy and received an invalid response
    from the downstream server.</dd>
  <dt>503 Service Unavailable </dt>

  <dd>The server is currently unavailable (because it is overloaded or down for
    maintenance). Generally, this is a temporary state.</dd>
  <dt>504 Gateway Timeout </dt>
  <dd>The server was acting as a gateway or proxy and did not receive a timely
    request from the downstream server.</dd>
  <dt>505 HTTP Version Not Supported </dt>
  <dd>The server does not support the HTTP protocol version used in the request.</dd>
  <dt>506 Variant Also Negotiates (RFC 2295 ) </dt>

  <dd>Transparent content negotiation for the request, results in a circular reference.</dd>
  <dt>507 Insufficient Storage (WebDAV) (RFC 4918 )</dt>
  <dt>509 Bandwidth Limit Exceeded (Apache bw/limited extension) </dt>
  <dd>This status code, while used by many servers, is not specified in any RFCs.</dd>
  <dt>510 Not Extended (RFC 2774 ) </dt>
  <dd>Further extensions to the request are required for the server to fulfill
    it.</dd>

</dl>
</body>
</html>