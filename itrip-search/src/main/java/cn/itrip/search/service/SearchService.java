package cn.itrip.search.service;

import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;
import cn.itrip.search.bean.ItripHotelVO;

import java.util.List;

public interface SearchService {
    Page<ItripHotelVO> queryHotelPage(SearchHotelVO vo, Integer pageNo, Integer pageSize) throws Exception;
    List<ItripHotelVO> queryHotCity(Integer cityId, Integer count) throws Exception;
}
