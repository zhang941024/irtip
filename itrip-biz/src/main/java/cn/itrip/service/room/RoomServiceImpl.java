package cn.itrip.service.room;

import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import cn.itrip.dao.hotelroom.ItripHotelRoomMapper;
import cn.itrip.dao.image.ItripImageMapper;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class RoomServiceImpl implements RoomService {
    @Resource
    private ItripLabelDicMapper labelDicMapper;
    @Resource
    private ItripHotelRoomMapper roomMapper;
    @Override
    public List<ItripLabelDicVO> queryRoomBed() throws Exception {
        Map map = new HashMap();
        map.put("parentId",1);
        return labelDicMapper.getItripLabelDicListByMap(map);
    }

    @Override
    public List<ItripHotelRoomVO> queryHotelRoomByHotel(Map map) throws Exception {
        return roomMapper.getItripHotelRoomListByMap(map);
    }

    @Override
    public ItripHotelRoom queryRoomById(Long id) throws Exception {
        return roomMapper.getItripHotelRoomById(id);
    }
}
