package cn.itrip.service.feature;

import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.dao.hotel.ItripHotelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {
    @Resource
    private ItripHotelMapper hotelMapper;


    @Override
    public List<ItripLabelDic> queryDetailsHotel(Long id) throws Exception {
       return hotelMapper.getHotelFeatureByHotelId(id);
    }
}
