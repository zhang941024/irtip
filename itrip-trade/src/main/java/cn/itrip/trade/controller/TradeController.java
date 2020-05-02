package cn.itrip.trade.controller;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.common.EmptyUtils;
import cn.itrip.trade.bean.AlipayBean;
import cn.itrip.trade.bean.AlipayConfig;
import cn.itrip.trade.service.OrderService;
import cn.itrip.trade.service.PayService;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class TradeController {
    @Resource
    private PayService payService;
    @Resource
    private AlipayConfig alipayConfig;
    @Resource
    private OrderService orderService;

    @RequestMapping(value = "/prepay/{orderNo}", method = RequestMethod.GET)
    public String prepay(@PathVariable String orderNo, Model model) {
        try {
            ItripHotelOrder order = orderService.loadItripHotelOrder(orderNo);
            if (EmptyUtils.isNotEmpty(order)) {
                model.addAttribute("hotelName", order.getHotelName());
                model.addAttribute("roomId", order.getRoomId());
                model.addAttribute("count", order.getCount());
                model.addAttribute("payAmount", order.getPayAmount());
                return "pay";
            }
            return "notfound";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST,produces = "text/html;charset=utf-8")
    @ResponseBody
    public String pay(AlipayBean bean) {
        try {
            return payService.alipay(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/return", method = RequestMethod.GET)
    public void success(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        if (signVerified) {
            Long id = orderService.loadItripHotelOrder(out_trade_no).getId();
            response.sendRedirect(String.format(alipayConfig.getPaymentSuccessUrl(),out_trade_no,id));
            //return "ok";
        } else {
            response.sendRedirect(alipayConfig.getPaymentFailureUrl());
            //return "no";
        }
    }

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String trackPaymentStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        //商户订单号
        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        //支付宝交易号
        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

        if (signVerified) {
            //即时到账普通版，那么这时的交易状态值为：  TRADE_FINISHED；如果是即时到账高级版，此时的交易状态值就为：TRADE_SUCCESS
            //收到TRADE_FINISHED请求后，这笔订单就结束了，支付宝不会再主动请求商户网站了；收到TRADE_SUCCESS请求后，后续一定还有至少一条通知记录，即TRADE_FINISHED。
            if (trade_status.equals("TRADE_FINISHED")) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                // 如果有做过处理，不执行商户的业务程序
                if (!orderService.processed(out_trade_no)) {
                    orderService.paySuccess(out_trade_no, 2, trade_no);
                }
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                if (!orderService.processed(out_trade_no)) {
                    orderService.paySuccess(out_trade_no, 2, trade_no);
                }
            }
            //response.getWriter().println("success");
            return "success";
        } else {
            orderService.payFailed(out_trade_no, 1, trade_no);
            //response.getWriter().println("fail");
            return "fail";
        }
    }


}
