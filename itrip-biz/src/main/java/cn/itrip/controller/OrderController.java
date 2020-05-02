package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.*;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.*;
import cn.itrip.service.hotel.HotelService;
import cn.itrip.service.order.OrderService;
import cn.itrip.service.room.RoomService;
import com.alibaba.fastjson.JSON;
import org.reflections.Store;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/hotelorder")
public class OrderController {
    @Resource
    private ValidationToken validationToken;
    @Resource
    private OrderService orderService;
    @Resource
    private HotelService hotelService;
    @Resource
    private RoomService roomService;
    @RequestMapping(value = "/validateroomstore", method = RequestMethod.POST)
    @ResponseBody
    public Dto validateroomstore(@RequestBody ValidateRoomStoreVO vo, HttpServletRequest request) {
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if (vo.getHotelId() == null) {
            return DtoUtil.returnFail("hotelId不能为空", "100515");
        }
        if (vo.getRoomId() == null) {
            return DtoUtil.returnFail("roomId不能为空", "100516");
        }
        Map map = new HashMap();
        map.put("hotelId", vo.getHotelId());
        map.put("roomId", vo.getRoomId());
        map.put("startTime",vo.getCheckInDate());
        map.put("endTime",vo.getCheckOutDate());
        map.put("count",vo.getCount());
        try {
            boolean flag = orderService.validateroomstore(map);
            map.clear();
            map.put("flag", flag);
            return DtoUtil.returnDataSuccess(map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常", "100517");
        }
    }

    @RequestMapping(value = "/getpersonalorderinfo/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public Dto getpersonalorderinfo(@PathVariable String orderId, HttpServletRequest request) {
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if (EmptyUtils.isEmpty(orderId)) {
            return DtoUtil.returnFail("请传递参数：orderId", "100525");
        }
        //订单状态（0：待支付 1:已取消 2:支付成功 3:已消费 4:已点评）
        //{"1":"订单提交","2":"订单支付","3":"支付成功","4":"入住","5":"订单点评","6":"订单完成"}
        //{"1":"订单提交","2":"订单支付","3":"订单取消"}
        try {
            ItripHotelOrder itripHotelOrder = orderService.getItripHotelOrderById(Long.parseLong(orderId));
            if (itripHotelOrder == null) {
                return DtoUtil.returnFail("没有相关订单信息", "100526");
            }
            ItripPersonalHotelOrderVO vo = new ItripPersonalHotelOrderVO();
            BeanUtils.copyProperties(itripHotelOrder,vo);

            Object ok = JSON.parse("{\"1\":\"订单提交\",\"2\":\"订单支付\",\"3\":\"支付成功\",\"4\":\"入住\",\"5\":\"订单点评\",\"6\":\"订单完成\"}");
            Object cancle = JSON.parse("{\"1\":\"订单提交\",\"2\":\"订单支付\",\"3\":\"订单取消\"}");
            if(itripHotelOrder.getOrderStatus() == 0){
                vo.setOrderProcess(ok);
                vo.setProcessNode("2");
            }else if(itripHotelOrder.getOrderStatus() == 1){
                vo.setOrderProcess(cancle);
                vo.setProcessNode("3");
            }else if(itripHotelOrder.getOrderStatus() == 2){
                vo.setOrderProcess(ok);
                vo.setProcessNode("3");
            }else if(itripHotelOrder.getOrderStatus() == 3){
                vo.setOrderProcess(ok);
                vo.setProcessNode("5");
            }else if(itripHotelOrder.getOrderStatus() == 4){
                vo.setOrderProcess(ok);
                vo.setProcessNode("6");
            }else{
                vo.setOrderProcess(null);
                vo.setProcessNode(null);
            }
            vo.setRoomPayType(itripHotelOrder.getPayType());

            return DtoUtil.returnDataSuccess(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单信息错误", "100527");
        }
    }
    @RequestMapping(value="/getpersonalorderroominfo/{orderId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto getPersonalOrderRoomInfo(@PathVariable String orderId,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(orderId)){
            return DtoUtil.returnFail("请传递参数：orderId","100529");
        }
        try {
            ItripPersonalOrderRoomVO roomInfo = orderService.getPersonalOrderRoomInfo(Long.parseLong(orderId));
            if(roomInfo == null){
                return DtoUtil.returnFail("没有相关订单房型信息","100530");
            }
            return DtoUtil.returnSuccess("获取成功",roomInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单房型信息错误","100531");
        }
    }
    @RequestMapping(value="getpersonalorderlist",method = RequestMethod.POST)
    @ResponseBody
    public Dto getPersonalOrderList(@RequestBody ItripSearchOrderVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(vo.getOrderType())){
            return DtoUtil.returnFail("请传递参数：orderType","100501");
        }
        if(EmptyUtils.isEmpty(vo.getOrderStatus())){
            return DtoUtil.returnFail("请传递参数：orderStatus","100502");
        }
        Map map = new HashMap();
        map.put("orderNo",vo.getOrderNo());
        map.put("orderType",vo.getOrderType() == -1 ? null : vo.getOrderType());
        map.put("orderStatus",vo.getOrderStatus() == -1 ? null : vo.getOrderStatus());
        map.put("userId",user.getId());
        map.put("linkUserName",vo.getLinkUserName());
        map.put("startDate",vo.getStartDate());
        map.put("endDate",vo.getEndDate());
        Integer pageNo = vo.getPageNo() == null ? 1 : vo.getPageNo();
        Integer pageSize = vo.getPageSize() == null ? 5 : vo.getPageSize();
        map.put("pageNo",pageNo);
        map.put("pageSize",pageSize);
        try {
            Page<ItripHotelOrder> page = orderService.getPersonalOrderList(map);
            return DtoUtil.returnSuccess("获取成功",page);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单列表错误","100503");
        }
    }

    @RequestMapping(value="/getpreorderinfo",method = RequestMethod.POST)
    @ResponseBody
    public Dto getPreOrderinfo(@RequestBody ValidateRoomStoreVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(vo.getHotelId())){
            return DtoUtil.returnFail("hotelId不能为空","100510");
        }
        if(EmptyUtils.isEmpty(vo.getRoomId())){
            return DtoUtil.returnFail("roomId不能为空","100511");
        }
        try {
            ItripHotel hotel = hotelService.queryHotelById(vo.getHotelId());
            ItripHotelRoom room = roomService.queryRoomById(vo.getRoomId());

            Map map = new HashMap();
            map.put("hotelId", vo.getHotelId());
            map.put("roomId", vo.getRoomId());
            map.put("startTime",vo.getCheckInDate());
            map.put("endTime",vo.getCheckOutDate());

            boolean flag = orderService.validateroomstore(map);
            if(!flag){
                return DtoUtil.returnFail(" 暂时无房","100512");
            }
            List<StoreVO> list = orderService.queryRoomStore(map);
            PreAddOrderVO preAddOrderVO = new PreAddOrderVO();
            preAddOrderVO.setHotelId(vo.getHotelId());
            preAddOrderVO.setRoomId(vo.getRoomId());
            preAddOrderVO.setCheckInDate(vo.getCheckInDate());
            preAddOrderVO.setCheckOutDate(vo.getCheckOutDate());
            preAddOrderVO.setHotelName(hotel.getHotelName());
            preAddOrderVO.setPrice(room.getRoomPrice());
            preAddOrderVO.setCount(vo.getCount());
            preAddOrderVO.setStore(list.get(0).getStore());
            return DtoUtil.returnSuccess("获取成功",preAddOrderVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常","100513");
        }
    }

    @RequestMapping(value="/addhotelorder",method = RequestMethod.POST)
    @ResponseBody
    public Dto addhotelorder(@RequestBody ItripAddHotelOrderVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(vo)){
            return DtoUtil.returnFail("不能提交空，请填写订单信息","100506");
        }

        Map map = new HashMap();
        map.put("hotelId", vo.getHotelId());
        map.put("roomId", vo.getRoomId());
        map.put("startTime",vo.getCheckInDate());
        map.put("endTime",vo.getCheckOutDate());
        map.put("count",vo.getCount());
        try {
            boolean flag = orderService.validateroomstore(map);
            if(!flag){
                return DtoUtil.returnFail("库存不足","100507");
            }
            Integer days = DateUtil.getBetweenDates(vo.getCheckInDate(),vo.getCheckOutDate()).size()-1;
            if(days <= 0){
                return DtoUtil.returnFail("退房日期必须大于入住日期", "100505");
            }
            ItripHotelOrder order = new ItripHotelOrder();
            BeanUtils.copyProperties(vo,order);
            order.setUserId(user.getId());
            order.setCreatedBy(user.getId());
            order.setCreationDate(new Date());
            List<ItripUserLinkUser> linkUserList = vo.getLinkUser();
            StringBuilder linkUserName = new StringBuilder();
            int size = linkUserList.size();
            for (int i = 0; i < size; i++) {
                if (i != size - 1) {
                    linkUserName.append(linkUserList.get(i).getLinkUserName() + ",");
                } else {
                    linkUserName.append(linkUserList.get(i).getLinkUserName());
                }
            }
            order.setLinkUserName(linkUserName.toString());
            order.setBookingDays(days);
            order.setOrderType(1);
            if(token.startsWith("token:PC")){
                order.setBookType(0);
            }else if(token.startsWith("token:MOBILE")){
                order.setBookType(1);
            }else{
                order.setBookType(2);
            }

            order.setOrderStatus(0);

            //生成订单号：机器码 +日期+（MD5）（商品IDs+毫秒数+1000000的随机数）
            StringBuilder md5String = new StringBuilder();
            md5String.append(order.getHotelId());
            md5String.append(order.getRoomId());
            md5String.append(System.currentTimeMillis());
            md5String.append(Math.random() * 1000000);
            String md5 = MD5.getMd5(md5String.toString(), 6);

            //生成订单编号
            StringBuilder orderNo = new StringBuilder();
            orderNo.append("D1000001");
            orderNo.append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
            orderNo.append(md5);
            order.setOrderNo(orderNo.toString());
            //计算订单的总金额
            ItripHotelRoom room = roomService.queryRoomById(vo.getRoomId());

            order.setPayAmount(room.getRoomPrice().multiply(BigDecimal.valueOf(days*vo.getCount())));
            Map map1 = orderService.insertOrder(order);
            return DtoUtil.returnSuccess("添加成功",map1);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("生成订单失败","100505");
        }
    }

    @RequestMapping(value = "/queryOrderById/{orderId}",method = RequestMethod.GET)
    @ResponseBody
    public Dto queryOrderById(@PathVariable Long orderId,HttpServletRequest request){
        try {
            String token = request.getHeader("token");
            ItripUser user = validationToken.getCurrentUser(token);
            if (user == null) {
                return DtoUtil.returnFail("token失效，请重登录", "100000");
            }
            ItripHotelOrder hotelOrder = orderService.getItripHotelOrderById(orderId);
            if(EmptyUtils.isEmpty(hotelOrder)){
                return DtoUtil.returnFail("没有查询到相应订单","100533");
            }

            return DtoUtil.returnSuccess("获取订单成功",hotelOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常","100534");
        }
    }

    @RequestMapping(value="/updateorderstatusandpaytype",method = RequestMethod.POST)
    @ResponseBody
    public Dto updateorderstatusandpaytype(@RequestBody ItripModifyHotelOrderVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(vo)){
            return DtoUtil.returnFail("不能提交空，请填写订单信息","100523");
        }
        ItripHotelOrder order = new ItripHotelOrder();
        order.setId(vo.getId());
        order.setOrderStatus(2);
        order.setCreatedBy(user.getId());
        order.setModifyDate(new Date());
        try {
            orderService.modifyOrder(order);
            return DtoUtil.returnSuccess("修改订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("修改订单失败","100522");
        }
    }

    @RequestMapping(value="/querysuccessorderinfo/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Dto querysuccessorderinfo(@PathVariable Long id,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("id不能为空","100519");
        }
        try {
            ItripHotelOrder order = orderService.getItripHotelOrderById(id);
            if(EmptyUtils.isEmpty(order)){
                return DtoUtil.returnFail("没有查询到相应订单", "100519");
            }
            ItripHotelRoom room = roomService.queryRoomById(order.getRoomId());
            Map map = new HashMap();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("payType", order.getPayType());
            map.put("payAmount", order.getPayAmount());
            map.put("hotelName", order.getHotelName());
            map.put("roomTitle", room.getRoomTitle());
            return DtoUtil.returnSuccess("获取数据成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取数据失败","100520");
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void flushCancleOrder(){
        try {
            boolean flag =  orderService.flushOrderStatus(1);
            System.out.println(flag ? "刷新订单成功" : "刷单失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0/30 * * * ?")
    public void flushOrder(){
        try {
            boolean flag =  orderService.flushOrderStatus(2);
            System.out.println(flag ? "刷新订单成功" : "刷单失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
