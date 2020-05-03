package cn.itrip.auth.service;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public void sendSms(String to, String templateId, String[] datas) throws Exception {
        HashMap<String,Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();

        restAPI.init("app.cloopen.com","8883");
        //初始化服务器地址和端口，生成环境配置成app.cloopen.com，端口是8883
        restAPI.setAccount("8a216da870e2267e0171386f3a762d76","cacc1cf022384b27901be94b14d5f78b");
        //初始化主账号名称和主账号令牌
        restAPI.setAppId("8a216da870e2267e0171386f3ae92d7d");
        //请使用管理控制台中已创建应用的APPID
        result = restAPI.sendTemplateSMS(to,templateId,datas);
        System.out.println("SDKTestGetSubAccounts result="+result);
        if("000000".equals(result.get("statusCode"))){
            System.out.println("短信发送成功");
        }else {
            throw new Exception("短信发送失败");
        }
    }
}
