package cn.itrip.service.hotel;

import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripHotelFeature;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import cn.itrip.dao.areadic.ItripAreaDicMapper;
import cn.itrip.dao.hotel.ItripHotelMapper;
import cn.itrip.dao.hotelfeature.ItripHotelFeatureMapper;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelServiceImpl implements HotelService {
    @Resource
    private ItripHotelMapper hotelMapper;
    @Resource
    private ItripLabelDicMapper labelDicMapper;
    @Resource
    private ItripAreaDicMapper areaDicMapper;
    @Resource
    private ItripHotelFeatureMapper featureMapper;
    @Override
    public ItripSearchFacilitiesHotelVO queryHotelFacilities(Long id) throws Exception {
        return hotelMapper.getItripHotelFacilitiesById(id);
    }

    @Override
    public List<ItripLabelDic> queryHotelFeature(Map map) throws Exception {
        return labelDicMapper.getItripLabelDicListByMap(map);
    }

    @Override
    public ItripSearchPolicyHotelVO queryHotelPolicy(Long id) throws Exception {
        return hotelMapper.queryHotelPolicy(id);
    }

    @Override
    public ItripHotel queryHotelById(Long id) throws Exception {
        return hotelMapper.getItripHotelById(id);
    }

    @Override
    public HotelVideoDescVO queryVideoDesc(Long id) throws Exception {
        HotelVideoDescVO vo = new HotelVideoDescVO();

        ItripHotel itripHotel = hotelMapper.getItripHotelById(id);
        vo.setHotelName(itripHotel.getHotelName());

        List<ItripAreaDic> hotelAreaList = hotelMapper.getHotelAreaByHotelId(id);
        List<String> tradingAreaNameList = new ArrayList<>();
        for(ItripAreaDic dic : hotelAreaList){
            tradingAreaNameList.add(dic.getName());
        }
        vo.setTradingAreaNameList(tradingAreaNameList);

        List<ItripLabelDic> labelDicList = hotelMapper.getHotelFeatureByHotelId(id);
        List<String> hotelFeatureList = new ArrayList<>();
        for(ItripLabelDic dic : labelDicList){
            hotelFeatureList.add(dic.getName());
        }
        vo.setHotelFeatureList(hotelFeatureList);
        return vo;
    }
}
