package cn.itrip.service.order;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import cn.itrip.beans.vo.order.ItripPersonalOrderRoomVO;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.DateUtil;
import cn.itrip.common.Page;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.hoteltempstore.ItripHotelTempStoreMapper;
import org.reflections.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Resource
    private ItripHotelTempStoreMapper storeMapper;
    @Resource
    private ItripHotelOrderMapper orderMapper;
    @Override
    public boolean validateroomstore(Map map) throws Exception {
        List<Date> dateList = DateUtil.getBetweenDates((Date) map.get("startTime"), (Date) map.get("endTime"));
        for(Date d : dateList){
            map.put("time",d);
            ItripHotelTempStore tempStore = storeMapper.queryByTime(map);
            if(tempStore == null){
                ItripHotelTempStore store = new ItripHotelTempStore();
                store.setHotelId((Long)map.get("hotelId"));
                store.setRoomId((Long)map.get("roomId"));
                store.setRecordDate(d);
                store.setStore(20);
                store.setCreationDate(new Date());
                storeMapper.insertItripHotelTempStore(store);
            }
        }
        List<StoreVO> list = storeMapper.queryRoomStore(map);
        for (StoreVO vo : list){
           Integer count =  map.get("count") == null ? 1 : (Integer)map.get("count");
            if(vo.getStore() < count ){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItripHotelOrder getItripHotelOrderById(Long id) throws Exception {
        return orderMapper.getItripHotelOrderById(id);
    }

    @Override
    public ItripPersonalOrderRoomVO getPersonalOrderRoomInfo(Long id) throws Exception {
        return orderMapper.getItripHotelOrderRoomInfoById(id);
    }

    @Override
    public Page<ItripHotelOrder> getPersonalOrderList(Map map) throws Exception {
        Integer beginPos = ((Integer)map.get("pageNo")-1)*(Integer)map.get("pageSize");
        map.put("beginPos",beginPos);
        List list = orderMapper.getItripHotelOrderListByMap(map);
        Integer total = orderMapper.getItripHotelOrderCountByMap(map);
        Page<ItripHotelOrder> page = new Page((Integer)map.get("pageNo"),(Integer)map.get("pageSize"),total);
        page.setRows(list);
        return page;
    }

    @Override
    public List<StoreVO> queryRoomStore(Map map) throws Exception {
        return storeMapper.queryRoomStore(map);
    }


    @Override
    public Map insertOrder(ItripHotelOrder order) throws Exception {
        Map map = new HashMap();
        orderMapper.insertItripHotelOrder(order);
        map.put("id",order.getId());
        map.put("orderNo",order.getOrderNo());
        return map;
    }

    @Override
    public ItripHotelOrder queryOrderById(Long id) throws Exception {
        return orderMapper.getItripHotelOrderById(id);
    }

    @Override
    public int modifyOrder(ItripHotelOrder order) throws Exception {
        return orderMapper.updateItripHotelOrder(order);
    }

    @Override
    public boolean flushOrderStatus(Integer type) throws Exception {
        Integer i = 0;
        if(type == 1){
            i = orderMapper.flushCancelOrderStatus();
        }else if(type == 2){
            i = orderMapper.flushSuccessOrderStatus();
        }
        return i == 1;
    }
}
