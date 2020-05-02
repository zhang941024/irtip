package cn.test;

import cn.itrip.trade.bean.AlipayBean;
import cn.itrip.trade.bean.AlipayConfig;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class Test2 {
    @Resource
    private AlipayConfig alipayConfig;
    @Test
    public void test01(){
        AlipayBean bean = new AlipayBean();
        bean.setSubject("北京大酒店");
        System.out.println(alipayConfig.getNotifyUrl());
    }
}
