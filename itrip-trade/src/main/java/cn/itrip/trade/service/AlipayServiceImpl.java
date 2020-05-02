package cn.itrip.trade.service;

import cn.itrip.trade.bean.AlipayBean;
import cn.itrip.trade.bean.AlipayConfig;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Service
public class AlipayServiceImpl implements PayService {
    @Resource
    private AlipayConfig alipayConfig;
    @Override
    public String alipay(AlipayBean alipayBean) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getUrl(),//支付宝网关
                alipayConfig.getAppID(),//appid
                alipayConfig.getRsaPrivateKey(),//商户私钥
                "json",
                alipayConfig.getCharset(),//字符编码格式
                alipayConfig.getAlipayPublicKey(),//支付宝公钥
               alipayConfig.getSignType()//签名方式
        );

        //2、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
        //封装参数
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        //3、请求支付宝进行付款，并获取支付结果
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        System.out.println(result);//返回付款信息
        return result;
    }
}
