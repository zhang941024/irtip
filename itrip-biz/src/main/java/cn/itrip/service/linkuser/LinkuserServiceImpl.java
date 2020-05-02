package cn.itrip.service.linkuser;

import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.order.ItripOrderLinkUserVo;
import cn.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;
import cn.itrip.dao.orderlinkuser.ItripOrderLinkUserMapper;
import cn.itrip.dao.userlinkuser.ItripUserLinkUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class LinkuserServiceImpl implements LinkuserService {
    @Resource
    private ItripUserLinkUserMapper linkUserMapper;
    @Resource
    private ItripOrderLinkUserMapper orderLinkUserMapper;
    @Override
    public int addUserLinkuser(ItripUserLinkUser linkUser) throws Exception {
        return linkUserMapper.insertItripUserLinkUser(linkUser);
    }

    @Override
    public List<Long> getLinkuser() throws Exception {
        return orderLinkUserMapper.getItripOrderLinkUserIdsByOrder();
    }

    @Override
    public int deleteLinkuser(Long[] ids) throws Exception {
        return linkUserMapper.deleteItripUserLinkUserByIds(ids);
    }

    @Override
    public List<ItripUserLinkUser> queryUserLinkuser(Map map) throws Exception {
        return linkUserMapper.getItripUserLinkUserListByMap(map);
    }

    @Override
    public int modifyUserLinkuser(ItripUserLinkUser linkUser) throws Exception {
        return linkUserMapper.updateItripUserLinkUser(linkUser);
    }


}
