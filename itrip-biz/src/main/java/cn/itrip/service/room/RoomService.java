package cn.itrip.service.room;

import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;

import java.util.List;
import java.util.Map;

public interface RoomService {
   List<ItripLabelDicVO> queryRoomBed() throws Exception;

   List<ItripHotelRoomVO> queryHotelRoomByHotel(Map map) throws Exception;
   ItripHotelRoom queryRoomById(Long id) throws Exception;
}
