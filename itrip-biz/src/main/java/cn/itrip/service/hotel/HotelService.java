package cn.itrip.service.hotel;

import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;

import java.util.List;
import java.util.Map;

public interface HotelService {
    ItripSearchFacilitiesHotelVO queryHotelFacilities(Long id) throws Exception;
    List<ItripLabelDic> queryHotelFeature(Map map) throws Exception;
    ItripSearchPolicyHotelVO queryHotelPolicy(Long id) throws Exception;
    ItripHotel queryHotelById(Long id) throws Exception;
    HotelVideoDescVO queryVideoDesc(Long id) throws Exception;
}
