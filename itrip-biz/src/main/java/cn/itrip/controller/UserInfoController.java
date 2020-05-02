package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;
import cn.itrip.beans.vo.userinfo.ItripModifyUserLinkUserVO;
import cn.itrip.beans.vo.userinfo.ItripSearchUserLinkUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ValidationToken;
import cn.itrip.service.linkuser.LinkuserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/userinfo")
public class UserInfoController {
    @Resource
    private ValidationToken validationToken;
    @Resource
    private LinkuserService linkuserService;

    @RequestMapping(value = "/adduserlinkuser", method = RequestMethod.POST)
    @ResponseBody
    public Dto addUserLinkuser(@RequestBody ItripAddUserLinkUserVO vo, HttpServletRequest request) {
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if (vo == null) {
            return DtoUtil.returnFail("不能提交空，请填写常用联系人信息", "100412");
        }
        ItripUserLinkUser linkUser = new ItripUserLinkUser();
        BeanUtils.copyProperties(vo, linkUser);
        linkUser.setUserId(currentUser.getId());
        linkUser.setCreatedBy(currentUser.getId());
        linkUser.setCreationDate(new Date());
        try {
            linkuserService.addUserLinkuser(linkUser);
            return DtoUtil.returnSuccess("新增常用联系人成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("新增常用联系人失败", "100411");
        }
    }

    @RequestMapping(value = "/deluserlinkuser", method = RequestMethod.GET)
    @ResponseBody
    public Dto delUserLinkuser(Long[] ids, HttpServletRequest request) {
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if (ids == null || ids.length == 0) {
            return DtoUtil.returnFail("请选择要删除的常用联系人", "100433");
        }
        try {
            List<Long> list = linkuserService.getLinkuser();
            if (list.size() != 0) {
                return DtoUtil.returnFail("所选的常用联系人中有与某条待支付的订单关联的项，无法删除", "100431");
            }
            linkuserService.deleteLinkuser(ids);
            return DtoUtil.returnSuccess("删除常用联系人成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("删除常用联系人失败", "100432");
        }
    }

    @RequestMapping(value="/queryuserlinkuser",method = RequestMethod.POST)
    @ResponseBody
    public Dto queryUserLinkuser(@RequestBody(required = false) ItripSearchUserLinkUserVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        Map map = new HashMap();
        if(EmptyUtils.isNotEmpty(vo)){
            map.put("linkUserName",vo.getLinkUserName());
        }
        try {
            List<ItripUserLinkUser> linkUserList = linkuserService.queryUserLinkuser(map);
            return DtoUtil.returnSuccess("获取常用联系人信息成功",linkUserList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取常用联系人信息失败","100401");
        }
    }

    @RequestMapping(value="/modifyuserlinkuser",method = RequestMethod.POST)
    @ResponseBody
    public Dto modifyUserLinkuser(@RequestBody ItripModifyUserLinkUserVO vo,HttpServletRequest request){
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if(vo == null){
            return DtoUtil.returnFail("不能提交空，请填写常用联系人信息","100422");
        }
        ItripUserLinkUser linkUser = new ItripUserLinkUser();
        BeanUtils.copyProperties(vo,linkUser);
        linkUser.setModifiedBy(currentUser.getId());
        linkUser.setUserId(currentUser.getId());
        try {
            linkuserService.modifyUserLinkuser(linkUser);
            return DtoUtil.returnSuccess("修改常用联系人成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("修改常用联系人失败","100421");
        }
    }
}
