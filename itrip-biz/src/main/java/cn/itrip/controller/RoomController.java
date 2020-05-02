package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import cn.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import cn.itrip.common.DateUtil;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import cn.itrip.service.img.ItripImgService;
import cn.itrip.service.room.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/api/hotelroom")
public class RoomController {
    @Resource
    private ItripImgService imgService;
    @Resource
    private RoomService roomService;
    @RequestMapping(value="/getimg/{targetId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getImg(@PathVariable String targetId){
        if(EmptyUtils.isEmpty(targetId)){
            return DtoUtil.returnFail("房间id不能为空", ErrorCode.BIZ_ROOMID_NOTNULL);
        }
        Map map = new HashMap();
        map.put("type",1);
        map.put("targetId",targetId);
        try {
            List<ItripImageVO> imgList = imgService.getImgByTargetId(map);
            return DtoUtil.returnSuccess("获取酒店房型图片成功",imgList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取酒店房型图片失败",ErrorCode.BIZ_ROOIMG_FAILURE);
        }
    }
    @RequestMapping(value="/queryhotelroombed",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryRoomBed(){
        try {
            List<ItripLabelDicVO> voList = roomService.queryRoomBed();
            return DtoUtil.returnDataSuccess(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取房间床型失败",ErrorCode.BIZ_ROOBED_FAILURE);
        }
    }
    @RequestMapping(value="/queryhotelroombyhotel",method = RequestMethod.POST)
    @ResponseBody
    public Dto queryHotelRoomByHotel(@RequestBody SearchHotelRoomVO vo){
        if(EmptyUtils.isEmpty(vo.getHotelId())){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTEL_NOTNULL);
        }
        if(EmptyUtils.isEmpty(vo.getStartDate()) || EmptyUtils.isEmpty(vo.getEndDate())){
            return DtoUtil.returnFail("入住时间或退房时间不能为空",ErrorCode.BIZ_HOTEL_NOTNULL);
        }
        System.out.println("----------------"+vo.getStartDate());
        if(vo.getEndDate().getTime() < vo.getStartDate().getTime()){
            return DtoUtil.returnFail("入住时间不能大于退房时间",ErrorCode.BIZ_HOTEL_NOTNULL);
        }

        Map param = new HashMap();
        List<Date> dates = DateUtil.getBetweenDates(vo.getStartDate(), vo.getEndDate());
        param.put("timesList", dates);
        param.put("hotelId", vo.getHotelId());
        param.put("isBook", vo.getIsBook());
        param.put("isHavingBreakfast", vo.getIsHavingBreakfast());
        param.put("isTimelyResponse", vo.getIsTimelyResponse());
        param.put("roomBedTypeId", vo.getRoomBedTypeId());
        param.put("isCancel", vo.getIsCancel());
        param.put("payType", vo.getPayType());
        try {
            List<ItripHotelRoomVO> roomVOList = roomService.queryHotelRoomByHotel(param);
            List<List<ItripHotelRoomVO>> roomList = new ArrayList<>();
            for(ItripHotelRoomVO roomVO : roomVOList){
                List<ItripHotelRoomVO> tempList = new ArrayList<>();
                tempList.add(roomVO);
                roomList.add(tempList);
            }
            return DtoUtil.returnSuccess("获取成功",roomList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常",ErrorCode.BIZ_ROOM_UNKNOWN);
        }

    }
}
