package cn.itrip.trade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.common.EmptyUtils;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Resource
    private ItripHotelOrderMapper orderMapper;
    @Override
    public ItripHotelOrder loadItripHotelOrder(String orderNo) throws Exception {
        Map map = new HashMap();
        map.put("orderNo",orderNo);
        List<ItripHotelOrder> list = orderMapper.getItripHotelOrderListByMap(map);
        if(list.size() == 1){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void paySuccess(String orderNo, int payType, String tradeNo) throws Exception {
        ItripHotelOrder order = loadItripHotelOrder(orderNo);
        order.setOrderType(2);
        order.setPayType(payType);
        order.setTradeNo(tradeNo);
        orderMapper.updateItripHotelOrder(order);

    }

    @Override
    public void payFailed(String orderNo, int payType, String tradeNo) throws Exception {
        ItripHotelOrder order = loadItripHotelOrder(orderNo);
        order.setOrderType(1);
        order.setPayType(payType);
        order.setTradeNo(tradeNo);
        orderMapper.updateItripHotelOrder(order);

    }

    @Override
    public boolean processed(String orderNo) throws Exception {
        ItripHotelOrder order = loadItripHotelOrder(orderNo);
        return order.getOrderType().equals(2) && EmptyUtils.isNotEmpty(order.getTradeNo());
    }
}
