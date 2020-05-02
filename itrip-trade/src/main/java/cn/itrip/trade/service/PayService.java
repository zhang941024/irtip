package cn.itrip.trade.service;

import cn.itrip.trade.bean.AlipayBean;

public interface PayService {
    String alipay(AlipayBean alipayBean) throws Exception;
}
