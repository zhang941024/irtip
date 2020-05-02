package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import cn.itrip.service.areadic.AreaDicService;
import cn.itrip.service.feature.FeatureService;
import cn.itrip.service.hotel.HotelService;
import cn.itrip.service.img.ItripImgService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/hotel")
public class HotelController {
    @Resource
    private AreaDicService areaDicService;
    @Resource
    private ItripImgService imgService;
    @Resource
    private HotelService hotelService;
    @Resource
    private FeatureService featureService;
    @RequestMapping(value="/querytradearea/{cityId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryTradeArea(@PathVariable Long cityId){
        if(EmptyUtils.isEmpty(cityId)){
            return DtoUtil.returnFail("cityId不能为空", ErrorCode.BIZ_CITYID_NOTNULL);
        }
        Map map = new HashMap();
        map.put("isTradingArea",1);
        map.put("parent",cityId);
        try {
            List<ItripAreaDic> areaDicList = areaDicService.getAreaDicList(map);
            List<ItripAreaDicVO> voList = new ArrayList<>();
            for (ItripAreaDic dic : areaDicList){
                ItripAreaDicVO vo = new ItripAreaDicVO();
                BeanUtils.copyProperties(dic,vo);
                voList.add(vo);
            }
            return DtoUtil.returnDataSuccess(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }
    @RequestMapping(value="/getimg/{targetId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getImg(@PathVariable String targetId){
        if(EmptyUtils.isEmpty(targetId)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        Map map = new HashMap();
        map.put("type",0);
        map.put("targetId",targetId);
        try {
            List<ItripImageVO> imageList = imgService.getImgByTargetId(map);
            if(imageList.size() == 0){
                return DtoUtil.returnFail("获取失败",ErrorCode.BIZ_GETIMG_FAILUER);
            }
            return DtoUtil.returnDataSuccess(imageList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }

    @RequestMapping(value="/getvideodesc/{hotelId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getVideoDesc(@PathVariable String hotelId){
        if(EmptyUtils.isEmpty(hotelId)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            HotelVideoDescVO vo = hotelService.queryVideoDesc(Long.parseLong(hotelId));
            return DtoUtil.returnDataSuccess(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_HOTELVIDEO_FAILURE);
        }

    }
    @RequestMapping(value="/queryhotelfacilities/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryHotelFacilities(@PathVariable Long id){
        if(EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            ItripSearchFacilitiesHotelVO vo = hotelService.queryHotelFacilities(id);
            return DtoUtil.returnDataSuccess(vo.getFacilities());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }
    @RequestMapping(value="/queryhotelfeature",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryHotelFeature(){
        try {
            Map map = new HashMap();
            map.put("parentId",16);
            List<ItripLabelDic> dicList = hotelService.queryHotelFeature(map);
            List<ItripLabelDicVO> voList = new ArrayList<>();
            for(ItripLabelDic dic : dicList){
                ItripLabelDicVO vo = new ItripLabelDicVO();
                BeanUtils.copyProperties(dic,vo);
                voList.add(vo);
            }
            return DtoUtil.returnDataSuccess(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }

    @RequestMapping(value = "/queryhotcity/{type}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryHotCity(@PathVariable Integer type){
        Map map = new HashMap();
        map.put("isHot",1);
        map.put("isChina",type);
        try {
            List<ItripAreaDic> areaDicList = areaDicService.getAreaDicList(map);
            List<ItripAreaDicVO> voList = new ArrayList<>();
            for (ItripAreaDic dic : areaDicList){
                ItripAreaDicVO vo = new ItripAreaDicVO();
                BeanUtils.copyProperties(dic,vo);
                voList.add(vo);
            }
            return DtoUtil.returnDataSuccess(voList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }
    @RequestMapping(value="/queryhoteldetails/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryHotelDetails(@PathVariable Long id){
        if(EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            List<ItripLabelDic> dicList = featureService.queryDetailsHotel(id);
            List<ItripSearchDetailsHotelVO> list = new ArrayList<>();
            for(ItripLabelDic dic : dicList){
                ItripSearchDetailsHotelVO vo = new ItripSearchDetailsHotelVO();
                BeanUtils.copyProperties(dic,vo);
                list.add(vo);
            }
            return DtoUtil.returnDataSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }

    }
    @RequestMapping(value="/queryhotelpolicy/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryHotelPolicy(@PathVariable Long id){
        if(EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空",ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            ItripSearchPolicyHotelVO policy = hotelService.queryHotelPolicy(id);
            return DtoUtil.returnDataSuccess(policy);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.BIZ_UNKNOWN);
        }
    }


}
