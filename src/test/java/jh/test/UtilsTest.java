package jh.test;

import hf.base.utils.EpaySignUtil;
import org.junit.Test;

public class UtilsTest {

    private String prikeyValue = "MIIEpAIBAAKCAQEAtLnOdyl9oKIX/LGyMswd0hr3blb+aS/OawATrroereTXTPrK\n" +
            "6bNJv6tr6KCbCJemuYQgNcgcNbrJe6BAdb5OhxQyMGCaVEw2Y76MxzfAuS2r7lLX\n" +
            "Ni+DmNFtM1ALSbipu6rua9XbDCA03ZJzslavvfeyuaeu7QTOEQGD35IVXIM9bZR5\n" +
            "ONSuJEGlEhCXzyII7ds34R3TE4k/pVKbJ4cqMxTuwb5izL/xsOrV/Ht4eDLan7nN\n" +
            "8zPdVzDCO4nj7t9Ijl7ZnxufdePKusaeTTQjmwAj9MsQptK4Bv81bDoI7SFw03EV\n" +
            "4ZIO7qkf+2pYcrVLUsiebc2yt8mw9/tazYpe/wIDAQABAoIBAQCI2L1PG/rFnJff\n" +
            "P0q5DjhydPrw8SyZx5pdCWTeBI7gjAy/fJQTnC/2073VG2/pdLPJfBPLxagewz53\n" +
            "vyOwRJc/z3olibCyrjbtFkeRPlVPoxayUsYlgJr8J3CxzyWNACh+M3Nv8jJ4nxaI\n" +
            "xLGY0+0lQp9x11gsn1vOIsCRlRNZxbtB6TGH+vzH5vanYkxTByTOYyl4HYpVEQDP\n" +
            "vuVOD2orxSJA1xGYtThNwLxrF39MmDyTP8N15CwRouoLU0omC7+Qq3C+y4Ejox6N\n" +
            "xJBK4DapzAIKy4K3XgMXR1uPqfDlD5f61waeh5UvY8029Kgn8GjXdymy2E86xASr\n" +
            "ohWP1LkZAoGBAO7KFI8awRCCeF1n+jCloNmsfj0FYezS9NLpmYg/CAaFFetG5eUt\n" +
            "RKyoYTt76rjNdGjCtarcjs/xOtZXWBEgxXiKfv6HwVaWAPLTA86FvEOfV9BZuP4X\n" +
            "pCNQv3sno6Z07mPeKRzhemXyzDlPyQxsb4csvePGAaDJ1ek6k0b/MNg9AoGBAMHA\n" +
            "ZCNKVJHuwVZ9Gp5/aj/6IIQBftwHnvbuRPDwPcRrGMALrJp9/gRAKyrrrk+mk/43\n" +
            "7mETuhBa7nK6XcHwiR5TucxLfYIv1t0poGR92zaKZ8QzSQPbkBvWTtFOeAnRQv2i\n" +
            "dAPjMpMPPoFpaJuIRhT/CXrY+uUkMIA8sCyZYkvrAoGAK06/N80UYtgm2Fn5SEVh\n" +
            "zNi59Hs7bWY9PNtdGxbDb9tHRGqRW2VAZUgMimtJAMdSa4WUyS5DQHdxwloJAOI+\n" +
            "rkQAEE2yxO9jsKaQtC4RHPqTRJhhMsQ64qTMdZuU1KW0bqxmLHTAbCkC3QoZXoV0\n" +
            "HMICloLc3Lp+b1ROTbwOsckCgYAtrckuFMkpequ0U1xiP9Hx8WuXE68v+s/8kaJJ\n" +
            "V6qIU2OLa3UvG0M3B1XmEZiQCMrdZZxa4Ma+MmIDRHL0VVxOfRjR1H5rohG7JKQ+\n" +
            "7Pkwu6LJO/ob4bjxBy6f5CsizWZI2/MUM41p5G8tHYffG1rCenpmrx8/xK92nFhA\n" +
            "u4zULwKBgQDX1aPGMlmExas3UEChC9/LVmcRRvOKwcitv6GNe+2O0W6kGH19bHs9\n" +
            "uHzCyAq0uqtrflFX/Eksq+CWCTcug8sH/EPLTIX7RZZtHS/6cpPcNizFswCLBbaR\n" +
            "ee+PJ/NGVyv1yQTb2y0+AQx4VCIrdICrZRd7xINnwPy5aKXujR+qHQ==";

    @Test
    public void testEncrypt() {
        String url = "test123452211";
        String sign = EpaySignUtil.sign(prikeyValue,url);
        System.out.println(sign);
    }

    private String content = "<html>\n" +
            "<script type='text/html' style='display:block'>\n" +
            "    <input type=\"text\" />\n" +
            "</scipt>\n" +
            "\n" +
            "<head>\n" +
            "    <title>订单支付</title>\n" +
            "    <script src=\"/pay/js/jquery-2.2.3.min.js\"></script>\n" +
            "</head>\n" +
            "<body>\u2028\n" +
            "<form id=\"form\" action=\"https://newpay.ips.com.cn/psfp-entry/gateway/payment.do\" method=\"post\">\n" +
            "    <input name=\"pGateWayReq\" type=\"hidden\" value=\"<Ips><GateWayReq><head><Version>v1.0.0</Version><MerCode>203951</MerCode><MerName>海南侨乐邦网络科技有限公司</MerName><Account>2039510017</Account><MsgId>msg20180209113746</MsgId><ReqDate>20180209113746</ReqDate><Signature>0b1f992ba1ac78579b586f051b4576d5</Signature></head><body><MerBillNo>20180209113746036690</MerBillNo><Lang>GB</Lang><Amount>11.00</Amount><Date>20180209</Date><CurrencyType>156</CurrencyType><GatewayType>02</GatewayType><Merchanturl>http://pay1.hlqlb.cn:8692/pay/payment/result</Merchanturl><FailUrl><![CDATA[]]></FailUrl><Attach><![CDATA[]]></Attach><OrderEncodeType>5</OrderEncodeType><RetEncodeType>17</RetEncodeType><RetType>1</RetType><ServerUrl><![CDATA[http://47.97.175.195:8682/posp/bankPay/hxPayNotify]]></ServerUrl><BillEXP>24</BillEXP><GoodsName>湖南慧付 收款</GoodsName><IsCredit></IsCredit><BankCode></BankCode><ProductType></ProductType></body></GateWayReq></Ips>\" />\n" +
            "</form>\u2028\n" +
            "<script type=\"text/javascript\">\n" +
            "    $(document).ready(function(){\n" +
            "        $(\"#form\").submit();\n" +
            "    });\n" +
            "</script>\n" +
            "\u2028\n" +
            "</body>\n" +
            "</html>";

    @Test
    public void testSubstring() {
        String result = content.substring(content.indexOf("<form"),content.indexOf("</form>")+7);
        System.out.println(result);
    }
}
